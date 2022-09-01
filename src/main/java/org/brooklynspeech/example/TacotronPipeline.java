package org.brooklynspeech.example;

import org.brooklynspeech.pipeline.audio.WavDataUnwrapperProcessor;
import org.brooklynspeech.pipeline.core.Pipeline;
import org.brooklynspeech.pipeline.data.FeatureChunk;
import org.brooklynspeech.pipeline.data.FeatureConversation;
import org.brooklynspeech.pipeline.entrainment.NeutralEntrainmentStrategyProcessor;
import org.brooklynspeech.pipeline.sink.SocketSink;
import org.brooklynspeech.pipeline.source.DummyTextSource;
import org.brooklynspeech.pipeline.tts.ControllableTacotronTTSProcessor;

public class TacotronPipeline {
    protected static final int SOURCE_PORT = 9991;
    protected static final int SINK_PORT = 9992;
    protected static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) throws Exception {
        FeatureConversation<FeatureChunk> conversation = new FeatureConversation<>(0);

        final Pipeline<byte[]> tacotronPipeline = new Pipeline<>(
                new DummyTextSource<>(FeatureChunk.class, conversation))
                .addProcessor(new NeutralEntrainmentStrategyProcessor<>())
                .addProcessor(new ControllableTacotronTTSProcessor<>("tacotron-gpu.pt"))
                .addProcessor(new WavDataUnwrapperProcessor<>())
                .setSink(new SocketSink(SINK_PORT));

        tacotronPipeline.start();
    }
}
