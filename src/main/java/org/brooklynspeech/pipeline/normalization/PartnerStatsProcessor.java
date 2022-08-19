package org.brooklynspeech.pipeline.normalization;

import java.util.HashMap;
import java.util.Iterator;

import org.brooklynspeech.pipeline.core.PassthroughStreamProcessor;
import org.brooklynspeech.pipeline.data.ChunkMessage;
import org.brooklynspeech.pipeline.data.FeatureChunk;
import org.brooklynspeech.pipeline.data.FeatureConversation;

public class PartnerStatsProcessor<ChunkType extends FeatureChunk, ConversationType extends FeatureConversation<ChunkType>>
        extends PassthroughStreamProcessor<ChunkMessage<ChunkType, ConversationType>> {

    @Override
    public ChunkMessage<ChunkType, ConversationType> doProcess(ChunkMessage<ChunkType, ConversationType> message)
            throws InterruptedException {
        final ConversationType conversation = message.conversation;
        Iterator<ChunkType> iterator;
        int count = 0;

        conversation.acquireConversation();
        conversation.acquireStats();

        // Compute means
        HashMap<String, Float> sums = new HashMap<>();
        iterator = conversation.getPartnerChunksIterator();
        while (iterator.hasNext()) {
            ChunkType f = iterator.next();
            count += 1;

            for (String featureKey : FeatureChunk.featureKeys) {
                float sum = sums.getOrDefault(featureKey, 0.0f);
                sum += f.getFeature(featureKey);
                sums.put(featureKey, sum);
            }
        }
        for (String featureKey : FeatureChunk.featureKeys) {
            conversation.setPartnerMean(featureKey, sums.get(featureKey) / count);
        }

        // Compute standard deviation
        final HashMap<String, Float> diffs = new HashMap<>();
        iterator = conversation.getPartnerChunksIterator();
        while (iterator.hasNext()) {
            ChunkType f = iterator.next();
            count += 1;

            for (String featureKey : FeatureChunk.featureKeys) {
                float diff = diffs.getOrDefault(featureKey, 0.0f);
                diff += Math.pow(f.getFeature(featureKey) - conversation.getPartnerMean(featureKey), 2);
                diffs.put(featureKey, diff);
            }
        }
        for (String featureKey : FeatureChunk.featureKeys) {
            float std = (float) Math.sqrt(diffs.get(featureKey) / count);
            conversation.setPartnerStd(featureKey, std);
        }

        conversation.releaseConversation();
        conversation.releaseStats();

        return message;
    }
}
