package org.brooklynspeech.jdialogue;

import javax.sound.sampled.AudioFormat;

import org.brooklynspeech.pipeline.ContextCommitProcessor;
import org.brooklynspeech.pipeline.DummyTTSProcessor;
import org.brooklynspeech.pipeline.EmbeddingFeatureProcessor;
import org.brooklynspeech.pipeline.PartnerFilterProcessor;
import org.brooklynspeech.pipeline.asr.VoskProcessor;
import org.brooklynspeech.pipeline.audio.FileSaverProcessor;
import org.brooklynspeech.pipeline.audio.PraatFeatureProcessor;
import org.brooklynspeech.pipeline.audio.VADProcessor;
import org.brooklynspeech.pipeline.core.Pipeline;
import org.brooklynspeech.pipeline.data.Context;
import org.brooklynspeech.pipeline.data.Features;
import org.brooklynspeech.pipeline.entrainment.NeuralEntrainmentStrategyProcessor;
import org.brooklynspeech.pipeline.normalization.PartnerStatsProcessor;
import org.brooklynspeech.pipeline.source.AudioFileSource;
import org.brooklynspeech.pipeline.source.DummyTextSource;
import org.brooklynspeech.pipeline.source.MergeSource;

public class Jdialogue {
    public static final AudioFormat FORMAT = new AudioFormat(16000, 16, 1, true, false);
    public static final int maxLength = 100;
    public static final int featureEncoderLayers = 2;
    public static final int decoderLayers = 2;
    public static final int encodedSize = 256;
    public static final int hiddenSize = 256;

    public static void main(String[] args) throws Exception {

        final Context context = new Context(0);

        NeuralEntrainmentStrategyProcessor neuralEntrainmentStrategyProcessor = new NeuralEntrainmentStrategyProcessor();

        final Pipeline<Features> agentPipeline = new Pipeline<>(new DummyTextSource(context))
                .addProcessor(new EmbeddingFeatureProcessor("glove.6B.300d.txt", 300))
                .addProcessor(new ContextCommitProcessor());

        final Pipeline<Features> userPipeline = new Pipeline<>(new AudioFileSource("wav/GAME_speakerB.wav", 1024))
                .addProcessor(new VoskProcessor("vosk-model-small-en-us-0.15", FORMAT, context))
                .addProcessor(new VADProcessor())
                .addProcessor(new FileSaverProcessor(FORMAT))
                .addProcessor(new PraatFeatureProcessor())
                .addProcessor(new EmbeddingFeatureProcessor("glove.6B.300d.txt", 300))
                .addProcessor(new ContextCommitProcessor())
                .addProcessor(new PartnerStatsProcessor());

        final Pipeline<Features> mergedPipeline = new Pipeline<>(
                new MergeSource<Features>().add(agentPipeline).add(userPipeline))
                .addProcessor(neuralEntrainmentStrategyProcessor)
                .addProcessor(new PartnerFilterProcessor())
                .addProcessor(new DummyTTSProcessor());

        mergedPipeline.start();
        agentPipeline.start();
        userPipeline.start();

    }
}
