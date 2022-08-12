package org.brooklynspeech.pipeline;

import org.brooklynspeech.pipeline.core.Processor;
import org.brooklynspeech.pipeline.data.Conversation;
import org.brooklynspeech.pipeline.data.Chunk;

public class ContextCommitProcessor extends Processor<Chunk, Chunk> {

    @Override
    public Chunk doProcess(Chunk features) throws InterruptedException {
        Conversation context = features.getContext();

        context.commitFeatures(features);

        return features;
    }
}
