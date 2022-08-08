package org.brooklynspeech.pipeline.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;


public class Pipeline<OutType> extends Thread {
    private final List<Thread> processors;
    private final Unit<OutType> lastProcessor;

    public Pipeline(Source<OutType> source) {
        this.processors = new ArrayList<>();
        lastProcessor = source;
    }

    private Pipeline(List<Thread> processors, Unit<OutType> lastProcessor) {
        this.processors = processors;
        this.lastProcessor = lastProcessor;
    }

    public <T> Pipeline<T> addProcessor(Processor<OutType, T> p) throws Exception {
        List<Thread> processors = new ArrayList<>(this.processors);
        processors.add(this.lastProcessor);

        p.setInQueue(this.lastProcessor.getOutQueue());
        return new Pipeline<T>(processors, p);
    }

    public BlockingQueue<OutType> getOutQueue() {
        return this.lastProcessor.getOutQueue();
    }

    @Override
    public void run() {
        lastProcessor.start();

        for (int i = this.processors.size() - 1; i >= 0; i--) {
            this.processors.get(i).start();
        }
    }
}
