package org.brooklynspeech.pipeline;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.brooklynspeech.pipeline.component.Processor;
import org.brooklynspeech.pipeline.data.Features;

public class FileSaverProcessor extends Processor<Features, Features> {

    private final AudioFormat format;

    public FileSaverProcessor(AudioFormat format) {
        this.format = format;
    }

    @Override
    public Features doProcess(Features features) {
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
