package org.brooklynspeech.pipeline.core;

import java.util.concurrent.BlockingQueue;

public abstract class Sink<InType> extends Unit<InType> {
    public Sink(BlockingQueue<InType> queue) {
        super(queue);
    }
}
