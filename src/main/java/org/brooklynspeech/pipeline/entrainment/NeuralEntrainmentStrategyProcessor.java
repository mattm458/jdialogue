package org.brooklynspeech.pipeline.entrainment;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.brooklynspeech.pipeline.core.PassthroughStreamProcessor;
import org.brooklynspeech.pipeline.data.Chunk.Speaker;
import org.brooklynspeech.pipeline.data.ChunkMessage;
import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;

public class NeuralEntrainmentStrategyProcessor extends
        PassthroughStreamProcessor<ChunkMessage<NeuralEntrainmentChunk, NeuralEntrainmentConversation>> {

    private final Module model;

    private final int encodedDim;
    private final int numFeatureEncoderLayers;
    private final int featureEncoderHiddenDim;
    private final int numDecoderLayers;
    private final int decoderHiddenDim;
    private final int embeddingDim;

    public NeuralEntrainmentStrategyProcessor(String modelPath, int encodedDim,
            int numFeatureEncoderLayers,
            int featureEncoderHiddenDim,
            int numDecoderLayers, int decoderHiddenDim, int embeddingDim) {
        super();
        this.model = Module.load(modelPath);

        this.encodedDim = encodedDim;
        this.numFeatureEncoderLayers = numFeatureEncoderLayers;
        this.featureEncoderHiddenDim = featureEncoderHiddenDim;
        this.numDecoderLayers = numDecoderLayers;
        this.decoderHiddenDim = decoderHiddenDim;
        this.embeddingDim = embeddingDim;
    }

    private static IValue chunkFeaturesToIValue(final NeuralEntrainmentChunk chunk) {
        final int featureDim = NeuralEntrainmentChunk.featureKeys.length;

        final float features[] = new float[featureDim];
        for (int i = 0; i < featureDim; i++) {
            String featureKey = NeuralEntrainmentChunk.featureKeys[i];
            features[i] = chunk.getNormalizedFeature(featureKey, 0.0f);
        }

        return IValue.from(Tensor.fromBlob(features, new long[] { 1, featureDim }));
    }

    private static IValue historyToIValue(final float[][] history, int encodedDim) {
        if (history == null) {
            return IValue.from(Tensor.fromBlob(new float[encodedDim], new long[] { 1, 1, encodedDim }));
        }

        final int timesteps = history.length;

        // Add an extra timestep for the ouput of what we're currently encoding
        float[] historyFlattened = new float[(timesteps + 1) * encodedDim];

        for (int timestep = 0; timestep < timesteps; timestep++) {
            for (int encodedIdx = 0; encodedIdx < encodedDim; encodedIdx++) {
                final int i = (timestep * encodedDim) + encodedIdx;

                historyFlattened[i] = history[timestep][encodedIdx];
            }
        }

        return IValue.from(Tensor.fromBlob(historyFlattened, new long[] { 1, timesteps + 1, encodedDim }));
    }

    private static IValue hiddenToIValue(float[][][] hidden, final int numLayers, final int hiddenDim) {
        IValue[] layers = new IValue[numLayers];

        for (int layer = 0; layer < numLayers; layer++) {
            float[] h = new float[hiddenDim];
            float[] c = new float[hiddenDim];

            if (hidden != null) {
                for (int i = 0; i < hiddenDim; i++) {
                    h[i] = hidden[layer][0][i];
                    c[i] = hidden[layer][1][i];
                }
            }

            IValue hIValue = IValue.from(Tensor.fromBlob(h, new long[] { 1, hiddenDim }));
            IValue cIValue = IValue.from(Tensor.fromBlob(h, new long[] { 1, hiddenDim }));
            IValue hiddenTuple = IValue.tupleFrom(hIValue, cIValue);

            layers[layer] = hiddenTuple;
        }

        return IValue.listFrom(layers);
    }

    private static IValue speakerToIValue(Speaker speaker) {
        if (speaker == Speaker.partner) {
            return IValue.from(Tensor.fromBlob(new float[] { 1.0f, 0.0f }, new long[] { 1, 2 }));
        } else {
            return IValue.from(Tensor.fromBlob(new float[] { 0.0f, 1.0f }, new long[] { 1, 2 }));
        }
    }

    private static IValue getEmbeddingIValue(NeuralEntrainmentChunk chunk, int embeddingDim) {
        float[][] embeddings = chunk.getEmbeddings();

        int embeddingIdx = 0;
        float[] embeddingsFlattened = new float[embeddings.length * embeddingDim];

        for (int i = 0; i < embeddings.length; i++) {
            for (int j = 0; j < embeddingDim; j++) {
                embeddingsFlattened[embeddingIdx] = embeddings[i][j];
                embeddingIdx++;
            }
        }

        return IValue.from(Tensor.fromBlob(embeddingsFlattened, new long[] { 1, embeddings.length, embeddingDim }));
    }

    private static IValue getEmbeddingLenIValue(NeuralEntrainmentChunk chunk) {
        float[][] embeddings = chunk.getEmbeddings();

        return IValue.from(Tensor.fromBlob(new long[] { embeddings.length }, new long[] { 1 }));
    }

    private static List<NeuralEntrainmentChunk> getChunkHistory(NeuralEntrainmentChunk chunk,
            NeuralEntrainmentConversation conversation) {
        Iterator<NeuralEntrainmentChunk> iterator = conversation.getFeaturesIterator();

        NeuralEntrainmentChunk next;
        LinkedList<NeuralEntrainmentChunk> chunks = new LinkedList<>();

        while (iterator.hasNext() && (next = iterator.next()) != chunk) {
            chunks.add(next);
        }

        chunks.add(chunk);

        return chunks;
    }

    @Override
    public final ChunkMessage<NeuralEntrainmentChunk, NeuralEntrainmentConversation> doProcess(
            ChunkMessage<NeuralEntrainmentChunk, NeuralEntrainmentConversation> messages) {

        NeuralEntrainmentChunk chunk = messages.chunk;
        NeuralEntrainmentConversation conversation = messages.conversation;

        List<NeuralEntrainmentChunk> chunkHistory = getChunkHistory(chunk, conversation);

        IValue featureInput = chunkFeaturesToIValue(messages.chunk);
        IValue embeddingInput = getEmbeddingIValue(chunk, this.embeddingDim);
        IValue embeddingLen = getEmbeddingLenIValue(chunk);
        IValue speaker = speakerToIValue(chunk.getSpeaker());
        IValue featureEncoderHidden = hiddenToIValue(conversation.getFeatureEncoderHidden(),
                this.numFeatureEncoderLayers, this.featureEncoderHiddenDim);
        IValue decoderHidden = hiddenToIValue(conversation.getDecoderHidden(),
                this.numDecoderLayers, this.decoderHiddenDim);
        IValue featureHistory = historyToIValue(conversation.getEncodedHistory(), this.encodedDim);

        IValue batchMask = IValue.from(Tensor.fromBlob(new byte[] { 1 }, new long[] { 1 }));
        IValue embeddingsEncodeMask = IValue.from(Tensor.fromBlob(new byte[] { 1 }, new long[] { 1 }));
        IValue featureEncodeMask = null;
        IValue predictMask = null;
        IValue idx = IValue.from(Tensor.fromBlob(new long[] { chunkHistory.size() - 1 }, new long[] { 1 }));

        if (chunk.getSpeaker() == NeuralEntrainmentChunk.Speaker.partner) {
            System.out.println("NeuralEntrainmentStrategyProcessor: Partner");
            featureEncodeMask = IValue.from(Tensor.fromBlob(new byte[] { 1 }, new long[] { 1 }));
            predictMask = IValue.from(Tensor.fromBlob(new byte[] { 0 }, new long[] { 1 }));
        } else {
            System.out.println("NeuralEntrainmentStrategyProcessor: Us");
            featureEncodeMask = IValue.from(Tensor.fromBlob(new byte[] { 0 }, new long[] { 1 }));
            predictMask = IValue.from(Tensor.fromBlob(new byte[] { 1 }, new long[] { 1 }));
        }

        // IValue featureMask =
        // getHistoryMaskIValue(conversation.getEncodedHistory().length + 1);

        this.model.forward(
                featureInput, // acoutsic_prosodic_features: Tensor
                embeddingInput, // embeddings: Tensor
                embeddingLen, // embeddings_len: Tensor
                speaker, // speakers: Tensor
                featureEncoderHidden, // encoder_hidden: List[Tuple[Tensor, Tensor]]
                decoderHidden, // decoder_hidden: List[Tuple[Tensor, Tensor]]
                featureHistory, // history: Tensor
                batchMask, // batch_mask: Tensor
                embeddingsEncodeMask, // embeddings_encode_mask: Tensor
                featureEncodeMask, // feature_encode_mask: Tensor
                predictMask, // predict_mask: Tensor
                idx // idx: Tensor
        );

        return messages;
    }

    // private final Module model;

    // private final int featureDim;
    // private final int maxLength;
    // private final int encodedDim;
    // private final int decoderHiddenDim;
    // private final int decoderLayers;

    // private final boolean hasEmbeddings;
    // private final int embeddingDim;

    // public NeuralEntrainmentStrategyProcessor(String modelPath, int featureDim,
    // int featureEncoderHiddenDim, int encodedDim, int featureEncoderLayers, int
    // decoderHiddenDim,
    // int decoderLayers, boolean hasEmbeddings, int embeddingDim) {

    // super();

    // this.featureDim = featureDim;
    // this.encodedDim = encodedDim;

    // this.decoderLayers = decoderLayers;
    // this.decoderHiddenDim = decoderHiddenDim;

    // this.hasEmbeddings = hasEmbeddings;
    // this.embeddingDim = embeddingDim;

    // int batchSize = 1;

    // final IValue speaker = getSpeaker(batchSize, Chunk.Speaker.us);
    // final IValue predIdxs = getPredIdxs(batchSize, 1);
    // final IValue embeddingInput = getEmbedding(batchSize, 100,
    // this.embeddingDim);
    // final IValue embeddingLen = getEmbeddingLen(batchSize);
    // final IValue predEmbeddingInput = getEmbedding(batchSize, 100,
    // this.embeddingDim);
    // final IValue predEmbeddingLen = getEmbeddingLen(batchSize);

    // this.model.forward(timestep, featureInput,
    // featureHistory, featureMask,
    // featureEncoderHidden, decoderHidden, speaker, predIdxs, embeddingInput,
    // embeddingLen,
    // predEmbeddingInput, predEmbeddingLen);
    // }

    // public NeuralEntrainmentStrategyProcessor() {
    // this("entrainer.pt", 7, 100, 256, 256, 2, 256, 2, true, 300);
    // }

    // private static IValue getFeatureInput(int batchSize, int featureDim) {
    // final float[] featureInput_val = new float[batchSize * featureDim];
    // return IValue.from(Tensor.fromBlob(featureInput_val, new long[] { batchSize,
    // featureDim }));
    // }

    // public static IValue getFeatureHistory(int batchSize, int maxLength, int
    // encodedDim) {
    // final float[] featureInput_val = new float[batchSize * maxLength *
    // encodedDim];
    // return IValue.from(Tensor.fromBlob(featureInput_val, new long[] { batchSize,
    // maxLength, encodedDim }));
    // }

    // private static IValue getFeatureMask(int batchSize, int maxLength) {
    // final byte[] featureMask_val = new byte[batchSize * maxLength];
    // return IValue.from(Tensor.fromBlob(featureMask_val, new long[] { batchSize,
    // maxLength, 1 }));
    // }

    // public static IValue getHidden(int batchSize, int numLayers, int hiddenSize)
    // {
    // IValue[] hidden = new IValue[numLayers];

    // for (int i = 0; i < numLayers; i++) {
    // final IValue h = IValue.from(Tensor.fromBlob(new float[hiddenSize], new
    // long[] { batchSize, hiddenSize }));
    // final IValue c = IValue.from(Tensor.fromBlob(new float[hiddenSize], new
    // long[] { batchSize, hiddenSize }));

    // hidden[i] = IValue.tupleFrom(h, c);
    // }

    // return IValue.listFrom(hidden);
    // }

    // private static IValue getSpeaker(int batchSize, Chunk.Speaker speaker) {
    // final float[] speaker_val = new float[batchSize * 2];

    // if (speaker == Chunk.Speaker.partner) {
    // speaker_val[0] = 1.0f;
    // } else {
    // speaker_val[1] = 1.0f;
    // }

    // System.out.println(speaker_val[0] + " " + speaker_val[1]);

    // return IValue.from(Tensor.fromBlob(speaker_val, new long[] { batchSize, 2
    // }));
    // }

    // private static IValue getPredIdxs(int batchSize, int predIdxsSize) {
    // final long[] predIdxs_val = new long[predIdxsSize];
    // return IValue.from(Tensor.fromBlob(predIdxs_val, new long[] { predIdxsSize
    // }));

    // }

    // private static IValue getEmbedding(int batchSize, int textSize, int
    // embeddingDim) {
    // final float[] embeddingInput_val = new float[batchSize * textSize *
    // embeddingDim];
    // return IValue.from(Tensor.fromBlob(embeddingInput_val, new long[] {
    // batchSize, textSize, embeddingDim }));
    // }

    // private static IValue getEmbeddingLen(int batchSize) {
    // final long[] embeddingLen_val = new long[batchSize];
    // embeddingLen_val[0] = 10;
    // return IValue.from(Tensor.fromBlob(embeddingLen_val, new long[] { batchSize
    // }));
    // }

    // @Override
    // public ChunkMessage<ChunkType, ConversationType>
    // doProcess(ChunkMessage<ChunkType, ConversationType> message) {
    // ChunkType chunk = message.chunk;

    // final int batchSize = 1;
    // final int predIdxsSize = 1;
    // final int textSize = 100;
    // final int predTextSize = 100;

    // final IValue timestep = IValue.from(0);
    // final IValue featureInput = getFeatureInput(batchSize, this.featureDim);
    // final IValue featureHistory = getFeatureHistory(batchSize, this.maxLength,
    // this.encodedDim);
    // final IValue featureMask = getFeatureMask(batchSize, this.maxLength);
    // final IValue featureEncoderHidden = getHidden(batchSize,
    // this.featureEncoderLayers,
    // this.featureEncoderHiddenDim);
    // final IValue decoderHidden = getHidden(batchSize, this.decoderLayers,
    // this.decoderHiddenDim);
    // final IValue speaker = getSpeaker(batchSize, chunk.getSpeaker());
    // final IValue predIdxs = getPredIdxs(batchSize, predIdxsSize);
    // final IValue embeddingInput = getEmbedding(batchSize, textSize,
    // this.embeddingDim);
    // final IValue embeddingLen = getEmbeddingLen(batchSize);
    // final IValue predEmbeddingInput = getEmbedding(batchSize, predTextSize,
    // this.embeddingDim);
    // final IValue predEmbeddingLen = getEmbeddingLen(batchSize);

    // return message;
    // }
}
