package org.brooklynspeech.pipeline.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Pipeline<OutType> extends Thread {
    private final List<Thread> units;
    private Producer<OutType> lastUnit;
    private Sink<OutType> sink = null;

    public Pipeline(Source<OutType> source) {
        this.units = new ArrayList<>();
        this.lastUnit = source;
    }

    private Pipeline(List<Thread> processors, Producer<OutType> lastProcessor) {
        this.units = processors;
        this.lastUnit = lastProcessor;
    }

    public <T> Pipeline<T> addProcessor(Processor<OutType, T> p) throws Exception {
        if (this.sink != null) {
            throw new Exception("Pipeline was assigned a sink and cannot be extended with additional processors!");
        }

        List<Thread> processors = new ArrayList<>(this.units);
        processors.add(this.lastUnit);

        p.setInQueue(this.lastUnit.getOutQueue());
        return new Pipeline<T>(processors, p);
    }

    public Pipeline<OutType> setSink(Sink<OutType> sink) {
        this.sink = sink;
        this.sink.setInQueue(this.lastUnit.getOutQueue());

        return this;
    }

    public BlockingQueue<OutType> getOutQueue() {
        return this.lastUnit.getOutQueue();
    }

    @Override
    public void run() {
        if (this.sink != null) {
            this.sink.start();
        }

        if (this.lastUnit != null) {
            this.lastUnit.start();
        }

        for (int i = this.units.size() - 1; i >= 0; i--) {
            this.units.get(i).start();
        }
    }
}
