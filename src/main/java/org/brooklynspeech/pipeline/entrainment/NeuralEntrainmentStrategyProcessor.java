package org.brooklynspeech.pipeline.entrainment;

import org.brooklynspeech.pipeline.Processor;
import org.brooklynspeech.pipeline.data.Features;
import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;

public class NeuralEntrainmentStrategyProcessor extends Processor<Features, Features> {

    private final Module model;

    private final int featureDim;
    private final int maxLength;
    private final int featureEncoderHiddenDim;
    private final int encodedDim;
    private final int featureEncoderLayers;
    private final int decoderHiddenDim;
    private final int decoderLayers;

    private final boolean hasEmbeddings;
    private final int embeddingDim;

    public NeuralEntrainmentStrategyProcessor(String modelPath, int featureDim, int maxLength,
            int featureEncoderHiddenDim, int encodedDim, int featureEncoderLayers, int decoderHiddenDim,
            int decoderLayers, boolean hasEmbeddings, int embeddingDim) {
        this.model = Module.load(modelPath);

        this.featureDim = featureDim;
        this.maxLength = maxLength;
        this.encodedDim = encodedDim;

        this.featureEncoderLayers = featureEncoderLayers;
        this.featureEncoderHiddenDim = featureEncoderHiddenDim;
        this.decoderLayers = decoderLayers;
        this.decoderHiddenDim = decoderHiddenDim;

        this.hasEmbeddings = hasEmbeddings;
        this.embeddingDim = embeddingDim;
    }

    public NeuralEntrainmentStrategyProcessor() {
        this("entrainer.pt", 7, 100, 256, 256, 2, 256, 2, true, 300);
    }

    private static IValue getHidden(int batchSize, int numLayers, int hiddenSize) {
        IValue[] hidden = new IValue[numLayers];

        for (int i = 0; i < numLayers; i++) {
            final IValue h = IValue.from(Tensor.fromBlob(new float[hiddenSize], new long[] { batchSize, hiddenSize }));
            final IValue c = IValue.from(Tensor.fromBlob(new float[hiddenSize], new long[] { batchSize, hiddenSize }));

            final IValue tuple = IValue.tupleFrom(h, c);

            hidden[i] = tuple;
        }

        return IValue.listFrom(hidden);
    }

    @Override
    public Features doProcess(Features ourFeatures) {
        final int batchSize = 1;
        final int predIdxsSize = 1;
        final int textSize = 100;
        final int predTextSize = 100;

        final IValue timestep = IValue.from(0);

        final float[] featureInput_val = new float[batchSize * this.featureDim];
        final IValue featureInput = IValue.from(Tensor.fromBlob(featureInput_val, new long[] { batchSize, 7 }));

        final float[] featureHistory_val = new float[this.encodedDim * this.maxLength];
        final IValue featureHistory = IValue
                .from(Tensor.fromBlob(featureHistory_val, new long[] { batchSize,
                        this.maxLength, this.encodedDim }));

        final byte[] featureMask_val = new byte[batchSize * this.maxLength];
        final IValue featureMask = IValue
                .from(Tensor.fromBlob(featureMask_val, new long[] { batchSize,
                        this.maxLength, 1 }));

        final IValue featureEncoderHidden = getHidden(batchSize, this.featureEncoderLayers, this.featureEncoderHiddenDim);
        final IValue decoderHidden = getHidden(batchSize, this.decoderLayers, this.decoderHiddenDim);

        final float[] speaker_val = new float[batchSize * 2];
        final IValue speaker = IValue.from(Tensor.fromBlob(speaker_val, new long[] { batchSize, 2 }));

        final long[] predIdxs_val = new long[predIdxsSize];
        final IValue predIdxs = IValue.from(Tensor.fromBlob(predIdxs_val, new long[] { predIdxsSize }));

        final float[] embeddingInput_val = new float[batchSize * textSize * this.embeddingDim];
        final IValue embeddingInput = IValue
                .from(Tensor.fromBlob(embeddingInput_val, new long[] { batchSize, textSize, this.embeddingDim }));

        final int[] embeddingLen_val = new int[batchSize];
        embeddingLen_val[0] = 10;
        final IValue embeddingLen = IValue.from(Tensor.fromBlob(embeddingLen_val, new long[] { batchSize }));

        final float[] predEmbeddingInput_val = new float[batchSize * predTextSize * this.embeddingDim];
        final IValue predEmbeddingInput = IValue.from(
                Tensor.fromBlob(predEmbeddingInput_val, new long[] { batchSize, predTextSize, this.embeddingDim }));

        final int[] predEmbeddingLen_val = new int[batchSize];
        predEmbeddingLen_val[0] = 10;
        final IValue predEmbeddingLen = IValue.from(Tensor.fromBlob(predEmbeddingLen_val, new long[] { batchSize }));

        final IValue output = this.model.forward(timestep, featureInput,
                featureHistory, featureMask,
                featureEncoderHidden, decoderHidden, speaker, predIdxs, embeddingInput,
                embeddingLen,
                predEmbeddingInput, predEmbeddingLen);

        
        IValue[] outTuple = output.toTuple();
        System.out.println(outTuple[0].toTensor().getDataAsFloatArray()[0]);

        return ourFeatures;
    }
}
