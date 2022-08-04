package org.brooklynspeech.pipeline.entrainment;

import java.util.concurrent.Semaphore;

import org.brooklynspeech.pipeline.Processor;
import org.brooklynspeech.pipeline.data.Context;
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

    private static IValue getFeatureInput(int batchSize, int featureDim) {
        final float[] featureInput_val = new float[batchSize * featureDim];
        return IValue.from(Tensor.fromBlob(featureInput_val, new long[] { batchSize, featureDim }));
    }

    public static IValue getFeatureHistory(int batchSize, int maxLength, int encodedDim) {
        final float[] featureInput_val = new float[batchSize * maxLength * encodedDim];
        return IValue.from(Tensor.fromBlob(featureInput_val, new long[] { batchSize, maxLength, encodedDim }));
    }

    private static IValue getFeatureMask(int batchSize, int maxLength) {
        final byte[] featureMask_val = new byte[batchSize * maxLength];
        return IValue.from(Tensor.fromBlob(featureMask_val, new long[] { batchSize, maxLength, 1 }));
    }

    public static IValue getHidden(int batchSize, int numLayers, int hiddenSize) {
        IValue[] hidden = new IValue[numLayers];

        for (int i = 0; i < numLayers; i++) {
            final IValue h = IValue.from(Tensor.fromBlob(new float[hiddenSize], new long[] { batchSize, hiddenSize }));
            final IValue c = IValue.from(Tensor.fromBlob(new float[hiddenSize], new long[] { batchSize, hiddenSize }));

            hidden[i] = IValue.tupleFrom(h, c);
        }

        return IValue.listFrom(hidden);
    }

    private static IValue getSpeaker(int batchSize, Features.Speaker speaker) {
        final float[] speaker_val = new float[batchSize * 2];

        if (speaker == Features.Speaker.partner) {
            speaker_val[0] = 1.0f;
        } else {
            speaker_val[1] = 1.0f;
        }

        System.out.println(speaker_val[0] + " " + speaker_val[1]);

        return IValue.from(Tensor.fromBlob(speaker_val, new long[] { batchSize, 2 }));
    }

    private static IValue getPredIdxs(int batchSize, int predIdxsSize) {
        final long[] predIdxs_val = new long[predIdxsSize];
        return IValue.from(Tensor.fromBlob(predIdxs_val, new long[] { predIdxsSize }));

    }

    private static IValue getEmbedding(int batchSize, int textSize, int embeddingDim) {
        final float[] embeddingInput_val = new float[batchSize * textSize * embeddingDim];
        return IValue.from(Tensor.fromBlob(embeddingInput_val, new long[] { batchSize, textSize, embeddingDim }));
    }

    private static IValue getEmbeddingLen(int batchSize) {
        final long[] embeddingLen_val = new long[batchSize];
        embeddingLen_val[0] = 10;
        return IValue.from(Tensor.fromBlob(embeddingLen_val, new long[] { batchSize }));
    }

    @Override
    public Features doProcess(Features features) {
        Context context = features.getContext();

        final int batchSize = 1;
        final int predIdxsSize = 1;
        final int textSize = 100;
        final int predTextSize = 100;

        final IValue timestep = IValue.from(0);
        final IValue featureInput = getFeatureInput(batchSize, this.featureDim);
        final IValue featureHistory = context.getTorchFeature("featureHistory");
        final IValue featureMask = getFeatureMask(batchSize, this.maxLength);
        final IValue featureEncoderHidden = context.getTorchFeature("featureEncoderHidden");
        final IValue decoderHidden = context.getTorchFeature("decoderHidden");
        final IValue speaker = getSpeaker(batchSize, features.getSpeaker());
        final IValue predIdxs = getPredIdxs(batchSize, predIdxsSize);
        final IValue embeddingInput = getEmbedding(batchSize, textSize, this.embeddingDim);
        final IValue embeddingLen = getEmbeddingLen(batchSize);
        final IValue predEmbeddingInput = getEmbedding(batchSize, predTextSize, this.embeddingDim);
        final IValue predEmbeddingLen = getEmbeddingLen(batchSize);

        this.model.forward(timestep, featureInput,
                featureHistory, featureMask,
                featureEncoderHidden, decoderHidden, speaker, predIdxs, embeddingInput,
                embeddingLen,
                predEmbeddingInput, predEmbeddingLen);

        return features;
    }
}
