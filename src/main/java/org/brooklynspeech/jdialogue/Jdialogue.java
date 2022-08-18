package org.brooklynspeech.jdialogue;

import javax.sound.sampled.AudioFormat;

import org.brooklynspeech.pipeline.ContextCommitProcessor;
import org.brooklynspeech.pipeline.ConversationWrapperProcessor;
import org.brooklynspeech.pipeline.EmbeddingFeatureProcessor;
import org.brooklynspeech.pipeline.audio.FileSaverProcessor;
import org.brooklynspeech.pipeline.audio.PraatFeatureProcessor;
import org.brooklynspeech.pipeline.core.Pipeline;
import org.brooklynspeech.pipeline.data.ChunkMessage;
import org.brooklynspeech.pipeline.entrainment.NeuralEntrainmentChunk;
import org.brooklynspeech.pipeline.entrainment.NeuralEntrainmentConversation;
import org.brooklynspeech.pipeline.entrainment.NeuralEntrainmentStrategyProcessor;
import org.brooklynspeech.pipeline.normalization.PartnerStatsProcessor;
import org.brooklynspeech.pipeline.source.DummyTextSource;
import org.brooklynspeech.pipeline.source.MergeSource;
import org.brooklynspeech.pipeline.source.SocketObjectSource;

public class Jdialogue {

        public static final AudioFormat FORMAT = new AudioFormat(16000, 16, 1, true, false);
        public static final int maxLength = 100;
        public static final int featureEncoderLayers = 2;
        public static final int decoderLayers = 2;
        public static final int encodedSize = 256;
        public static final int hiddenSize = 256;

        public static void main(String[] args) throws Exception {
                final NeuralEntrainmentConversation conversation = new NeuralEntrainmentConversation(0, 256);

                final Pipeline<ChunkMessage<NeuralEntrainmentChunk, NeuralEntrainmentConversation>> agentPipeline = new Pipeline<>(
                                new DummyTextSource<>(NeuralEntrainmentChunk.class, conversation))
                                .addProcessor(new EmbeddingFeatureProcessor<>("glove.6B.300d.txt", 300))
                                .addProcessor(new ContextCommitProcessor<>());

                final Pipeline<ChunkMessage<NeuralEntrainmentChunk, NeuralEntrainmentConversation>> userPipeline = new Pipeline<>(
                                new SocketObjectSource<>(NeuralEntrainmentChunk.class, 9990))
                                .addProcessor(new ConversationWrapperProcessor<>(conversation))
                                .addProcessor(new FileSaverProcessor<>(FORMAT))
                                .addProcessor(new PraatFeatureProcessor<>())
                                .addProcessor(new EmbeddingFeatureProcessor<>("glove.6B.300d.txt", 300))
                                .addProcessor(new ContextCommitProcessor<>())
                                .addProcessor(new PartnerStatsProcessor<>());

                final Pipeline<ChunkMessage<NeuralEntrainmentChunk, NeuralEntrainmentConversation>> mergedPipeline = new Pipeline<>(
                                new MergeSource<ChunkMessage<NeuralEntrainmentChunk, NeuralEntrainmentConversation>>()
                                                .add(agentPipeline).add(userPipeline))
                                .addProcessor(new NeuralEntrainmentStrategyProcessor(
                                                "entrainer.pt", 256, 2,
                                                256, 2, 256, 300));

                Process voskProcess = JavaProcess.exec(VoskJDialogue.class, null);

                Runtime.getRuntime().addShutdownHook(new Thread() {
                        @Override
                        public void run() {
                                voskProcess.destroy();
                                mergedPipeline.interrupt();
                                agentPipeline.interrupt();
                                userPipeline.interrupt();
                        }
                });

                mergedPipeline.start();
                agentPipeline.start();
                userPipeline.start();
        }
}