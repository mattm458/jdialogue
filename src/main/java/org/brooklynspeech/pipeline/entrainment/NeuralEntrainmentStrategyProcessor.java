package org.brooklynspeech.pipeline.entrainment;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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

    private static IValue chunkFeaturesToIValue(final float[] features) {
        final int featureDim = NeuralEntrainmentChunk.featureKeys.length;

        return IValue.from(Tensor.fromBlob(features, new long[] { 1, featureDim }));
    }

    private static IValue historyToIValue(final float[][] history, int encodedDim) {
        final int timesteps = history.length;
        float[] historyFlattened = new float[timesteps * encodedDim];

        for (int timestep = 0; timestep < timesteps; timestep++) {
            for (int encodedIdx = 0; encodedIdx < encodedDim; encodedIdx++) {
                final int i = (timestep * encodedDim) + encodedIdx;

                historyFlattened[i] = history[timestep][encodedIdx];
            }
        }

        return IValue.from(Tensor.fromBlob(historyFlattened, new long[] { 1, timesteps, encodedDim }));
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

    private static float[][][] expandHidden(IValue hidden, final int numLayers, final int hiddenDim) {
        float[][][] expandedHidden = new float[numLayers][2][hiddenDim];

        IValue[] layers = hidden.toList();

        for (int i = 0; i < numLayers; i++) {
            IValue layer = layers[i];
            IValue[] lstmValues = layer.toTuple();

            for (int j = 0; j < 2; j++) {
                IValue h = lstmValues[j];
                float[] hVal = h.toTensor().getDataAsFloatArray();

                for (int k = 0; k < hiddenDim; k++) {
                    expandedHidden[i][j][k] = hVal[k];
                }
            }
        }

        return expandedHidden;
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

    private static float[][] expandHistory(float[] history, int historyLength, int encodedDim) {
        final float[][] expandedHistory = new float[historyLength + 1][encodedDim];

        int flattenedIdx = 0;
        for (int i = 0; i < historyLength; i++) {
            for (int j = 0; j < encodedDim; j++) {
                expandedHistory[i][j] = history[flattenedIdx];
                flattenedIdx += 1;
            }
        }

        return expandedHistory;
    }

    @Override
    public final ChunkMessage<NeuralEntrainmentChunk, NeuralEntrainmentConversation> doProcess(
            ChunkMessage<NeuralEntrainmentChunk, NeuralEntrainmentConversation> messages) {

        NeuralEntrainmentChunk chunk = messages.chunk;
        NeuralEntrainmentConversation conversation = messages.conversation;

        List<NeuralEntrainmentChunk> chunkHistory = getChunkHistory(chunk, conversation);

        float[][] history = conversation.getEncodedHistory();
        if (history.length == 0) {
            history = new float[1][this.encodedDim];
        }

        float[] features = new float[NeuralEntrainmentChunk.featureKeys.length];
        for (int i = 0; i < NeuralEntrainmentChunk.featureKeys.length; i++) {
            features[i] = chunk.getNormalizedFeature(NeuralEntrainmentChunk.featureKeys[i], 0.0f);
        }

        IValue featureInput = chunkFeaturesToIValue(features);

        IValue embeddingInput = getEmbeddingIValue(chunk, this.embeddingDim);
        IValue embeddingLen = getEmbeddingLenIValue(chunk);
        IValue speaker = speakerToIValue(chunk.getSpeaker());
        IValue featureEncoderHidden = hiddenToIValue(conversation.getFeatureEncoderHidden(),
                this.numFeatureEncoderLayers, this.featureEncoderHiddenDim);
        IValue decoderHidden = hiddenToIValue(conversation.getDecoderHidden(),
                this.numDecoderLayers, this.decoderHiddenDim);
        IValue featureHistory = historyToIValue(history, this.encodedDim);

        IValue batchMask = IValue.from(Tensor.fromBlob(new byte[] { 1 }, new long[] { 1 }));
        IValue embeddingsEncodeMask = IValue.from(Tensor.fromBlob(new byte[] { 1 }, new long[] { 1 }));
        IValue featureEncodeMask = null;
        IValue predictMask = null;
        IValue idx = IValue.from(Tensor.fromBlob(new long[] { chunkHistory.size() - 1 }, new long[] { 1 }));

        if (chunk.getSpeaker() == NeuralEntrainmentChunk.Speaker.partner) {
            featureEncodeMask = IValue.from(Tensor.fromBlob(new byte[] { 1 }, new long[] { 1 }));
            predictMask = IValue.from(Tensor.fromBlob(new byte[] { 0 }, new long[] { 1 }));
        } else {
            featureEncodeMask = IValue.from(Tensor.fromBlob(new byte[] { 0 }, new long[] { 1 }));
            predictMask = IValue.from(Tensor.fromBlob(new byte[] { 1 }, new long[] { 1 }));
        }

        IValue results = this.model.forward(
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

        if (chunk.getSpeaker() == NeuralEntrainmentChunk.Speaker.partner) {
            // If this was a partner utterance, we can expand the history and hidden tensors
            // to save for another turn.
            float[][] expandedHistory = expandHistory(featureHistory.toTensor().getDataAsFloatArray(), history.length,
                    this.encodedDim);
            conversation.setEncodedHistory(expandedHistory);

            float[][][] expandedFeatureEncoderHidden = expandHidden(
                    featureEncoderHidden, this.numFeatureEncoderLayers,
                    this.featureEncoderHiddenDim);
            conversation.setFeatureEncoderHidden(expandedFeatureEncoderHidden);

        } else {
            /*
             * If this was our utterance, we have to carry out these additional steps:
             * 
             * 1. Unpack the results from the output tensor.
             * 2. Save the results as normalized features in the chunk.
             * 3. Unpack and save the decoder hidden tensors
             * 3. Create a new set of input tensors and process the features again
             * to save them in the encoded history.
             */
            float[] resultsExpanded = results.toTuple()[0].toTensor().getDataAsFloatArray();
            for (int i = 0; i < NeuralEntrainmentChunk.featureKeys.length; i++) {
                chunk.setNormalizedFeature(NeuralEntrainmentChunk.featureKeys[i], resultsExpanded[i]);
            }
            featureInput = chunkFeaturesToIValue(resultsExpanded);

            float[][][] expandedDecoderHidden = expandHidden(
                    decoderHidden, this.numDecoderLayers,
                    this.decoderHiddenDim);
            conversation.setDecoderHidden(expandedDecoderHidden);
            decoderHidden = hiddenToIValue(expandedDecoderHidden, this.numDecoderLayers, this.decoderHiddenDim);
            featureEncodeMask = IValue.from(Tensor.fromBlob(new byte[] { 1 }, new long[] { 1 }));
            predictMask = IValue.from(Tensor.fromBlob(new byte[] { 0 }, new long[] { 1 }));

            results = this.model.forward(
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

            float[][] expandedHistory = expandHistory(featureHistory.toTensor().getDataAsFloatArray(), history.length,
                    this.encodedDim);
            conversation.setEncodedHistory(expandedHistory);
            
            float[][][] expandedFeatureEncoderHidden = expandHidden(
                    featureEncoderHidden, this.numFeatureEncoderLayers,
                    this.featureEncoderHiddenDim);
            conversation.setFeatureEncoderHidden(expandedFeatureEncoderHidden);
        }

        return messages;
    }
}
