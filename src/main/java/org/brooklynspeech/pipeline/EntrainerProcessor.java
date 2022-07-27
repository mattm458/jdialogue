package org.brooklynspeech.pipeline;

import org.brooklynspeech.pipeline_old.message.Chunk;
import org.pytorch.Module;

public class EntrainerProcessor extends Processor<Chunk, Chunk> {

    private final Module module;

    public EntrainerProcessor() {
        this.module = Module.load("entrainer.pt");
    }

    @Override
    public Chunk doProcess(Chunk chunk) {
        return chunk;
    }
}
