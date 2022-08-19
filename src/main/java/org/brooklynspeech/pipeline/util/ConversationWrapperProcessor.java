package org.brooklynspeech.pipeline.util;

import org.brooklynspeech.pipeline.core.StreamProcessor;
import org.brooklynspeech.pipeline.data.Chunk;
import org.brooklynspeech.pipeline.data.ChunkMessage;
import org.brooklynspeech.pipeline.data.Conversation;

public class ConversationWrapperProcessor<ChunkType extends Chunk, ConversationType extends Conversation<ChunkType>>
        extends StreamProcessor<ChunkType, ChunkMessage<ChunkType, ConversationType>> {

    private final ConversationType conversation;

    public ConversationWrapperProcessor(ConversationType conversation) {
        super();
        this.conversation = conversation;
    }

    @Override
    public ChunkMessage<ChunkType, ConversationType> doProcess(ChunkType chunk) {
        return new ChunkMessage<>(chunk, this.conversation);
    }
}
