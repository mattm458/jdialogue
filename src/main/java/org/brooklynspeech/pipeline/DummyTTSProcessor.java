package org.brooklynspeech.pipeline;

import org.brooklynspeech.pipeline.core.Processor;
import org.brooklynspeech.pipeline.data.Chunk;

public class DummyTTSProcessor extends Processor<Chunk, Chunk> {

    @Override
    public Chunk doProcess(Chunk input) {
        System.out.println("TTS: " + input.getTranscript());
        return input;
    }

}
