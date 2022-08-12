package org.brooklynspeech.pipeline.audio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.brooklynspeech.pipeline.core.Processor;
import org.brooklynspeech.pipeline.data.Chunk;

public class PraatFeatureProcessor extends Processor<Chunk, Chunk> {

    @Override
    public Chunk doProcess(Chunk features) {
        final String wavPath = features.getWavPath();

        final ProcessBuilder pb = new ProcessBuilder("praat", "--run", "extract_features.praat", wavPath);
        final Process p;

        try {
            p = pb.start();
        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.exit(1);
            return null;
        }

        final BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

        String line;

        try {
            while ((line = input.readLine()) != null) {
                String[] lineData = line.split(",");
                String featureName = lineData[0];
                float featureValue;

                try {
                    featureValue = Float.valueOf(lineData[1]);
                } catch (Exception e) {
                    // Occurs if a feature value is missing. If so, discard the chunk
                    return null;
                }

                features.setFeature(featureName, featureValue);
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.exit(1);
            return null;
        }

        features.setFeature(
                "rate",
                features.getFeature("duration")
                        / (features.getTranscript().chars().filter(c -> c == (int) ' ').count() + 1));

        return features;
    }
}
