package org.brooklynspeech.pipeline.entrainment;

import org.brooklynspeech.pipeline.core.PassthroughStreamProcessor;
import org.brooklynspeech.pipeline.data.TurnConversation;
import org.brooklynspeech.pipeline.data.TurnFeatures;
import org.brooklynspeech.pipeline.data.FeatureConversation;

public class NeutralEntrainmentStrategyProcessor<ChunkType extends TurnFeatures, ConversationType extends FeatureConversation<ChunkType>>
        extends PassthroughStreamProcessor<TurnConversation<ChunkType, ConversationType>> {

    @Override
    public TurnConversation<ChunkType, ConversationType> doProcess(TurnConversation<ChunkType, ConversationType> message) {
        ChunkType chunk = message.chunk;

        for (String key : TurnFeatures.featureKeys) {
            chunk.setNormalizedFeature(key, 0.0f);
        }

        return message;
    }
}
