package org.brooklynspeech.pipeline.component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class Unit<OutType> extends Thread {

    protected final LinkedBlockingQueue<OutType> outQueue;

    public Unit() {
        super();
        this.outQueue = new LinkedBlockingQueue<>();
    }

    public BlockingQueue<OutType> getOutQueue() {
        return this.outQueue;
    }
}
