package org.common.source;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

import org.common.core.Pipeline;
import org.common.core.Source;

public class MergeSource<T> extends Source<T> {

    protected ArrayList<BlockingQueue<T>> inQueues = new ArrayList<>();

    public MergeSource<T> add(Pipeline<T> pipeline) {
        this.inQueues.add(pipeline.getOutQueue());
        return this;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            for (BlockingQueue<T> q : inQueues) {
                T message = q.poll();
                if (message != null) {
                    this.outQueue.add(message);
                }
            }
        }
    }

}
