package org.brooklynspeech.server;

import javax.sound.sampled.AudioFormat;
import org.brooklynspeech.pipeline.FileSaverProcessor;
import org.brooklynspeech.pipeline.Pipeline;
import org.brooklynspeech.pipeline.PraatFeatureProcessor;
import org.brooklynspeech.pipeline.VADProcessor;
import org.brooklynspeech.pipeline.VoskProcessor;

public class Server {

    private final Pipeline pipeline;
    public static final AudioFormat FORMAT = new AudioFormat(16000, 16, 1, true, false);

    public Server() throws Exception {
        this.pipeline = Pipeline.Builder
                .withAudioFileSource("wav/GAME_speakerB.wav", 1024)
                .addProcessor(new VoskProcessor("vosk-model-en-us-0.21", 16000, FORMAT, 0))
                .addProcessor(new VADProcessor())
                .addProcessor(new FileSaverProcessor(FORMAT))
                .addProcessor(new PraatFeatureProcessor())
                .build();
    }

    public void start() {
        this.pipeline.start();
    }
}
