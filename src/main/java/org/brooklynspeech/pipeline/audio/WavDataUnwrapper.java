package org.brooklynspeech.pipeline.audio;

import org.brooklynspeech.pipeline.core.Processor;
import org.brooklynspeech.pipeline.data.Chunk;

public class WavDataUnwrapper extends Processor<Chunk, byte[]> {
    public byte[] doProcess(Chunk features) {
        return features.getWavData();
    }
}
