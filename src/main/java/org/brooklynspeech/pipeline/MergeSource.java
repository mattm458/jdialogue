package org.brooklynspeech.pipeline;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

public class MergeSource<T> extends Source<T> {
    protected ArrayList<BlockingQueue<T>> inQueues = new ArrayList<>();

    private int i = -1;

    public MergeSource(Pipeline[] pipelines) {
        super();

        for (Pipeline p : pipelines) {
            this.inQueues.add(p.getOutQueue());
        }
    }

    public T doProcess() {
        this.i++;

        if (this.i >= this.inQueues.size()) {
            this.i = 0;
        }

        return this.inQueues.get(this.i).poll();
    }

}
