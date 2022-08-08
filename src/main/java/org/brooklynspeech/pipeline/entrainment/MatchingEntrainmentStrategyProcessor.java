package org.brooklynspeech.pipeline.entrainment;

import java.util.List;

import org.brooklynspeech.pipeline.core.Processor;
import org.brooklynspeech.pipeline.data.Context;
import org.brooklynspeech.pipeline.data.Features;

public class MatchingEntrainmentStrategyProcessor extends Processor<Features, Features> {

    @Override
    public Features doProcess(Features ourFeatures) {
        Context context = ourFeatures.getContext();

        List<Features> partnerFeatures = context.getPartnerFeatures();
        Features lastPartnerFeatures = partnerFeatures.get(partnerFeatures.size() - 1);

        for (String key : Features.featureKeys) {
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
