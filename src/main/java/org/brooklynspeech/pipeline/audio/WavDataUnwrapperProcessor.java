package org.brooklynspeech.pipeline.audio;

import org.brooklynspeech.pipeline.core.Processor;
import org.brooklynspeech.pipeline.data.Chunk;
import org.brooklynspeech.pipeline.data.ChunkMessage;
import org.brooklynspeech.pipeline.data.Conversation;

public class WavDataUnwrapperProcessor<ChunkType extends Chunk, ConversationType extends Conversation<ChunkType>>
        extends Processor<ChunkMessage<ChunkType, ConversationType>, byte[]> {

    public byte[] doProcess(ChunkMessage<ChunkType, ConversationType> message) {
        return message.chunk.getWavData();
    }
}
