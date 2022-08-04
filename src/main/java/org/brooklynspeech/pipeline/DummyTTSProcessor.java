package org.brooklynspeech.pipeline;

import org.brooklynspeech.pipeline.data.Features;

public class DummyTTSProcessor extends Processor<Features, Features> {

    //private final Voice voice;

    public DummyTTSProcessor() {
    }

    @Override
    public Features doProcess(Features input) {
        System.out.println("TTS: " + input.getTranscript());
        return input;
    }

}
