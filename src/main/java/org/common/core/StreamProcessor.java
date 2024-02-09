package org.brooklynspeech.pipeline.core;

public abstract class StreamProcessor<InType, OutType> extends Processor<InType, OutType> {

    private boolean isReady = false;

    public boolean isReady() {
        return isReady;
    }

    protected void setup() {}

    @Override
    public final void run() {
        try {
            setup();

            this.isReady = true;

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
