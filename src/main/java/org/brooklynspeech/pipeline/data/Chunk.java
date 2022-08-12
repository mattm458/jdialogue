package org.brooklynspeech.pipeline.data;

import java.util.List;

public class Chunk {
    public enum Speaker {
        us, partner
    }

    private Speaker speaker;
    private String transcript;

    private List<double[]> embeddings;

    private byte[] wavData;
    private String wavPath;

    public Speaker getSpeaker() {
        return this.speaker;
    }

    public void setSpeaker(Speaker speaker) {
        this.speaker = speaker;
    }

    public String getTranscript() {
        return this.transcript;
    }

    public void setTranscript(String transcript) {
        this.transcript = transcript;
    }

    public List<double[]> getEmbeddings() {
        return this.embeddings;
    }

    public void setEmbeddings(List<double[]> embeddings) {
        this.embeddings = embeddings;
    }

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
}
