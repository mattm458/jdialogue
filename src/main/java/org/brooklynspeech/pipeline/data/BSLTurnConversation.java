package org.brooklynspeech.pipeline.data;

public class BSLTurnConversation<TurnType extends BSLTurn, ConversationType extends BSLConversation<TurnType>> {
    public final TurnType chunk;
    public final ConversationType conversation;

    public BSLTurnConversation(TurnType chunk, ConversationType conversation) {
        this.chunk = chunk;
        this.conversation = conversation;
    }

    public int getConversationId() {
        return this.conversation.getConversationId();
    }

}
