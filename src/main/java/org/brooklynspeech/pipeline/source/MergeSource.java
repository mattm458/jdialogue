package org.brooklynspeech.pipeline.source;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

import org.brooklynspeech.pipeline.component.Pipeline;
import org.brooklynspeech.pipeline.component.Source;

public class MergeSource<T> extends Source<T> {

    protected ArrayList<BlockingQueue<T>> inQueues = new ArrayList<>();
    private int i = -1;

    public MergeSource<T> add(Pipeline<T> pipeline) {
        this.inQueues.add(pipeline.getOutQueue());
        return this;
    }

    public T doProcess() {
        this.i++;

        if (this.i >= this.inQueues.size()) {
            this.i = 0;
        }

        return this.inQueues.get(this.i).poll();
    }

}
