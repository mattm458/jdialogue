package org.brooklynspeech.pipeline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.HashMap;
import org.brooklynspeech.pipeline_old.message.Chunk;
import org.brooklynspeech.pipeline_old.message.Feature;

public class PraatFeatureProcessor extends Processor<Chunk, Chunk> {

    @Override
    public Chunk doProcess(Chunk chunk) {
        final Path wavPath = chunk.getWavPath();

        final ProcessBuilder pb = new ProcessBuilder("praat", "--run", "extract_features.praat", wavPath.toString());
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
        HashMap<String, Float> featuresMap;

        try {
            featuresMap = new HashMap<>();

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

                featuresMap.put(featureName, featureValue);
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.exit(1);
            return null;
        }

        Feature features = new Feature();

        features.duration = featuresMap.get("dur");
        features.pitchMean = featuresMap.get("f0_mean");
        features.pitchPct5 = featuresMap.get("f0_pct5");
        features.pitchPct95 = featuresMap.get("f0_pct95");
        features.intensityMean = featuresMap.get("int_mean");
        features.jitter = featuresMap.get("jitter");
        features.shimmer = featuresMap.get("shimmer");
        features.nhr = featuresMap.get("nhr");

        features.pitchRange = features.pitchPct95 - features.pitchPct5;
        features.rate = chunk.getTranscript().result.size() / features.duration;

        chunk.setFeatures(features);
        return chunk;
    }
}
