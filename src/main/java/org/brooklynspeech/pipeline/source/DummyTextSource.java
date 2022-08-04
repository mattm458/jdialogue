package org.brooklynspeech.pipeline.source;

import java.time.Instant;

import org.brooklynspeech.pipeline.component.Source;
import org.brooklynspeech.pipeline.data.Context;
import org.brooklynspeech.pipeline.data.Features;

public class DummyTextSource extends Source<Features> {

    protected final Context context;

    long last;
    String text;
    long interval;

    public DummyTextSource(Context context, String text, long interval) {
        super();

        this.context = context;

        last = Instant.now().getEpochSecond();

        this.text = text;
        this.interval = interval;
    }

    public DummyTextSource(Context context) {
        this(context, "testing testing one two three", 5);
    }

    @Override
    public Features doProcess() {
        long now = Instant.now().getEpochSecond();
        if (now - last > interval) {
            last = now;
            return new Features(this.context, Features.Speaker.us, this.text);
        } else {
            return null;
        }
    }
}
