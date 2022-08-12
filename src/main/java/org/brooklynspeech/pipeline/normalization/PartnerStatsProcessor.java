package org.brooklynspeech.pipeline.normalization;

import java.util.HashMap;
import java.util.Iterator;

import org.brooklynspeech.pipeline.core.PassthroughProcessor;
import org.brooklynspeech.pipeline.data.ChunkMessage;
import org.brooklynspeech.pipeline.data.FeatureChunk;
import org.brooklynspeech.pipeline.data.FeatureConversation;

public class PartnerStatsProcessor<ChunkType extends FeatureChunk, ConversationType extends FeatureConversation<ChunkType>>
        extends PassthroughProcessor<ChunkMessage<ChunkType, ConversationType>> {

    @Override
    public ChunkMessage<ChunkType, ConversationType> doProcess(ChunkMessage<ChunkType, ConversationType> message)
            throws InterruptedException {
        final ConversationType conversation = message.conversation;
        Iterator<ChunkType> iterator;
        int count = 0;

        conversation.acquireConversation();
        conversation.acquireStats();

        // Compute means
        HashMap<String, Double> sums = new HashMap<>();
        iterator = conversation.getPartnerFeaturesIterator();
        while (iterator.hasNext()) {
            ChunkType f = iterator.next();
            count += 1;

            for (String featureKey : FeatureChunk.featureKeys) {
                double sum = sums.getOrDefault(featureKey, 0.0);
                sum += f.getFeature(featureKey);
                sums.put(featureKey, sum);
            }
        }
        for (String featureKey : FeatureChunk.featureKeys) {
            conversation.setPartnerMean(featureKey, sums.get(featureKey) / count);
        }

        // Compute standard deviation
        final HashMap<String, Double> diffs = new HashMap<>();
        iterator = conversation.getPartnerFeaturesIterator();
        while (iterator.hasNext()) {
            ChunkType f = iterator.next();
            count += 1;

            for (String featureKey : FeatureChunk.featureKeys) {
                double diff = diffs.getOrDefault(featureKey, 0.0);
                diff += Math.pow(f.getFeature(featureKey) - conversation.getPartnerMean(featureKey), 2);
                diffs.put(featureKey, diff);
            }
        }
        for (String featureKey : FeatureChunk.featureKeys) {
            double std = Math.sqrt(diffs.get(featureKey) / count);
            conversation.setPartnerStd(featureKey, std);
        }

        conversation.releaseConversation();
        conversation.releaseStats();

        return message;
    }
}
