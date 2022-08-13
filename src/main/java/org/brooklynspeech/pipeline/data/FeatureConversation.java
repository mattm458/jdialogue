package org.brooklynspeech.pipeline.data;

import java.util.HashMap;
import java.util.concurrent.Semaphore;

public class FeatureConversation<T extends FeatureChunk> extends Conversation<T> {

    private final HashMap<String, Float> usMean = new HashMap<>();
    private final HashMap<String, Float> usStd = new HashMap<>();
    private final HashMap<String, Float> partnerMean = new HashMap<>();
    private final HashMap<String, Float> partnerStd = new HashMap<>();

    private final Semaphore stats = new Semaphore(1);

    public FeatureConversation(int conversationId) {
        super(conversationId);
    }

    public void acquireStats() throws InterruptedException {
        this.stats.acquire();
    }

    public float getUsMean(String key) {
        return this.usMean.get(key);
    }

    public void setUsMean(String key, float value) {
        this.usMean.put(key, value);
    }

    public float getUsStd(String key) {
        return this.usStd.get(key);
    }

    public void setUsStd(String key, float value) {
        this.usStd.put(key, value);
    }

    public float getPartnerMean(String key) {
        return this.partnerMean.get(key);
    }

    public void setPartnerMean(String key, float value) {
        this.partnerMean.put(key, value);
    }

    public float getPartnerStd(String key) {
        return this.partnerStd.get(key);
    }

    public void setPartnerStd(String key, float value) {
        this.partnerStd.put(key, value);
    }

    public void releaseStats() {
        this.stats.release();
    }
}
