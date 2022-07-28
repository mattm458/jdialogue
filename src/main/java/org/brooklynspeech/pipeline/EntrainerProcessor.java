package org.brooklynspeech.pipeline;

import org.brooklynspeech.pipeline.entrainment.EntrainmentStrategy;
import org.brooklynspeech.pipeline.data.Features;

public class EntrainerProcessor extends Processor<Features, Features> {

    private final EntrainmentStrategy strategy;

    public EntrainerProcessor(EntrainmentStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public Features doProcess(Features chunk) {
        return chunk;
    }
}
