package org.brooklynspeech.pipeline;

import org.brooklynspeech.pipeline.component.Processor;
import org.brooklynspeech.pipeline.data.Context;
import org.brooklynspeech.pipeline.data.Features;

public class ContextCommitProcessor extends Processor<Features, Features> {

    @Override
    public Features doProcess(Features features) {
        Context context = features.getContext();
        context.commitFeatures(features);

        return features;
    }
}
