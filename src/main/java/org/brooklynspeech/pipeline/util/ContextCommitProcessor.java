package org.brooklynspeech.pipeline.util;

import org.brooklynspeech.pipeline.data.BSLTurn;
import org.brooklynspeech.pipeline.data.BSLTurnConversation;
import org.common.core.PassthroughStreamProcessor;
import org.brooklynspeech.pipeline.data.BSLConversation;

public class ContextCommitProcessor<ChunkType extends BSLTurn, ConversationType extends BSLConversation<ChunkType>>
        extends PassthroughStreamProcessor<BSLTurnConversation<ChunkType, ConversationType>> {

    @Override
    public BSLTurnConversation<ChunkType, ConversationType> doProcess(BSLTurnConversation<ChunkType, ConversationType> message) throws InterruptedException {
        ConversationType conversation = message.conversation;
        conversation.commitChunk(message.chunk);

        return message;
    }
}
