package org.brooklynspeech.pipeline.core;

import java.util.concurrent.BlockingQueue;

public abstract class Processor<InType, OutType> extends Unit<OutType> {

    protected BlockingQueue<InType> inQueue = null;

    protected void setup() {
    }

    public void setInQueue(BlockingQueue<InType> inQueue) throws Exception {
        if (this.inQueue != null) {
            throw new Exception("Processor already has an input queue and cannot be reused!");
        }

        this.inQueue = inQueue;
    }

    @Override
    public final void run() {
        this.setup();

        while (!Thread.currentThread().isInterrupted()) {
            InType input;

            try {
                input = this.inQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace(System.out);
                continue;
            }

            OutType output = doProcess(input);

            if (output == null) {
                continue;
            }

            outQueue.add(output);
        }
    }

    public abstract OutType doProcess(InType input);
}
