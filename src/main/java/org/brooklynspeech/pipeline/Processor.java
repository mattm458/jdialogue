package org.brooklynspeech.pipeline;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class Processor<InType, OutType> extends Thread {

    protected BlockingQueue<InType> inQueue = null;
    protected BlockingQueue<OutType> outQueue = null;

    protected boolean running;

    public Processor() {
        this.running = false;
        this.outQueue = new LinkedBlockingQueue<>();
    }

    public BlockingQueue<OutType> getOutQueue() {
        return outQueue;
    }

    public void setInQueue(BlockingQueue<InType> inQueue) {
        this.inQueue = inQueue;
    }

    protected void setup() {
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
