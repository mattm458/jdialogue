package org.brooklynspeech.server;

import javax.sound.sampled.AudioFormat;
import org.brooklynspeech.pipeline.EmbeddingProcessor;
import org.brooklynspeech.pipeline.EntrainerProcessor;
import org.brooklynspeech.pipeline.FileSaverProcessor;
import org.brooklynspeech.pipeline.Pipeline;
import org.brooklynspeech.pipeline.PraatFeatureProcessor;
import org.brooklynspeech.pipeline.VADProcessor;
import org.brooklynspeech.pipeline.VoskProcessor;
import org.brooklynspeech.pipeline_old.message.Context;

public class Server {

    private final Pipeline pipeline;
    public static final AudioFormat FORMAT = new AudioFormat(16000, 16, 1, true, false);

    public Server() throws Exception {
        final Context context = new Context(10, 7, 256, 2, 256, 2);

        this.pipeline = Pipeline.Builder
                .withAudioFileSource("wav/GAME_speakerB.wav", 1024)
                .addProcessor(new VoskProcessor("vosk-model-en-us-0.21", 16000, FORMAT, 0, context))
                .addProcessor(new VADProcessor())
                .addProcessor(new FileSaverProcessor(FORMAT))
                .addProcessor(new PraatFeatureProcessor())
                .addProcessor(new EmbeddingProcessor("glove.6B.300d.txt", 300))
                .addProcessor(new EntrainerProcessor())
                .build();
    }

    public void start() {
        this.pipeline.start();
    }
}
