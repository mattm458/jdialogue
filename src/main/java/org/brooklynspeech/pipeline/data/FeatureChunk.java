package org.brooklynspeech.pipeline.data;

import java.util.HashMap;
import java.util.Map;

public class FeatureChunk extends Chunk {
    public static String[] featureKeys = new String[] {
            "pitch_mean", "pitch_range", "intensity_mean", "jitter", "shimmer", "nhr", "rate"
    };

    private final Map<String, Double> rawFeatures = new HashMap<>();
    private final Map<String, Double> normFeatures = new HashMap<>();

    public Double getFeature(String key) {
        return this.rawFeatures.get(key);
    }

    public void setFeature(String key, double value) {
        this.rawFeatures.put(key, value);
    }

    public Double getNormalizedFeature(String key) {
        return this.normFeatures.get(key);
    }

    public void setNormalizedFeature(String key, double value) {
        this.normFeatures.put(key, value);
    }
}
