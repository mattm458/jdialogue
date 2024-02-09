package org.brooklynspeech.pipeline.entrainment;

import org.brooklynspeech.pipeline.data.BSLTurnFeatures;

public class NeuralEntrainmentTurnFeatures extends BSLTurnFeatures {
    public static float[][] mergeFeatures(NeuralEntrainmentTurnFeatures[] chunks) {
        final float[][] output = new float[chunks.length][NeuralEntrainmentTurnFeatures.featureKeys.length];

        for (int featureIdx = 0; featureIdx < NeuralEntrainmentTurnFeatures.featureKeys.length; featureIdx++) {
            String featureKey = NeuralEntrainmentTurnFeatures.featureKeys[featureIdx];

            for (int batchIdx = 0; batchIdx < chunks.length; batchIdx++) {
                output[batchIdx][featureIdx] = chunks[batchIdx].getNormalizedFeature(featureKey);
            }

        }

        return output;
    }
}
