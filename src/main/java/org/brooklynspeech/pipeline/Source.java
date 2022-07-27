package org.brooklynspeech.pipeline;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class Source<OutType> {

    protected final LinkedBlockingQueue<OutType> outQueue;

    protected boolean running;

    public Source() {
        this.outQueue = new LinkedBlockingQueue<>();
        this.running = false;
    }

    public final void start() {
        this.running = true;

        while (this.running) {
            OutType output = doProcess();
            if (output == null) {
                continue;
            }
            
            outQueue.add(output);
        }
    }

    public void stop() {
        this.running = false;
    }
    
    public BlockingQueue<OutType> getOutQueue() {
        return this.outQueue;
    }

    public abstract OutType doProcess();
}
