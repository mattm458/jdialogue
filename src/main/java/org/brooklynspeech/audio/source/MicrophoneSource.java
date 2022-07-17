package org.brooklynspeech.audio.source;

import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class MicrophoneSource implements Source {

    private final AudioFormat format;
    private final TargetDataLine microphone;

    public MicrophoneSource(AudioFormat format) throws LineUnavailableException {
        this.format = format;
        this.microphone = AudioSystem.getTargetDataLine(format);
        this.microphone.open(this.format);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return this.microphone.read(b, 0, len);
    }

    @Override
    public boolean isOpen() {
        return this.microphone.isOpen();
    }

    @Override
    public void start() {
        this.microphone.start();
    }

    @Override
    public void stop() {
        this.microphone.stop();
    }

    @Override
    public void close() throws IOException {
        this.microphone.close();
    }
}
