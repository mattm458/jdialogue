package org.brooklynspeech.pipeline;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class Source<OutType> extends Thread {

    protected final LinkedBlockingQueue<OutType> outQueue;

    public Source() {
        this.outQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public final void run() {
        while (!Thread.currentThread().isInterrupted()) {
            OutType output = doProcess();

            if (output == null) {
                continue;
            }

            outQueue.add(output);
        }
    }

    public BlockingQueue<OutType> getOutQueue() {
        return this.outQueue;
    }

    public abstract OutType doProcess();
}
