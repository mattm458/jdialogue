package org.brooklynspeech.pipeline.entrainment;

import org.brooklynspeech.pipeline.data.BSLFeatureConversation;

public final class NeuralEntrainmentConversation extends BSLFeatureConversation<NeuralEntrainmentTurnFeatures> {

    final static int PADDING = 1;

    private float[][] encodedHistory = null;
    private float[][][] featureEncoderHidden = null;
    private float[][][] decoderHidden = null;

    public NeuralEntrainmentConversation(int conversationId, int encodedDim) {
        super(conversationId);
        this.encodedHistory = new float[0][encodedDim];
    }

    public float[][] getEncodedHistory() {
        return encodedHistory;
    }

    public void setEncodedHistory(float[][] encodedHistory) {
        this.encodedHistory = encodedHistory;
    }

    public float[][][] getFeatureEncoderHidden() {
        return this.featureEncoderHidden;
    }

    public void setFeatureEncoderHidden(float[][][] featureEncoderHidden) {
        this.featureEncoderHidden = featureEncoderHidden;
    }

    public float[][][] getDecoderHidden() {
        return this.decoderHidden;
    }

    public void setDecoderHidden(float[][][] decoderHidden) {
        this.decoderHidden = decoderHidden;
    }

}
