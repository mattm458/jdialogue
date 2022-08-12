package org.brooklynspeech.pipeline.data;

import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class FeatureConversation<T extends FeatureChunk> extends Conversation<T> {

    private final HashMap<String, Double> usMean = new HashMap<>();
    private final HashMap<String, Double> usStd = new HashMap<>();
    private final HashMap<String, Double> partnerMean = new HashMap<>();
    private final HashMap<String, Double> partnerStd = new HashMap<>();

    private final Semaphore stats = new Semaphore(1);

    public FeatureConversation(int conversationId) {
        super(conversationId);
    }

    public void acquireStats() throws InterruptedException {
        this.stats.acquire();
    }

    public double getUsMean(String key) {
        return this.usMean.get(key);
    }

    public void setUsMean(String key, double value) {
        this.usMean.put(key, value);
    }

    public double getUsStd(String key) {
        return this.usStd.get(key);
    }

    public void setUsStd(String key, double value) {
        this.usStd.put(key, value);
    }

    public double getPartnerMean(String key) {
        return this.partnerMean.get(key);
    }

    public void setPartnerMean(String key, double value) {
        this.partnerMean.put(key, value);
    }

    public double getPartnerStd(String key) {
        return this.partnerStd.get(key);
    }

    public void setPartnerStd(String key, double value) {
        this.partnerStd.put(key, value);
    }

    public void releaseStats() {
        this.stats.release();
    }
}
