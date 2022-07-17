package org.brooklynspeech.audio.source;

import java.io.IOException;
import javax.sound.sampled.AudioInputStream;

public class AudioInputStreamSource implements Source {

    private final AudioInputStream stream;

    public AudioInputStreamSource(AudioInputStream stream) {
        this.stream = stream;
    }

    @Override
    public void open() {
    }

    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public void start() {
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return this.stream.read(b, off, len);
    }

    @Override
    public void stop() {
    }

    @Override
    public void close() throws IOException {
        this.stream.close();
    }
}
