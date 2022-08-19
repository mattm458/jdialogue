package org.brooklynspeech.pipeline.entrainment;

import java.util.List;

import org.brooklynspeech.pipeline.core.PassthroughStreamProcessor;
import org.brooklynspeech.pipeline.data.Chunk;
import org.brooklynspeech.pipeline.data.ChunkMessage;
import org.brooklynspeech.pipeline.data.FeatureChunk;
import org.brooklynspeech.pipeline.data.FeatureConversation;

public class MatchingEntrainmentStrategyProcessor<ChunkType extends FeatureChunk, ConversationType extends FeatureConversation<ChunkType>>
        extends PassthroughStreamProcessor<ChunkMessage<ChunkType, ConversationType>> {

    @Override
    public ChunkMessage<ChunkType, ConversationType> doProcess(ChunkMessage<ChunkType, ConversationType> message) {
        ConversationType conversation = message.conversation;
        ChunkType chunk = message.chunk;

        List<ChunkType> partnerChunks = conversation.getPartnerChunks();
        ChunkType lastPartnerFeatures = partnerChunks.get(partnerChunks.size() - 1);

        for (String key : FeatureChunk.featureKeys) {
            float partnerFeatureVal = lastPartnerFeatures.getFeature(key);
            float partnerMean = conversation.getPartnerMean(key);
            float partnerStd = conversation.getPartnerStd(key);

            float ourFeatureNorm = (partnerFeatureVal - partnerMean) / partnerStd;

            chunk.setNormalizedFeature(key, ourFeatureNorm);
            System.out.println("Entrained " + key + ": " + ourFeatureNorm);
        }

        return message;
    }
}
