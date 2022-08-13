package org.brooklynspeech.pipeline.data;

public class ChunkMessage<ChunkType extends Chunk, ConversationType extends Conversation<ChunkType>> {
    public final ChunkType chunk;
    public final ConversationType conversation;

    public ChunkMessage(ChunkType chunk, ConversationType conversation) {
        this.chunk = chunk;
        this.conversation = conversation;
    }

    public int getConversationId() {
        return this.conversation.getConversationId();
    }

}
