package org.brooklynspeech.pipeline.data;

import java.util.HashMap;
import java.util.Map;

public class BSLTurnFeatures extends BSLTurn {
    public static String[] featureKeys = new String[] {
            "pitch_mean", "pitch_range", "intensity_mean", "jitter", "shimmer", "nhr", "rate"
    };

    private final Map<String, Float> rawFeatures = new HashMap<>();
    private final Map<String, Float> normFeatures = new HashMap<>();
    
    public float getFeature(String key) {
        return this.rawFeatures.get(key);
    }

    public float getFeature(String key, float value) {
        return this.rawFeatures.getOrDefault(key, value);
    }

    public void setFeature(String key, float value) {
        this.rawFeatures.put(key, value);
    }

    public float getNormalizedFeature(String key) {
        return this.normFeatures.get(key);
    }

    public float getNormalizedFeature(String key, float value) {
        return this.normFeatures.getOrDefault(key, value);
    }

    public void setNormalizedFeature(String key, float value) {
        this.normFeatures.put(key, value);
    }
}
