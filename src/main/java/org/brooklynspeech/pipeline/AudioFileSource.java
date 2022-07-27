package org.brooklynspeech.pipeline;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioFileSource extends Source<AudioPacket> {

    private final AudioInputStream stream;
    private final int packetSize;

    public AudioFileSource(String filename, int packetSize) throws UnsupportedAudioFileException, IOException {
        super();

        this.stream = AudioSystem.getAudioInputStream(
                AudioFileSource.class
                        .getClassLoader()
                        .getResourceAsStream(filename)
        );

        this.packetSize = packetSize;
    }

    @Override
    public AudioPacket doProcess() {
        byte[] bytes = new byte[packetSize];
        int len;

        try {
            len = this.stream.read(bytes, 0, packetSize);

            if (len == -1) {
                return null;
            }

        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.exit(1);
            return null;
        }

        return new AudioPacket(bytes, len);
    }
}
