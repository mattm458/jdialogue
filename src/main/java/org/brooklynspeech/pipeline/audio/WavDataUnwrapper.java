package org.brooklynspeech.pipeline.audio;

import org.brooklynspeech.pipeline.core.Processor;
import org.brooklynspeech.pipeline.data.Features;

public class WavDataUnwrapper extends Processor<Features, byte[]> {
    public byte[] doProcess(Features features) {
        return features.getWavData();
    }
}
