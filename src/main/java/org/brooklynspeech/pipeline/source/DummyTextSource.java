package org.brooklynspeech.pipeline.source;

import org.brooklynspeech.pipeline.core.Source;
import org.brooklynspeech.pipeline.data.Context;
import org.brooklynspeech.pipeline.data.Features;

public class DummyTextSource extends Source<Features> {

    private final Context context;
    private final String text;
    private final long interval;

    public DummyTextSource(Context context, String text, long interval) {
        super();

        this.context = context;
        this.text = text;
        this.interval = interval;
    }

    public DummyTextSource(Context context) {
        this(context, "testing testing one two three", 5);
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(interval * 1000);
                this.outQueue.add(new Features(this.context, Features.Speaker.us, this.text));

            }
        } catch (InterruptedException e) {
        }
    }
}
