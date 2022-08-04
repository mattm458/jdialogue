package org.brooklynspeech.pipeline.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.pytorch.IValue;

public class Context {

    private final int conversationId;

    private final LinkedList<Features> features = new LinkedList<>();
    private final LinkedList<Features> partnerFeatures = new LinkedList<>();
    private final LinkedList<Features> usFeatures = new LinkedList<>();

    private final HashMap<String, Double> usMean = new HashMap<>();
    private final HashMap<String, Double> usStd = new HashMap<>();
    private final HashMap<String, Double> partnerMean = new HashMap<>();
    private final HashMap<String, Double> partnerStd = new HashMap<>();

    private final HashMap<String, IValue> torchFeatures = new HashMap<>();

    public Context(int conversationId) {
        this.conversationId = conversationId;
    }

    public int getConversationId() {
        return this.conversationId;
    }

    public void commitFeatures(Features f) {
        this.features.add(f);

        if (f.getSpeaker() == Features.Speaker.partner) {
            this.partnerFeatures.add(f);
        } else {
            this.usFeatures.add(f);
        }
    }

    public Iterator<Features> getFeaturesIterator() {
        return this.features.iterator();
    }

    public Iterator<Features> getPartnerFeaturesIterator() {
        return this.partnerFeatures.iterator();
    }

    public List<Features> getPartnerFeatures() {
        return this.partnerFeatures;
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

    public IValue getTorchFeature(String key) {
        return this.torchFeatures.get(key);
    }

    public void setTorchFeature(String key, IValue value) {
        this.torchFeatures.put(key, value);
    }
}
