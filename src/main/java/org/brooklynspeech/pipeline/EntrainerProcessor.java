package org.brooklynspeech.pipeline;

import org.brooklynspeech.pipeline_old.message.Chunk;
import org.brooklynspeech.pipeline_old.message.Context;
import org.brooklynspeech.pipeline_old.message.Feature;
import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;

public class EntrainerProcessor extends Processor<Chunk, Chunk> {

    private final Module module;

    public EntrainerProcessor() {
        System.out.println("Loading entrainment model...");
        this.module = Module.load("entrainer.pt");
    }

    @Override
    public Chunk doProcess(Chunk chunk) {
        Context context = chunk.getContext();

        Feature f = chunk.getFeatures();
        final Tensor data = Tensor.fromBlob(
                new float[]{f.pitchMean, f.pitchRange, f.intensityMean, f.jitter, f.shimmer, f.nhr, f.rate},
                new long[]{1, 7}
        );

        IValue result = this.module.forward(
                IValue.from(0),
                IValue.from(data),
                context.getFeatureHistory(),
                context.getFeatureMask(),
                context.getFeatureEncoderHidden(),
                context.getDecoderHidden(),
                chunk.getSpeaker()
        );

        return chunk;
    }
}
