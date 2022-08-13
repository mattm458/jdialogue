package org.brooklynspeech.pipeline.data;

public class NeuralEntrainmentChunk extends FeatureChunk {

    public static float[][] mergeFeatures(NeuralEntrainmentChunk[] chunks) {
        final float[][] output = new float[chunks.length][NeuralEntrainmentChunk.featureKeys.length];

        for (int featureIdx = 0; featureIdx < NeuralEntrainmentChunk.featureKeys.length; featureIdx++) {
            String featureKey = NeuralEntrainmentChunk.featureKeys[featureIdx];

            for (int batchIdx = 0; batchIdx < chunks.length; batchIdx++) {
                output[batchIdx][featureIdx] = chunks[batchIdx].getNormalizedFeature(featureKey);
            }

        }

        return output;
    }
}
