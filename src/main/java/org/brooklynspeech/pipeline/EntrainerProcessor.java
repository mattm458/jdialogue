package org.brooklynspeech.pipeline;

import org.brooklynspeech.pipeline.data.Features;
import org.brooklynspeech.pipeline.entrainment.MatchingEntrainmentStrategyProcessor;

public class EntrainerProcessor extends Processor<Features, Features> {

    private final MatchingEntrainmentStrategyProcessor strategy;

    public EntrainerProcessor(MatchingEntrainmentStrategyProcessor strategy) {
        this.strategy = strategy;
    }

    @Override
    public Features doProcess(Features chunk) {
        return chunk;
    }
}
