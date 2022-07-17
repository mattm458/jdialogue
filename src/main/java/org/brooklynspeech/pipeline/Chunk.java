package org.brooklynspeech.pipeline;

import org.brooklynspeech.asr.alignment.Transcript;

public class Chunk {

    private Transcript transcript;
    private byte[] wavData;

    public void setTranscript(Transcript transcript) {
        this.transcript = transcript;
    }
    
    public void setWavData(byte[] wavData) {
        this.wavData = wavData;
    }
}
