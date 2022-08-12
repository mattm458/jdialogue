package org.brooklynspeech.example;

import org.brooklynspeech.pipeline.audio.WavDataUnwrapper;
import org.brooklynspeech.pipeline.core.Pipeline;
import org.brooklynspeech.pipeline.data.Conversation;
import org.brooklynspeech.pipeline.sink.SocketSink;
import org.brooklynspeech.pipeline.source.AudioFileSource;
import org.brooklynspeech.pipeline.source.SocketTextSource;
import org.brooklynspeech.pipeline.tts.FreeTTSProcessor;

public class WozPipeline {
    protected static final int SYNTH_OUT_PORT=9003;
    protected static final int AUDIO_SINK_PORT = 9002;
    protected static final int TEXT_SOURCE_PORT = 9001;
    protected static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) throws Exception {
        Conversation context = new Conversation(0);

        final Pipeline<byte[]> audioPipeline = new Pipeline<>(new AudioFileSource("wav/GAME_speakerB.wav", BUFFER_SIZE))
                .setSink(new SocketSink(AUDIO_SINK_PORT));

        final Pipeline<byte[]> textPipeline = new Pipeline<>(new SocketTextSource(context, TEXT_SOURCE_PORT))
                .addProcessor(new FreeTTSProcessor())
                .addProcessor(new WavDataUnwrapper())
                .setSink(new SocketSink(SYNTH_OUT_PORT));

        textPipeline.start();
        audioPipeline.start();
    }
}
