package org.brooklynspeech.pipeline;

import org.brooklynspeech.pipeline.core.PassthroughProcessor;
import org.brooklynspeech.pipeline.data.Chunk;
import org.brooklynspeech.pipeline.data.ChunkMessage;
import org.brooklynspeech.pipeline.data.Conversation;

public class PartnerFilterProcessor<ChunkType extends Chunk, ConversationType extends Conversation<ChunkType>>
        extends PassthroughProcessor<ChunkMessage<ChunkType, ConversationType>> {

    @Override
    public ChunkMessage<ChunkType, ConversationType> doProcess(ChunkMessage<ChunkType, ConversationType> message) {
        ChunkType chunk = message.chunk;

        if (chunk.getSpeaker() == Chunk.Speaker.partner) {
            return null;
        }

        return message;
    }

}
