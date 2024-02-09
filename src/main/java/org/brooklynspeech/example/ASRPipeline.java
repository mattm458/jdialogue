package org.brooklynspeech.example;

import javax.sound.sampled.AudioFormat;

import org.brooklynspeech.pipeline.asr.VoskProcessor;
import org.brooklynspeech.pipeline.data.Turn;
import org.brooklynspeech.pipeline.data.TurnConversation;
import org.brooklynspeech.pipeline.data.Conversation;
import org.brooklynspeech.pipeline.util.ConversationWrapperProcessor;
import org.common.core.Pipeline;
import org.common.source.SocketSource;

public class ASRPipeline {
    protected static final int SOURCE_PORT = 9001;
    protected static final int BUFFER_SIZE = 1024;
    protected static final AudioFormat FORMAT = new AudioFormat(16000, 16, 1, true, false);

    public static void main(String[] args) throws Exception {
        final Conversation<Turn> context = new Conversation<Turn>(0);

        final Pipeline<TurnConversation<Turn, Conversation<Turn>>> relayPipeline;
        relayPipeline = new Pipeline<>(new SocketSource(SOURCE_PORT, BUFFER_SIZE))
                .addProcessor(new VoskProcessor<>(Turn.class, "vosk-model-small-en-us-0.15", FORMAT))
                .addProcessor(new ConversationWrapperProcessor<>(context));

        relayPipeline.start();
    }
}