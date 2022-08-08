package org.brooklynspeech.pipeline.source;

import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

import org.brooklynspeech.pipeline.core.Pipeline;
import org.brooklynspeech.pipeline.core.Source;

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
                    this.queue.add(message);
                }
            }
        }
    }

}
