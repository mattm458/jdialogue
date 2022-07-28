package org.brooklynspeech.pipeline_old.message;

import java.util.ArrayList;
import java.util.Arrays;
import org.pytorch.IValue;
import org.pytorch.Tensor;

public class Context {

    private final int maxLength;
    private final int numFeatures;
    private final int featureHiddenDim;

    private final ArrayList<Chunk> chunks = new ArrayList<>();
    private final IValue featureHistory;
    private final IValue dummyMask;

    private IValue featureEncoderHidden, decoderHidden;

    public Context(int maxLength, int numFeatures, int featureHiddenDim, int numEncoderLayers, int decoderHiddenDim, int numDecoderLayers) {
        this.maxLength = maxLength;
        this.numFeatures = numFeatures;
        this.featureHiddenDim = featureHiddenDim;

        this.featureHistory = IValue.from(Tensor.fromBlob(new float[maxLength * numFeatures], new long[]{1, maxLength, numFeatures}));

        final double[] dummyMaskValues = new double[this.maxLength * this.numFeatures];
        Arrays.fill(dummyMaskValues, 1.0);
        this.dummyMask = IValue.from(Tensor.fromBlob(dummyMaskValues, new long[]{1, maxLength, numFeatures}));

        IValue[] featureEncoderHiddenIValues = new IValue[numEncoderLayers];
        for (int i = 0; i < numEncoderLayers; i++) {
            featureEncoderHiddenIValues[i] = IValue.tupleFrom(
                    IValue.from(Tensor.fromBlob(new float[featureHiddenDim], new long[]{1, featureHiddenDim})),
                    IValue.from(Tensor.fromBlob(new float[featureHiddenDim], new long[]{1, featureHiddenDim}))
            );
        }
        this.featureEncoderHidden = IValue.listFrom(featureEncoderHiddenIValues);

        IValue[] decoderHiddenIValues = new IValue[numDecoderLayers];
        for (int i = 0; i < numDecoderLayers; i++) {
            decoderHiddenIValues[i] = IValue.tupleFrom(
                    IValue.from(Tensor.fromBlob(new float[decoderHiddenDim], new long[]{1, featureHiddenDim})),
                    IValue.from(Tensor.fromBlob(new float[decoderHiddenDim], new long[]{1, featureHiddenDim}))
            );
        }
        this.decoderHidden = IValue.listFrom(decoderHiddenIValues);
    }

    public void addChunk(Chunk chunk) {
        this.chunks.add(chunk);
    }

    public IValue getFeatureHistory() {
        return this.featureHistory;
    }

    public IValue getFeatureMask() {
        return this.dummyMask;
    }

    public IValue getFeatureEncoderHidden() {
        return this.featureEncoderHidden;
    }

    public IValue getDecoderHidden() {
        return this.decoderHidden;
    }
}
