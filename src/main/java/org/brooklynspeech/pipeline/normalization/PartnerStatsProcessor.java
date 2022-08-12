package org.brooklynspeech.pipeline.normalization;

import java.util.HashMap;
import java.util.Iterator;

import org.brooklynspeech.pipeline.core.Processor;
import org.brooklynspeech.pipeline.data.Conversation;
import org.brooklynspeech.pipeline.data.Chunk;

public class PartnerStatsProcessor extends Processor<Chunk, Chunk> {

    @Override
    public Chunk doProcess(Chunk features) throws InterruptedException {
        final Conversation context = features.getContext();
        Iterator<Chunk> iterator;
        int count = 0;

        context.acquireConversation();
        context.acquireStats();

        // Compute means
        HashMap<String, Double> sums = new HashMap<>();
        iterator = context.getPartnerFeaturesIterator();
        while (iterator.hasNext()) {
            Chunk f = iterator.next();
            count += 1;

            for (String featureKey : Chunk.featureKeys) {
                double sum = sums.getOrDefault(featureKey, 0.0);
                sum += f.getFeature(featureKey);
                sums.put(featureKey, sum);
            }
        }
        for (String featureKey : Chunk.featureKeys) {
            context.setPartnerMean(featureKey, sums.get(featureKey) / count);
        }

        // Compute standard deviation
        final HashMap<String, Double> diffs = new HashMap<>();
        iterator = context.getPartnerFeaturesIterator();
        while (iterator.hasNext()) {
            Chunk f = iterator.next();
            count += 1;

            for (String featureKey : Chunk.featureKeys) {
                double diff = diffs.getOrDefault(featureKey, 0.0);
                diff += Math.pow(f.getFeature(featureKey) - context.getPartnerMean(featureKey), 2);
                diffs.put(featureKey, diff);
            }
        }
        for (String featureKey : Chunk.featureKeys) {
            double std = Math.sqrt(diffs.get(featureKey) / count);
            context.setPartnerStd(featureKey, std);
        }

        context.releaseConversation();
        context.releaseStats();

        return features;
    }
}
