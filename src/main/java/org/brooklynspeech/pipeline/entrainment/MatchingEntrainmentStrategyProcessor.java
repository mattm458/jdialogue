package org.brooklynspeech.pipeline.entrainment;

import java.util.List;

import org.brooklynspeech.pipeline.core.PassthroughStreamProcessor;
import org.brooklynspeech.pipeline.data.ChunkMessage;
import org.brooklynspeech.pipeline.data.FeatureChunk;
import org.brooklynspeech.pipeline.data.FeatureConversation;

public class MatchingEntrainmentStrategyProcessor<ChunkType extends FeatureChunk, ConversationType extends FeatureConversation<ChunkType>>
        extends PassthroughStreamProcessor<ChunkMessage<ChunkType, ConversationType>> {

    @Override
    public ChunkMessage<ChunkType, ConversationType> doProcess(ChunkMessage<ChunkType, ConversationType> message) {
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

        for (String key : FeatureChunk.featureKeys) {
            float partnerFeatureVal = lastPartnerFeatures.getFeature(key);

            float ourFeatureNorm = (partnerFeatureVal - conversation.getPartnerMean(key))
                    / conversation.getPartnerStd(key);

            chunk.setNormalizedFeature(key, ourFeatureNorm);
        }

        conversation.releaseStats();

        return message;
    }
}
