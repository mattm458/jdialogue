package org.brooklynspeech.pipeline.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class Context {

    private final int conversationId;
    private final int maxLength;

    private final LinkedList<Features> features = new LinkedList<>();
    private final LinkedList<Features> partnerFeatures = new LinkedList<>();

    private final HashMap<String, Double> usMean = new HashMap<>();
    private final HashMap<String, Double> usStd = new HashMap<>();
    private final HashMap<String, Double> partnerMean = new HashMap<>();
    private final HashMap<String, Double> partnerStd = new HashMap<>();

    public Context(int conversationId, int maxLength) {
        this.conversationId = conversationId;
        this.maxLength = maxLength;
    }

    public int getConversationId() {
        return this.conversationId;
    }

    public void commitFeatures(Features f) {
        this.features.add(f);

        if (f.getSpeaker() == Features.Speaker.partner) {
            this.partnerFeatures.add(f);
        }
    }

    public Iterator<Features> getFeaturesIterator() {
        return this.features.iterator();
    }

    public Iterator<Features> getPartnerFeaturesIterator() {
        return this.partnerFeatures.iterator();
    }

    public void setPartnerMean(String key, double value) {
        this.partnerMean.put(key, value);
    }

    public double getPartnerMean(String key) {
        return this.partnerMean.get(key);
    }

    public void setPartnerStd(String key, double value) {
        this.partnerStd.put(key, value);
    }
}
