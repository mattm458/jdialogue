package org.common.core;

import java.util.concurrent.BlockingDeque;

public abstract class Processor<InType, OutType> extends Producer<OutType> {
    protected BlockingDeque<InType> inQueue = null;

    public void setInQueue(BlockingDeque<InType> inQueue) throws Exception {
        if (this.inQueue != null) {
            throw new Exception("Processor already has an input queue and cannot be reused!");
        }

        this.inQueue = inQueue;
    }
}
