package org.brooklynspeech.pipeline.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class Unit<T> extends Thread {

    protected final BlockingQueue<T> queue;

    public Unit() {
        super();
        this.queue = new LinkedBlockingQueue<>();
    }

    public Unit(BlockingQueue<T> queue) {
        this.queue = queue;
    }

    public BlockingQueue<T> getQueue() {
        return this.queue;
    }
}
