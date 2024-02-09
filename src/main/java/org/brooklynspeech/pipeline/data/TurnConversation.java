package org.brooklynspeech.pipeline.data;

public class TurnConversation<ChunkType extends Turn, ConversationType extends Conversation<ChunkType>> {
    public final ChunkType chunk;
    public final ConversationType conversation;

    public TurnConversation(ChunkType chunk, ConversationType conversation) {
        this.chunk = chunk;
        this.conversation = conversation;
    }

    public int getConversationId() {
        return this.conversation.getConversationId();
    }

}
