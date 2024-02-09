package org.brooklynspeech.pipeline.util;

import org.brooklynspeech.pipeline.data.Turn;
import org.brooklynspeech.pipeline.data.TurnConversation;
import org.common.core.PassthroughStreamProcessor;
import org.brooklynspeech.pipeline.data.Conversation;

public class PartnerFilterProcessor<ChunkType extends Turn, ConversationType extends Conversation<ChunkType>>
        extends PassthroughStreamProcessor<TurnConversation<ChunkType, ConversationType>> {

    @Override
    public TurnConversation<ChunkType, ConversationType> doProcess(TurnConversation<ChunkType, ConversationType> message) {
        ChunkType chunk = message.chunk;

        if (chunk.getSpeaker() == Turn.Speaker.partner) {
            return null;
        }

        return message;
    }

}
