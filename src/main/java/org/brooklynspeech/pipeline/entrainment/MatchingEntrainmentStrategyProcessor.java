package org.brooklynspeech.pipeline.entrainment;

import java.util.List;

import org.brooklynspeech.pipeline.core.PassthroughStreamProcessor;
import org.brooklynspeech.pipeline.data.TurnConversation;
import org.brooklynspeech.pipeline.data.TurnFeatures;
import org.brooklynspeech.pipeline.data.FeatureConversation;

public class MatchingEntrainmentStrategyProcessor<ChunkType extends TurnFeatures, ConversationType extends FeatureConversation<ChunkType>>
        extends PassthroughStreamProcessor<TurnConversation<ChunkType, ConversationType>> {

    @Override
    public TurnConversation<ChunkType, ConversationType> doProcess(TurnConversation<ChunkType, ConversationType> message) {
        ConversationType conversation = message.conversation;
        ChunkType chunk = message.chunk;

        try {
            conversation.acquireConversation();
            conversation.acquireStats();
        } catch (InterruptedException e) {
            e.printStackTrace(System.out);
            System.exit(1);
        }

        List<ChunkType> partnerChunks = conversation.getPartnerChunks();
        ChunkType lastPartnerFeatures = partnerChunks.get(partnerChunks.size() - 1);

        conversation.releaseConversation();

        for (String key : TurnFeatures.featureKeys) {
            float partnerFeatureVal = lastPartnerFeatures.getFeature(key);

            float ourFeatureNorm = (partnerFeatureVal - conversation.getPartnerMean(key))
                    / conversation.getPartnerStd(key);

            chunk.setNormalizedFeature(key, ourFeatureNorm);
        }

        conversation.releaseStats();

        return message;
    }
}
