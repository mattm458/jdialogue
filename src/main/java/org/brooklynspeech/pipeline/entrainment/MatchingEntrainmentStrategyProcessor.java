package org.brooklynspeech.pipeline.entrainment;

import java.util.List;

import org.brooklynspeech.pipeline.core.Processor;
import org.brooklynspeech.pipeline.data.Conversation;
import org.brooklynspeech.pipeline.data.Chunk;

public class MatchingEntrainmentStrategyProcessor extends Processor<Chunk, Chunk> {

    @Override
    public Chunk doProcess(Chunk ourFeatures) {
        Conversation context = ourFeatures.getContext();

        List<Chunk> partnerFeatures = context.getPartnerFeatures();
        Chunk lastPartnerFeatures = partnerFeatures.get(partnerFeatures.size() - 1);

        for (String key : Chunk.featureKeys) {
            double partnerFeatureVal = lastPartnerFeatures.getFeature(key);
            double partnerMean = context.getPartnerMean(key);
            double partnerStd = context.getPartnerStd(key);

            double ourFeatureNorm = (partnerFeatureVal - partnerMean) / partnerStd;

            ourFeatures.setNormalizedFeature(key, ourFeatureNorm);
            System.out.println("Entrained " + key + ": " + ourFeatureNorm);
        }

        return ourFeatures;
    }
}
