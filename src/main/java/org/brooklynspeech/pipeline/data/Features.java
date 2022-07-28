package org.brooklynspeech.pipeline.data;

import org.brooklynspeech.pipeline.data.Context;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Features {

    public enum Speaker {
        us, partner
    }

    public static String[] featureKeys = new String[]{
        "pitch_mean", "pitch_range", "intensity_mean", "jitter", "shimmer", "nhr", "rate"
    };

    private final Map<String, Double> featureDict = new HashMap<>();
    private final Context context;
    private final Speaker speaker;
    private final String transcript;

    private List<double[]> embeddings;

    private byte[] wavData;
    private String wavPath;

    public Features(Context context, Speaker speaker, String transcript) {
        this.context = context;
        this.speaker = speaker;
        this.transcript = transcript;
    }

    public Features(Context context, Speaker speaker, String transcript, byte[] wavData) {
        this(context, speaker, transcript);
        this.wavData = wavData;
    }

    public Double getFeature(String key) {
        return this.featureDict.get(key);
    }

    public void setFeature(String key, double value) {
        this.featureDict.put(key, value);
    }

    public Context getContext() {
        return this.context;
    }

    public Speaker getSpeaker() {
        return this.speaker;
    }

    public String getTranscript() {
        return this.transcript;
    }

    public void setEmbeddings(List<double[]> embeddings) {
        this.embeddings = embeddings;
    }

    public byte[] getWavData() {
        return this.wavData;
    }

    public void setWavData(byte[] wavData) {
        this.wavData = wavData;
    }

    public String getWavPath() {
        return this.wavPath;
    }

    public void setWavPath(String wavPath) {
        this.wavPath = wavPath;
    }
}
