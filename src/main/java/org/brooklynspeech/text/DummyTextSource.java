package org.brooklynspeech.text;

import java.io.IOException;
import java.time.Instant;

public class DummyTextSource implements TextSource {

    private boolean running;
    private Long last;

    private String str;
    private long interval;

    public DummyTextSource(String str, long interval) {
        this.str = str;
        this.interval = interval;
    }

    public DummyTextSource() {
        this("testing testing one two three", 5);
    }

    @Override
    public String read() {
        long now = Instant.now().getEpochSecond();
        if (now - last > interval) {
            last = now;
            return str;
        } else {
            return "";
        }
    }

    @Override
    public boolean isOpen() {
        return running;
    }

    @Override
    public void start() {
        running = true;
        last = Instant.now().getEpochSecond();
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public void close() throws IOException {
    }
}
