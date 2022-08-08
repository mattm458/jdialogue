package org.brooklynspeech.pipeline.core;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class Producer<OutType> extends Thread {
    protected final BlockingQueue<OutType> outQueue = new LinkedBlockingQueue<>();

    public BlockingQueue<OutType> getOutQueue() {
        return this.outQueue;
    }
}
