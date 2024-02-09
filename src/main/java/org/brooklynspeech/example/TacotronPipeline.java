package org.brooklynspeech.example;

import org.brooklynspeech.pipeline.audio.WavByteExtractor;
import org.brooklynspeech.pipeline.data.BSLTurn;
import org.brooklynspeech.pipeline.data.BSLTurnConversation;
import org.brooklynspeech.pipeline.data.BSLTurnFeatures;
import org.brooklynspeech.pipeline.data.BSLFeatureConversation;
import org.brooklynspeech.pipeline.entrainment.NeutralEntrainmentStrategyProcessor;
import org.brooklynspeech.pipeline.tts.ControllableTacotronTTSVocoderProcessor;
import org.common.core.Pipeline;
import org.common.sink.SocketSink;
import org.common.source.DummyTextSource;

public class TacotronPipeline {
    protected static final int SOURCE_PORT = 9991;
    protected static final int SINK_PORT = 9992;
    protected static final int BUFFER_SIZE = 1024;

    // public static void main(String[] args) throws Exception {
    //     BSLFeatureConversation<BSLTurn> conversation = new BSLFeatureConversation<>(0);

    //     final Pipeline<byte[]> tacotronPipeline = new Pipeline<>(
    //             new DummyTextSource<>(BSLTurnConversation.class, conversation))
    //             .addProcessor(new NeutralEntrainmentStrategyProcessor<>())
    //             .addProcessor(new ControllableTacotronTTSVocoderProcessor<>("tacotron-hifi-gan.pt"))
    //             .addProcessor(new WavByteExtractor<>())
    //             .setSink(new SocketSink(SINK_PORT));

    //     tacotronPipeline.start();
    // }
}
