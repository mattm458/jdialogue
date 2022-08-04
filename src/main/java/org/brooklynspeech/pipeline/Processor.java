package org.brooklynspeech.pipeline;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class Processor<InType, OutType> {

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

    protected void setup() {}

    public final void start() {
        this.running = true;

        this.setup();

        while (this.running) {
            InType input;

            try {
                input = this.inQueue.take();
            } catch (InterruptedException e) {
                e.printStackTrace(System.out);
                this.running = false;
                return;
            }

            OutType output = doProcess(input);
            if (output == null) {
                continue;
            }

            outQueue.add(output);
        }
    }

    public void stop() {
        this.running = false;
    }

    public abstract OutType doProcess(InType input);
}
