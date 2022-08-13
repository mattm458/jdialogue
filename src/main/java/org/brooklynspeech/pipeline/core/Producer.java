package org.brooklynspeech.pipeline.core;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public abstract class Producer<OutType> extends Thread {
    protected final BlockingDeque<OutType> outQueue = new LinkedBlockingDeque<>();

    public BlockingDeque<OutType> getOutQueue() {
        return this.outQueue;
    }
}
