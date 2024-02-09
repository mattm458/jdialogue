package org.brooklynspeech.pipeline.util;

import org.brooklynspeech.pipeline.core.StreamProcessor;
import org.brooklynspeech.pipeline.data.Turn;
import org.brooklynspeech.pipeline.data.TurnConversation;
import org.brooklynspeech.pipeline.data.Conversation;

public class ConversationWrapperProcessor<ChunkType extends Turn, ConversationType extends Conversation<ChunkType>>
        extends StreamProcessor<ChunkType, TurnConversation<ChunkType, ConversationType>> {

    private final ConversationType conversation;

    public ConversationWrapperProcessor(ConversationType conversation) {
        super();
        this.conversation = conversation;
    }

    @Override
    public TurnConversation<ChunkType, ConversationType> doProcess(ChunkType chunk) {
        return new TurnConversation<>(chunk, this.conversation);
    }
}
