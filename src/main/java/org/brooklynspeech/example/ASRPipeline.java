package org.brooklynspeech.example;

import javax.sound.sampled.AudioFormat;

import org.brooklynspeech.pipeline.data.BSLTurn;
import org.brooklynspeech.pipeline.data.BSLTurnConversation;
import org.brooklynspeech.pipeline.data.BSLConversation;
import org.brooklynspeech.pipeline.util.ConversationWrapperProcessor;
import org.common.core.Pipeline;
import org.common.pipeline.asr.VoskProcessor;
import org.common.source.SocketByteSource;

public class ASRPipeline {
    protected static final int SOURCE_PORT = 9001;
    protected static final int BUFFER_SIZE = 1024;
    protected static final AudioFormat FORMAT = new AudioFormat(16000, 16, 1, true, false);

    public static void main(String[] args) throws Exception {
        final BSLConversation<BSLTurn> context = new BSLConversation<BSLTurn>(0);

        final Pipeline<BSLTurnConversation<BSLTurn, BSLConversation<BSLTurn>>> relayPipeline;
        relayPipeline = new Pipeline<>(new SocketByteSource(SOURCE_PORT, BUFFER_SIZE))
                .addProcessor(new VoskProcessor<>(BSLTurn.class, "vosk-model-small-en-us-0.15", FORMAT))
                .addProcessor(new ConversationWrapperProcessor<>(context));

        relayPipeline.start();
    }
}