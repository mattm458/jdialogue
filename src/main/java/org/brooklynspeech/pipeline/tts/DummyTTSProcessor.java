package org.brooklynspeech.pipeline.tts;

import org.brooklynspeech.pipeline.data.BSLTurn;
import org.brooklynspeech.pipeline.data.BSLTurnConversation;
import org.common.core.PassthroughStreamProcessor;
import org.brooklynspeech.pipeline.data.BSLConversation;

public class DummyTTSProcessor<ChunkType extends BSLTurn, ConversationType extends BSLConversation<ChunkType>>
        extends PassthroughStreamProcessor<BSLTurnConversation<ChunkType, ConversationType>> {

    @Override
    public BSLTurnConversation<ChunkType, ConversationType> doProcess(BSLTurnConversation<ChunkType, ConversationType> message) {
        System.out.println("TTS: " + message.chunk.getTranscript());
        return message;
    }

}
