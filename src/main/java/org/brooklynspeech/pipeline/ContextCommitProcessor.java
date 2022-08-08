package org.brooklynspeech.pipeline;

import org.brooklynspeech.pipeline.core.Processor;
import org.brooklynspeech.pipeline.data.Context;
import org.brooklynspeech.pipeline.data.Features;

public class ContextCommitProcessor extends Processor<Features, Features> {

    @Override
    public Features doProcess(Features features) throws InterruptedException {
        Context context = features.getContext();

        context.commitFeatures(features);

        return features;
    }
}
