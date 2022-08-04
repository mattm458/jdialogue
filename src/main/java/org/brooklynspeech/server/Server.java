package org.brooklynspeech.server;

import javax.sound.sampled.AudioFormat;

import org.brooklynspeech.pipeline.ContextCommitProcessor;
import org.brooklynspeech.pipeline.DummyTTSProcessor;
import org.brooklynspeech.pipeline.EmbeddingFeatureProcessor;
import org.brooklynspeech.pipeline.FileSaverProcessor;
import org.brooklynspeech.pipeline.PartnerFilterProcessor;
import org.brooklynspeech.pipeline.PartnerStatsProcessor;
import org.brooklynspeech.pipeline.Pipeline;
import org.brooklynspeech.pipeline.PraatFeatureProcessor;
import org.brooklynspeech.pipeline.VADProcessor;
import org.brooklynspeech.pipeline.VoskProcessor;
import org.brooklynspeech.pipeline.data.Context;
import org.brooklynspeech.pipeline.entrainment.NeuralEntrainmentStrategyProcessor;

public class Server {
        private final Pipeline userPipeline;
        private final Pipeline agentPipeline;
        private final Pipeline mergedPipeline;

        public static final AudioFormat FORMAT = new AudioFormat(16000, 16, 1, true, false);
        public static final int maxLength = 100;
        public static final int featureEncoderLayers = 2;
        public static final int decoderLayers = 2;
        public static final int encodedSize = 256;
        public static final int hiddenSize = 256;

        public Server() throws Exception {
                final Context context = new Context(0, Server.maxLength);

                context.setTorchFeature("featureHistory",
                                NeuralEntrainmentStrategyProcessor.getFeatureHistory(1, Server.maxLength,
                                                Server.encodedSize));
                context.setTorchFeature("featureEncoderHidden",
                                NeuralEntrainmentStrategyProcessor.getHidden(1, Server.featureEncoderLayers,
                                                Server.hiddenSize));
                context.setTorchFeature("decoderHidden",
                                NeuralEntrainmentStrategyProcessor.getHidden(1, Server.decoderLayers,
                                                Server.hiddenSize));

                this.agentPipeline = Pipeline.Builder
                                .withDummyTextSource(context)
                                .addProcessor(new EmbeddingFeatureProcessor("glove.6B.300d.txt", 300))
                                .addProcessor(new ContextCommitProcessor())
                                // .addProcessor(new NeuralEntrainmentStrategyProcessor())
                                .build();

                this.userPipeline = Pipeline.Builder
                                .withAudioFileSource("wav/GAME_speakerB.wav", 1024)
                                .addProcessor(new VoskProcessor("vosk-model-small-en-us-0.15", 16000, FORMAT, 0,
                                                context))
                                .addProcessor(new VADProcessor())
                                .addProcessor(new FileSaverProcessor(FORMAT))
                                .addProcessor(new PraatFeatureProcessor())
                                .addProcessor(new EmbeddingFeatureProcessor("glove.6B.300d.txt", 300))
                                .addProcessor(new ContextCommitProcessor())
                                .addProcessor(new PartnerStatsProcessor())
                                .build();

                this.mergedPipeline = Pipeline.Builder.fromMergedPipelines(this.agentPipeline, this.userPipeline)
                                .addProcessor(new NeuralEntrainmentStrategyProcessor())
                                .addProcessor(new PartnerFilterProcessor())
                                .addProcessor(new DummyTTSProcessor())
                                .build();

        }

        public void start() {
                this.mergedPipeline.start();
                this.userPipeline.start();
                this.agentPipeline.start();
        }
}
