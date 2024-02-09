package org.brooklynspeech.pipeline.data;

import org.common.data.Turn;

public class BSLTurn extends Turn {
    public enum Speaker {
        us, partner
    }

    private Speaker speaker;

    float[][] embeddings;

    public Speaker getSpeaker() {
        return this.speaker;
    }

    public void setSpeaker(Speaker speaker) {
        this.speaker = speaker;
    }

    public float[][] getEmbeddings() {
        return this.embeddings;
    }

    public void setEmbeddings(float[][] embeddings) {
        this.embeddings = embeddings;
    }
}
