package org.brooklynspeech.jdialogue;

import java.util.Map;

import javax.sound.sampled.AudioFormat;

import org.brooklynspeech.pipeline.EmbeddingFeatureProcessor;
import org.brooklynspeech.pipeline.audio.FileSaverProcessor;
import org.brooklynspeech.pipeline.audio.PraatFeatureProcessor;
import org.brooklynspeech.pipeline.audio.WavByteExtractor;
import org.brooklynspeech.pipeline.core.Pipeline;
import org.brooklynspeech.pipeline.data.TurnConversation;
import org.brooklynspeech.pipeline.entrainment.NeuralEntrainmentTurnFeatures;
import org.brooklynspeech.pipeline.entrainment.NeuralEntrainmentConversation;
import org.brooklynspeech.pipeline.entrainment.NeuralEntrainmentStrategyProcessor;
import org.brooklynspeech.pipeline.normalization.PartnerStatsProcessor;
import org.brooklynspeech.pipeline.sink.SocketSink;
import org.brooklynspeech.pipeline.source.MergeSource;
import org.brooklynspeech.pipeline.source.SocketObjectSource;
import org.brooklynspeech.pipeline.source.SocketTextSource;
import org.brooklynspeech.pipeline.tts.ControllableTacotronTTSProcessor;
import org.brooklynspeech.pipeline.util.ContextCommitProcessor;
import org.brooklynspeech.pipeline.util.ConversationWrapperProcessor;
import org.brooklynspeech.pipeline.util.PartnerFilterProcessor;

public class Jdialogue {

        private static final AudioFormat FORMAT = new AudioFormat(16000, 16, 1, true, false);

        private static final int VOSK_IN_PORT = 9990;
        private static final int TTS_OUT_PORT = 9993;
        private static final int TEXT_IN_PORT = 9992;

        public static void main(String[] args) throws Exception {

                // To run this pipeline, you need:
                // - A WozClient sending text on port 9992 and accepting audio on port 9993
                // - A MicrophoneSenderReceiver client accepting audio input on port 9994 and
                // sending microphone data on port 9991
                // Launch the pipeline first, then the clients.
                Map<String, float[]> embeddings = EmbeddingFeatureProcessor.load("glove.6B.300d.txt", 300);
                final NeuralEntrainmentConversation conversation = new NeuralEntrainmentConversation(0, 256);

                final Pipeline<TurnConversation<NeuralEntrainmentTurnFeatures, NeuralEntrainmentConversation>> agentPipeline = new Pipeline<>(
                                new SocketTextSource<>(NeuralEntrainmentTurnFeatures.class, conversation, TEXT_IN_PORT))
                                .addProcessor(new ContextCommitProcessor<>())
                                .addProcessor(new EmbeddingFeatureProcessor<>(embeddings, 300));

                final Pipeline<TurnConversation<NeuralEntrainmentTurnFeatures, NeuralEntrainmentConversation>> partnerPipeline = new Pipeline<>(
                                new SocketObjectSource<>(NeuralEntrainmentTurnFeatures.class, VOSK_IN_PORT))
                                .addProcessor(new ConversationWrapperProcessor<>(conversation))
                                .addProcessor(new FileSaverProcessor<>(FORMAT))
                                .addProcessor(new PraatFeatureProcessor<>())
                                .addProcessor(new EmbeddingFeatureProcessor<>(embeddings, 300))
                                .addProcessor(new ContextCommitProcessor<>())
                                .addProcessor(new PartnerStatsProcessor<>());

                final Pipeline<byte[]> mergedPipeline = new Pipeline<>(
                                new MergeSource<TurnConversation<NeuralEntrainmentTurnFeatures, NeuralEntrainmentConversation>>()
                                                .add(agentPipeline).add(partnerPipeline))
                                .addProcessor(new NeuralEntrainmentStrategyProcessor(
                                                "entrainer.pt", 256, 2,
                                                256, 2, 256, 300))
                                .addProcessor(new PartnerFilterProcessor<>())
                                .addProcessor(new ControllableTacotronTTSProcessor<>("tacotron-gpu.pt"))
                                .addProcessor(new WavByteExtractor<>())
                                .setSink(new SocketSink(TTS_OUT_PORT));

                Process voskProcess = JavaProcess.exec(VoskJDialogue.class, null);

                Runtime.getRuntime().addShutdownHook(new Thread() {
                        @Override
                        public void run() {
                                voskProcess.destroy();
                                mergedPipeline.interrupt();
                                agentPipeline.interrupt();
                                partnerPipeline.interrupt();
                        }
                });

                mergedPipeline.start();
                agentPipeline.start();
                partnerPipeline.start();
        }
}