package org.brooklynspeech.pipeline.audio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.brooklynspeech.pipeline.core.Processor;
import org.brooklynspeech.pipeline.data.Chunk;

public class FileSaverProcessor extends Processor<Chunk, Chunk> {

    private final AudioFormat format;

    public FileSaverProcessor(AudioFormat format) {
        this.format = format;
    }

    @Override
    public Chunk doProcess(Chunk features) {
        Path wavPath;

        byte[] wavData = features.getWavData();

        try {
            wavPath = Files.createTempFile("dialogue_" + features.getContext().getConversationId() + "_", ".wav");
        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.exit(1);
            return null;
        }

        features.setWavPath(wavPath.toString());

        ByteArrayInputStream byteStream = new ByteArrayInputStream(wavData);
        AudioInputStream audioStream = new AudioInputStream(byteStream, this.format, wavData.length);

        try {
            AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, wavPath.toFile());
        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.exit(1);
            return null;
        }

        return features;
    }

}
