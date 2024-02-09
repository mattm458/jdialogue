package org.brooklynspeech.pipeline.util;

import org.brooklynspeech.pipeline.core.PassthroughStreamProcessor;
import org.brooklynspeech.pipeline.data.Turn;
import org.brooklynspeech.pipeline.data.TurnConversation;
import org.brooklynspeech.pipeline.data.Conversation;

public class ContextCommitProcessor<ChunkType extends Turn, ConversationType extends Conversation<ChunkType>>
        extends PassthroughStreamProcessor<TurnConversation<ChunkType, ConversationType>> {

    @Override
    public TurnConversation<ChunkType, ConversationType> doProcess(TurnConversation<ChunkType, ConversationType> message) throws InterruptedException {
        ConversationType conversation = message.conversation;
        conversation.commitChunk(message.chunk);

        return message;
    }
}
