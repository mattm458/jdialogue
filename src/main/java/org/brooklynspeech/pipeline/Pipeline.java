package org.brooklynspeech.pipeline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.brooklynspeech.pipeline.data.Context;

public class Pipeline {

    private Source source;
    private final List<Processor> processors;
    private final ArrayList<Thread> threads;

    public Pipeline(Source source, List<Processor> processors) {
        this.source = source;
        this.processors = processors;
        this.threads = new ArrayList<>();
    }

    public BlockingQueue getOutQueue() {
        return this.processors.get(this.processors.size()-1).getOutQueue();
    }

    public void start() {
        for (Processor p : processors) {
            Thread processorThread = new Thread(() -> {
                try {
                    p.start();
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                    System.exit(1);
                }
            });
            this.threads.add(processorThread);
            processorThread.start();
        }

        Thread sourceThread = new Thread(() -> {
            try {
                source.start();
            } catch (Exception e) {
                e.printStackTrace(System.out);
                System.exit(1);
            }
        });
        this.threads.add(sourceThread);
        sourceThread.start();
    }

    public void stop() {
        source.stop();

        for (Processor p : processors) {
            p.stop();
        }
    }

    public static class Builder {

        private Source source;
        private final ArrayList<Processor> processors = new ArrayList<>();

        public static Builder withAudioFileSource(String path, int packetSize)
                throws UnsupportedAudioFileException, IOException {
            Builder builder = new Builder();
            builder.setSource(new AudioFileSource(path, packetSize));

            return builder;
        }

        public static Builder withDummyTextSource(Context context) {
            Builder builder = new Builder();
            builder.setSource(new DummyTextSource(context));

            return builder;
        }

        public static Builder fromMergedPipelines(Pipeline... pipelines) {
            Builder builder = new Builder();
            builder.setSource(new MergeSource(pipelines));

            return builder;
        }


        public Builder setSource(Source source) {
            this.source = source;

            return this;
        }

        public Builder addProcessor(Processor p) {
            if (processors.isEmpty()) {
                p.setInQueue(source.getOutQueue());
            } else {
                p.setInQueue(this.processors.get(processors.size() - 1).getOutQueue());
            }

            processors.add(p);

            return this;
        }

        public Pipeline build() {
            return new Pipeline(source, processors);
        }
    }
}
