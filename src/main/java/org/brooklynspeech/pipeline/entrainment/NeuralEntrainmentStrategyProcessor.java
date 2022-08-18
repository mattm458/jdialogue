package org.brooklynspeech.pipeline.entrainment;

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

    public NeuralEntrainmentStrategyProcessor(String modelPath, int encodedDim, int numFeatureEncoderLayers,
            int featureEncoderHiddenDim,
            int numDecoderLayers, int decoderHiddenDim) {
        super();

        this.model = Module.load(modelPath);

        this.encodedDim = encodedDim;
        this.numFeatureEncoderLayers = numFeatureEncoderLayers;
        this.featureEncoderHiddenDim = featureEncoderHiddenDim;
        this.numDecoderLayers = numDecoderLayers;
        this.decoderHiddenDim = decoderHiddenDim;
    }

    private static IValue chunkFeaturesToIValue(final NeuralEntrainmentChunk chunk) {
        final int featureDim = NeuralEntrainmentChunk.featureKeys.length;

        final float features[] = new float[featureDim];
        for (int i = 0; i < featureDim; i++) {
            String featureKey = NeuralEntrainmentChunk.featureKeys[i];
            features[i] = chunk.getNormalizedFeature(featureKey);
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

        return IValue.from(Tensor.fromBlob(historyFlattened, new long[] { 1, timesteps, encodedDim }));
    }

    private static IValue getHistoryMaskIValue(int length) {
        byte[] mask = new byte[length];
        mask[length - 1] = 1;

        return IValue.from(Tensor.fromBlob(mask, new long[] { 1, length }));
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

    private static IValue embeddingsToIValue(float[][] embeddings) {
        final int timesteps = embeddings.length;
        final int embeddingsDim = embeddings[0].length;

        float[] embeddingsFlattened = new float[timesteps * embeddingsDim];

        for (int timestep = 0; timestep < embeddings.length; timestep++) {
            for (int i = 0; i < embeddingsDim; i++) {
                embeddingsFlattened[(timestep * embeddingsDim) + i] = embeddings[timestep][i];
            }
        }

        return IValue.from(Tensor.fromBlob(embeddingsFlattened, new long[] { 1, timesteps, embeddingsDim }));
    }

    @Override
    public final ChunkMessage<NeuralEntrainmentChunk, NeuralEntrainmentConversation> doProcess(
            ChunkMessage<NeuralEntrainmentChunk, NeuralEntrainmentConversation> messages) {

        NeuralEntrainmentChunk chunk = messages.chunk;
        NeuralEntrainmentConversation conversation = messages.conversation;

        IValue timestep = IValue.from(0);
        IValue featureInput = chunkFeaturesToIValue(messages.chunk);
        IValue featureHistory = historyToIValue(conversation.getEncodedHistory(), this.encodedDim);
        IValue featureMask = getHistoryMaskIValue(conversation.getEncodedHistory().length + 1);
        IValue featureEncoderHidden = hiddenToIValue(conversation.getFeatureEncoderHidden(),
                this.numFeatureEncoderLayers, this.featureEncoderHiddenDim);
        IValue decoderHidden = hiddenToIValue(conversation.getDecoderHidden(),
                this.numDecoderLayers, this.decoderHiddenDim);
        IValue speaker = speakerToIValue(chunk.getSpeaker());
        IValue predIdxs = IValue.from(Tensor.fromBlob(new long[1], new long[] { 1 }));
        IValue embeddingInput = null;
        IValue embeddingLen = null;
        IValue predEmbeddingInput = null;
        IValue predEmbeddingLen = null;

        this.model.forward(timestep, featureInput,
                featureHistory, featureMask,
                featureEncoderHidden, decoderHidden, speaker, predIdxs, embeddingInput,
                embeddingLen,
                predEmbeddingInput, predEmbeddingLen);
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
