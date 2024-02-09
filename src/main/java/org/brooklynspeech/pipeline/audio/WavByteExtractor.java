package org.brooklynspeech.pipeline.audio;

import org.brooklynspeech.pipeline.data.Turn;
import org.brooklynspeech.pipeline.data.TurnConversation;
import org.common.core.StreamProcessor;
import org.brooklynspeech.pipeline.data.Conversation;

public class WavByteExtractor<ChunkType extends Turn, ConversationType extends Conversation<ChunkType>>
        extends StreamProcessor<TurnConversation<ChunkType, ConversationType>, byte[]> {

    public byte[] doProcess(TurnConversation<ChunkType, ConversationType> message) {
        return message.chunk.getWavData();
    }
}
