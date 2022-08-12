package org.brooklynspeech.pipeline.entrainment;

import java.util.List;

import org.brooklynspeech.pipeline.core.PassthroughProcessor;
import org.brooklynspeech.pipeline.data.ChunkMessage;
import org.brooklynspeech.pipeline.data.FeatureChunk;
import org.brooklynspeech.pipeline.data.FeatureConversation;

public class MatchingEntrainmentStrategyProcessor<ChunkType extends FeatureChunk, ConversationType extends FeatureConversation<ChunkType>>
        extends PassthroughProcessor<ChunkMessage<ChunkType, ConversationType>> {

    @Override
    public ChunkMessage<ChunkType, ConversationType> doProcess(ChunkMessage<ChunkType, ConversationType> ourFeatures) {
        ConversationType conversation = ourFeatures.conversation;
        ChunkType chunk = ourFeatures.chunk;

        List<ChunkType> partnerFeatures = conversation.getPartnerFeatures();
        ChunkType lastPartnerFeatures = partnerFeatures.get(partnerFeatures.size() - 1);

        for (String key : FeatureChunk.featureKeys) {
            double partnerFeatureVal = lastPartnerFeatures.getFeature(key);
            double partnerMean = conversation.getPartnerMean(key);
            double partnerStd = conversation.getPartnerStd(key);

            double ourFeatureNorm = (partnerFeatureVal - partnerMean) / partnerStd;

            chunk.setNormalizedFeature(key, ourFeatureNorm);
            System.out.println("Entrained " + key + ": " + ourFeatureNorm);
        }

        return ourFeatures;
    }
}
