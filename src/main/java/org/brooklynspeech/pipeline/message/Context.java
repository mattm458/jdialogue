package org.brooklynspeech.pipeline.message;

import java.util.ArrayList;

public class Context {

    private final ArrayList<Chunk> chunks = new ArrayList<>();

    public void addChunk(Chunk chunk) {
        this.chunks.add(chunk);
    }
}
