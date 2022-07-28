package org.brooklynspeech.server;

import javax.sound.sampled.AudioFormat;
import org.brooklynspeech.pipeline.ContextCommitProcessor;
import org.brooklynspeech.pipeline.EmbeddingFeatureProcessor;
import org.brooklynspeech.pipeline.FileSaverProcessor;
import org.brooklynspeech.pipeline.PartnerStatsProcessor;
import org.brooklynspeech.pipeline.Pipeline;
import org.brooklynspeech.pipeline.PraatFeatureProcessor;
import org.brooklynspeech.pipeline.VADProcessor;
import org.brooklynspeech.pipeline.VoskProcessor;
import org.brooklynspeech.pipeline.data.Context;
import org.brooklynspeech.pipeline.entrainment.MatchingEntrainmentStrategyProcessor;

public class Server {

    private final Pipeline userPipeline;
    private final Pipeline agentPipeline;

    public static final AudioFormat FORMAT = new AudioFormat(16000, 16, 1, true, false);

    public Server() throws Exception {
        final Context context = new Context(0, 10);
        this.userPipeline = Pipeline.Builder
                .withAudioFileSource("wav/GAME_speakerB.wav", 1024)
                .addProcessor(new VoskProcessor("vosk-model-en-us-0.21", 16000, FORMAT, 0, context))
                .addProcessor(new VADProcessor())
                .addProcessor(new FileSaverProcessor(FORMAT))
                .addProcessor(new PraatFeatureProcessor())
                .addProcessor(new EmbeddingFeatureProcessor("glove.6B.300d.txt", 300))
                .addProcessor(new ContextCommitProcessor())
                .addProcessor(new PartnerStatsProcessor())
                .build();

        this.agentPipeline = Pipeline.Builder
                .withDummyTextSource(context)
                .addProcessor(new EmbeddingFeatureProcessor("glove.6B.300d.txt", 300))
                .addProcessor(new ContextCommitProcessor())
                .addProcessor(new MatchingEntrainmentStrategyProcessor())
                .build();
    }

    public void start() {
        this.userPipeline.start();
        this.agentPipeline.start();
    }
}
