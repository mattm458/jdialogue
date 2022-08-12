package org.brooklynspeech.pipeline.source;

import org.brooklynspeech.pipeline.core.Source;
import org.brooklynspeech.pipeline.data.Conversation;
import org.brooklynspeech.pipeline.data.Chunk;

public class DummyTextSource extends Source<Chunk> {

    private final Conversation context;
    private final String text;
    private final long interval;

    public DummyTextSource(Conversation context, String text, long interval) {
        super();

        this.context = context;
        this.text = text;
        this.interval = interval;
    }

    public DummyTextSource(Conversation context) {
        this(context, "testing testing one two three", 5);
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(interval * 1000);
                this.outQueue.add(new Chunk(this.context, Chunk.Speaker.us, this.text));

            }
        } catch (InterruptedException e) {
        }
    }
}
