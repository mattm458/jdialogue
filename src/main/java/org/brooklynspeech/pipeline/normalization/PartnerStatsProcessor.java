package org.brooklynspeech.pipeline.normalization;

import java.util.HashMap;
import java.util.Iterator;

import org.brooklynspeech.pipeline.core.Processor;
import org.brooklynspeech.pipeline.data.Context;
import org.brooklynspeech.pipeline.data.Features;

public class PartnerStatsProcessor extends Processor<Features, Features> {

    @Override
    public Features doProcess(Features features) throws InterruptedException {
        final Context context = features.getContext();
        Iterator<Features> iterator;
        int count = 0;

        context.acquireConversation();
        context.acquireStats();

        // Compute means
        HashMap<String, Double> sums = new HashMap<>();
        iterator = context.getPartnerFeaturesIterator();
        while (iterator.hasNext()) {
            Features f = iterator.next();
            count += 1;

            for (String featureKey : Features.featureKeys) {
                double sum = sums.getOrDefault(featureKey, 0.0);
                sum += f.getFeature(featureKey);
                sums.put(featureKey, sum);
            }
        }
        for (String featureKey : Features.featureKeys) {
            context.setPartnerMean(featureKey, sums.get(featureKey) / count);
        }

        // Compute standard deviation
        final HashMap<String, Double> diffs = new HashMap<>();
        iterator = context.getPartnerFeaturesIterator();
        while (iterator.hasNext()) {
            Features f = iterator.next();
            count += 1;

            for (String featureKey : Features.featureKeys) {
                double diff = diffs.getOrDefault(featureKey, 0.0);
                diff += Math.pow(f.getFeature(featureKey) - context.getPartnerMean(featureKey), 2);
                diffs.put(featureKey, diff);
            }
        }
        for (String featureKey : Features.featureKeys) {
            double std = Math.sqrt(diffs.get(featureKey) / count);
            context.setPartnerStd(featureKey, std);
        }

        context.releaseConversation();
        context.releaseStats();

        return features;
    }
}
