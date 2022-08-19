package org.brooklynspeech.example;

import javax.sound.sampled.AudioFormat;

import org.brooklynspeech.pipeline.asr.VoskProcessor;
import org.brooklynspeech.pipeline.core.Pipeline;
import org.brooklynspeech.pipeline.data.Chunk;
import org.brooklynspeech.pipeline.data.ChunkMessage;
import org.brooklynspeech.pipeline.data.Conversation;
import org.brooklynspeech.pipeline.source.SocketSource;
import org.brooklynspeech.pipeline.util.ConversationWrapperProcessor;

public class ASRPipeline {
    protected static final int SOURCE_PORT = 9001;
    protected static final int BUFFER_SIZE = 1024;
    protected static final AudioFormat FORMAT = new AudioFormat(16000, 16, 1, true, false);

    public static void main(String[] args) throws Exception {
        final Conversation<Chunk> context = new Conversation<Chunk>(0);

        final Pipeline<ChunkMessage<Chunk, Conversation<Chunk>>> relayPipeline;
        relayPipeline = new Pipeline<>(new SocketSource(SOURCE_PORT, BUFFER_SIZE))
                .addProcessor(new VoskProcessor<>(Chunk.class, "vosk-model-small-en-us-0.15", FORMAT))
                .addProcessor(new ConversationWrapperProcessor<>(context));

        relayPipeline.start();
    }
}