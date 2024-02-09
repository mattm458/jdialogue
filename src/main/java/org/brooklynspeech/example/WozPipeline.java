package org.brooklynspeech.example;

import org.brooklynspeech.pipeline.audio.WavByteExtractor;
import org.brooklynspeech.pipeline.data.Turn;
import org.brooklynspeech.pipeline.data.Conversation;
import org.brooklynspeech.pipeline.tts.FreeTTSProcessor;
import org.common.core.Pipeline;
import org.common.sink.SocketSink;
import org.common.source.AudioFileSource;
import org.common.source.SocketTextSource;

public class WozPipeline {
    protected static final int SYNTH_OUT_PORT = 9993;
    protected static final int AUDIO_SINK_PORT = 9992;
    protected static final int TEXT_SOURCE_PORT = 9991;
    protected static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) throws Exception {
        Conversation<Turn> context = new Conversation<>(0);

        final AudioFileSource audioPipelineSource;
        audioPipelineSource = new AudioFileSource("/wav/GAME_speakerB.wav", BUFFER_SIZE);

        final Pipeline<byte[]> audioPipeline = new Pipeline<>(audioPipelineSource)
                .setSink(new SocketSink(AUDIO_SINK_PORT));

        final SocketTextSource<Turn, Conversation<Turn>> textPipelineSource;
        textPipelineSource = new SocketTextSource<>(Turn.class, context, TEXT_SOURCE_PORT);

        final Pipeline<byte[]> textPipeline = new Pipeline<>(textPipelineSource)
                .addProcessor(new FreeTTSProcessor<>())
                .addProcessor(new WavByteExtractor<>())
                .setSink(new SocketSink(SYNTH_OUT_PORT));

        textPipeline.start();
        audioPipeline.start();
    }
}
