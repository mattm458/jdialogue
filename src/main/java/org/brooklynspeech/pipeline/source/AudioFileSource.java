package org.brooklynspeech.pipeline.source;

import java.io.IOException;
import java.util.Arrays;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.brooklynspeech.pipeline.core.Source;

public class AudioFileSource extends Source<byte[]> {

    private final AudioInputStream stream;
    private final int packetSize;

    public AudioFileSource(String filename, int packetSize) throws UnsupportedAudioFileException, IOException {
        super();

        this.stream = AudioSystem.getAudioInputStream(
                AudioFileSource.class
                        .getClassLoader()
                        .getResourceAsStream(filename));

        this.packetSize = packetSize;
    }

    @Override
    public void run() {
        byte[] bytes = new byte[packetSize];
        int length;

        try {
            while (!Thread.currentThread().isInterrupted()
                    && (length = this.stream.read(bytes, 0, this.packetSize)) > 0) {
                length = this.stream.read(bytes, 0, packetSize);
                this.outQueue.add(Arrays.copyOf(bytes, length));
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.exit(1);
        }
    }
}
