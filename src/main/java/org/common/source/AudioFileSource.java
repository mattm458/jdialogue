package org.common.source;

import java.io.IOException;
import java.util.Arrays;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.common.core.Source;

public class AudioFileSource extends Source<byte[]> {

    private final AudioInputStream stream;
    private final int packetSize;

    public AudioFileSource(String filename, int packetSize)
            throws UnsupportedAudioFileException, IOException {
        super();

        this.stream = AudioSystem.getAudioInputStream(getClass().getResource(filename));

        this.packetSize = packetSize;
    }

    @Override
    public void run() {
        byte[] bytes = new byte[packetSize];
        int length;

        try {
            while (!Thread.currentThread().isInterrupted()
                    && (length = this.stream.read(bytes, 0, this.packetSize)) > 0) {
                this.outQueue.add(Arrays.copyOf(bytes, length));
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.exit(1);
        }
    }
}
