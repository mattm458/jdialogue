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

    public void write(byte[] b, int len) {
        this.out.write(b, 0, len);
    }

    public void close() {
        final byte rec[] = out.toByteArray();
        AudioInputStream stream = new AudioInputStream(
                new ByteArrayInputStream(rec),
                this.format,
                rec.length / this.format.getFrameSize());

        try {
            AudioSystem.write(stream, AudioFileFormat.Type.WAVE, new File(this.filename));
        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.exit(1);
        }
    }
}
