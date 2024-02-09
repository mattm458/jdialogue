package org.common.core;

import java.util.concurrent.BlockingQueue;

public abstract class Sink<InType> extends Thread {
    protected BlockingQueue<InType> inQueue;

    public void setInQueue(BlockingQueue<InType> inQueue) {
        this.inQueue = inQueue;
    }
}
