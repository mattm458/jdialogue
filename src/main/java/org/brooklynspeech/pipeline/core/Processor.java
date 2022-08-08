package org.brooklynspeech.pipeline.core;

import java.util.concurrent.BlockingQueue;

public abstract class Processor<InType, OutType> extends Producer<OutType> {

    protected BlockingQueue<InType> inQueue = null;

    public void setInQueue(BlockingQueue<InType> inQueue) throws Exception {
        if (this.inQueue != null) {
            throw new Exception("Processor already has an input queue and cannot be reused!");
        }

        this.inQueue = inQueue;
    }

    public void setup() {
    }

    @Override
    public final void run() {
        this.setup();

        try {
            while (!Thread.currentThread().isInterrupted()) {
                final InType input = this.inQueue.take();
                final OutType output = doProcess(input);

                if (output == null) {
                    continue;
                }

                outQueue.add(output);
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(1);
            return;
        }
    }

    public abstract OutType doProcess(InType input) throws InterruptedException;
}
