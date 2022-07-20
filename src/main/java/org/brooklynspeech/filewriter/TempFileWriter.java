package org.brooklynspeech.filewriter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import org.brooklynspeech.pipeline.Chunk;

public class TempFileWriter {

    private final BlockingQueue<Chunk> q;
    private final AudioFormat format;
    private boolean running;

    public TempFileWriter(BlockingQueue<Chunk> q, AudioFormat format) {
        this.q = q;
        this.format = format;
        this.running = false;
    }

    public void start() {
        this.running = true;

        while (this.running) {
            final Chunk chunk;
            byte[] wavData;
            Path wavPath;

            try {
                chunk = q.take();
                wavData = chunk.getWavData();
                wavPath = Files.createTempFile("dialogue_" + chunk.getConversationId() + "_", ".wav");
            } catch (Exception e) {
                e.printStackTrace(System.out);
                System.exit(1);
                return;
            }

            chunk.setWavPath(wavPath);

            ByteArrayInputStream byteStream = new ByteArrayInputStream(wavData);
            AudioInputStream audioStream = new AudioInputStream(byteStream, this.format, wavData.length);

            try {
                AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, wavPath.toFile());
            } catch (IOException e) {
                e.printStackTrace(System.out);
                System.exit(1);
                return;
            }

            System.out.println(wavPath);
        }
    }

    public void stop() {
        this.running = false;
    }
}
