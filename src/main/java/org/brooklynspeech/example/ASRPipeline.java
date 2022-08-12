package org.brooklynspeech.example;

import javax.sound.sampled.AudioFormat;

import org.brooklynspeech.pipeline.asr.VoskProcessor;
import org.brooklynspeech.pipeline.core.Pipeline;
import org.brooklynspeech.pipeline.data.Conversation;
import org.brooklynspeech.pipeline.data.Chunk;
import org.brooklynspeech.pipeline.source.SocketSource;

public class ASRPipeline {
    protected static final int SOURCE_PORT = 9001;
    protected static final int BUFFER_SIZE = 1024;
    protected static final AudioFormat FORMAT = new AudioFormat(16000, 16, 1, true, false);

    public static void main(String[] args) throws Exception {
        final Conversation context = new Conversation(0);

        final Pipeline<Chunk> relayPipeline = new Pipeline<>(new SocketSource(SOURCE_PORT, BUFFER_SIZE))
                .addProcessor(new VoskProcessor("vosk-model-small-en-us-0.15", FORMAT, context));

        relayPipeline.start();
    }
}