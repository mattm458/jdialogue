package org.brooklynspeech.pipeline.util;

import org.brooklynspeech.pipeline.data.BSLTurn;
import org.brooklynspeech.pipeline.data.BSLTurnConversation;
import org.common.core.StreamProcessor;
import org.brooklynspeech.pipeline.data.BSLConversation;

public class ConversationWrapperProcessor<ChunkType extends BSLTurn, ConversationType extends BSLConversation<ChunkType>>
        extends StreamProcessor<ChunkType, BSLTurnConversation<ChunkType, ConversationType>> {

    private final ConversationType conversation;

    public ConversationWrapperProcessor(ConversationType conversation) {
        super();
        this.conversation = conversation;
    }

    @Override
    public BSLTurnConversation<ChunkType, ConversationType> doProcess(ChunkType chunk) {
        return new BSLTurnConversation<>(chunk, this.conversation);
    }
}
