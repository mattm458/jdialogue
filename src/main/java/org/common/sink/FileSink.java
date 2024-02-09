package org.brooklynspeech.audio.sink;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class FileSink implements Sink {

    private final String filename;
    private final AudioFormat format;
    private final ByteArrayOutputStream out;

    public FileSink(String filename, AudioFormat format) {
        this.filename = filename;
        this.format = format;
        this.out = new ByteArrayOutputStream();
    }

    @Override
    public void write(byte[] b, int len) {
        this.out.write(b, 0, len);
    }

    @Override
    public void close() throws IOException {
        final byte rec[] = out.toByteArray();
        AudioInputStream stream = new AudioInputStream(
                new ByteArrayInputStream(rec),
                this.format,
                rec.length / this.format.getFrameSize());

        AudioSystem.write(stream, AudioFileFormat.Type.WAVE, new File(this.filename));
    }
}
