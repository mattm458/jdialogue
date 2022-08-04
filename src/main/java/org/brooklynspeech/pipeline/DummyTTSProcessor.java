package org.brooklynspeech.pipeline;

import org.brooklynspeech.pipeline.component.Processor;
import org.brooklynspeech.pipeline.data.Features;

public class DummyTTSProcessor extends Processor<Features, Features> {

    @Override
    public Features doProcess(Features input) {
        System.out.println("TTS: " + input.getTranscript());
        return input;
    }

}
