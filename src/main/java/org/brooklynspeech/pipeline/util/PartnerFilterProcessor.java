package org.brooklynspeech.pipeline.util;

import org.brooklynspeech.pipeline.data.BSLTurn;
import org.brooklynspeech.pipeline.data.BSLTurnConversation;
import org.common.core.PassthroughStreamProcessor;
import org.brooklynspeech.pipeline.data.BSLConversation;

public class PartnerFilterProcessor<ChunkType extends BSLTurn, ConversationType extends BSLConversation<ChunkType>>
        extends PassthroughStreamProcessor<BSLTurnConversation<ChunkType, ConversationType>> {

    @Override
    public BSLTurnConversation<ChunkType, ConversationType> doProcess(BSLTurnConversation<ChunkType, ConversationType> message) {
        ChunkType chunk = message.chunk;

        if (chunk.getSpeaker() == BSLTurn.Speaker.partner) {
            return null;
        }

        return message;
    }

}
