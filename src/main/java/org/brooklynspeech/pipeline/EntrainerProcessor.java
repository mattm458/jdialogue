package org.brooklynspeech.pipeline;

import java.util.Arrays;
import java.util.List;
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

        String outputText = "testing testing hello is anybody here";
        List<double[]> embeddingsList = EmbeddingProcessor.getEmbeddings(outputText);
        double[] embeddingsFlattened = embeddingsList.stream().flatMapToDouble(Arrays::stream).toArray();

        IValue result = this.module.forward(
                IValue.from(0),
                IValue.from(data),
                context.getFeatureHistory(),
                context.getFeatureMask(),
                context.getFeatureEncoderHidden(),
                context.getDecoderHidden(),
                chunk.getSpeaker(),
                chunk.getEmbeddings(),
                chunk.getEmbeddingsLength(),
                IValue.from(Tensor.fromBlob(embeddingsFlattened, new long[]{1, embeddingsList.size(), 300})),
                IValue.from(Tensor.fromBlob(new long[]{embeddingsList.size()}, new long[]{1})),
                IValue.from(Tensor.fromBlob(new long[]{0}, new long[]{1}))
        );

        return chunk;
    }
}
