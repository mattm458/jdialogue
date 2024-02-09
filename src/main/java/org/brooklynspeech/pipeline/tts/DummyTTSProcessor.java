package org.brooklynspeech.pipeline.tts;

import org.brooklynspeech.pipeline.data.Turn;
import org.brooklynspeech.pipeline.data.TurnConversation;
import org.common.core.PassthroughStreamProcessor;
import org.brooklynspeech.pipeline.data.Conversation;

public class DummyTTSProcessor<ChunkType extends Turn, ConversationType extends Conversation<ChunkType>>
        extends PassthroughStreamProcessor<TurnConversation<ChunkType, ConversationType>> {

    @Override
    public TurnConversation<ChunkType, ConversationType> doProcess(TurnConversation<ChunkType, ConversationType> message) {
        System.out.println("TTS: " + message.chunk.getTranscript());
        return message;
    }

}
