package org.common.data;

public class Turn {
    private byte[] wavData;
    private String wavPath;
    private String transcript;

    public byte[] getWavData() {
        return this.wavData;
    }

    public void setWavData(byte[] wavData) {
        this.wavData = wavData;
    }

    public String getWavPath() {
        return this.wavPath;
    }

    public void setWavPath(String wavPath) {
        this.wavPath = wavPath;
    }

    public String getTranscript() {
        return this.transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

}
