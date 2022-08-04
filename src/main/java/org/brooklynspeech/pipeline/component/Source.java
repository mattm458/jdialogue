package org.brooklynspeech.pipeline.component;

public abstract class Source<OutType> extends Unit<OutType> {

    @Override
    public final void run() {
        while (!Thread.currentThread().isInterrupted()) {
            OutType output = doProcess();

            if (output == null) {
                continue;
            }

            this.outQueue.add(output);
        }
    }

    public abstract OutType doProcess();
}
