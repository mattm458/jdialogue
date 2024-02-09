package org.brooklynspeech.pipeline.normalization;

import java.util.HashMap;
import java.util.Iterator;

import org.brooklynspeech.pipeline.data.BSLTurnConversation;
import org.brooklynspeech.pipeline.data.BSLTurnFeatures;
import org.common.core.PassthroughStreamProcessor;
import org.brooklynspeech.pipeline.data.BSLFeatureConversation;

public class PartnerStatsProcessor<ChunkType extends BSLTurnFeatures, ConversationType extends BSLFeatureConversation<ChunkType>>
        extends PassthroughStreamProcessor<BSLTurnConversation<ChunkType, ConversationType>> {

    @Override
    public BSLTurnConversation<ChunkType, ConversationType> doProcess(BSLTurnConversation<ChunkType, ConversationType> message)
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

            for (String featureKey : BSLTurnFeatures.featureKeys) {
                float sum = sums.getOrDefault(featureKey, 0.0f);
                sum += f.getFeature(featureKey);
                sums.put(featureKey, sum);
            }
        }
        for (String featureKey : BSLTurnFeatures.featureKeys) {
            conversation.setPartnerMean(featureKey, sums.get(featureKey) / count);
        }

        // Compute standard deviation
        final HashMap<String, Float> diffs = new HashMap<>();
        iterator = conversation.getPartnerChunksIterator();
        while (iterator.hasNext()) {
            ChunkType f = iterator.next();
            count += 1;

            for (String featureKey : BSLTurnFeatures.featureKeys) {
                float diff = diffs.getOrDefault(featureKey, 0.0f);
                diff += Math.pow(f.getFeature(featureKey) - conversation.getPartnerMean(featureKey), 2);
                diffs.put(featureKey, diff);
            }
        }
        for (String featureKey : BSLTurnFeatures.featureKeys) {
            float std = (float) Math.sqrt(diffs.get(featureKey) / count);
            conversation.setPartnerStd(featureKey, std);
        }

        conversation.releaseConversation();
        conversation.releaseStats();

        return message;
    }
}
