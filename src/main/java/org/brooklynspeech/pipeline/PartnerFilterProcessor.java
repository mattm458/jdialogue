package org.brooklynspeech.pipeline;

import org.brooklynspeech.pipeline.core.Processor;
import org.brooklynspeech.pipeline.data.Chunk;

public class PartnerFilterProcessor extends Processor<Chunk, Chunk> {

    @Override
    public Chunk doProcess(Chunk features) {
        if (features.getSpeaker() == Chunk.Speaker.partner) {
            return null;
        }

        return features;
    }

}
