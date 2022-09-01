package org.brooklynspeech.pipeline.entrainment;

import org.brooklynspeech.pipeline.core.PassthroughStreamProcessor;
import org.brooklynspeech.pipeline.data.ChunkMessage;
import org.brooklynspeech.pipeline.data.FeatureChunk;
import org.brooklynspeech.pipeline.data.FeatureConversation;

public class NeutralEntrainmentStrategyProcessor<ChunkType extends FeatureChunk, ConversationType extends FeatureConversation<ChunkType>>
        extends PassthroughStreamProcessor<ChunkMessage<ChunkType, ConversationType>> {

    @Override
    public ChunkMessage<ChunkType, ConversationType> doProcess(ChunkMessage<ChunkType, ConversationType> message) {
        ChunkType chunk = message.chunk;

        for (String key : FeatureChunk.featureKeys) {
            chunk.setNormalizedFeature(key, 0.0f);
        }

        return message;
    }
}
