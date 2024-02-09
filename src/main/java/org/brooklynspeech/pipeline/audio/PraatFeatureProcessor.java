package org.brooklynspeech.pipeline.audio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.brooklynspeech.pipeline.data.BSLTurnConversation;
import org.brooklynspeech.pipeline.data.BSLConversation;
import org.brooklynspeech.pipeline.data.BSLTurnFeatures;
import org.common.core.PassthroughStreamProcessor;

public class PraatFeatureProcessor<TurnType extends BSLTurnFeatures, ConversationType extends BSLConversation<TurnType>>
        extends PassthroughStreamProcessor<BSLTurnConversation<TurnType, ConversationType>> {

    @Override
    public BSLTurnConversation<TurnType, ConversationType> doProcess(BSLTurnConversation<TurnType, ConversationType> message) {
        TurnType chunk = message.chunk;

        final String wavPath = chunk.getWavPath();

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

                chunk.setFeature(featureName, featureValue);
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.exit(1);
            return null;
        }

        chunk.setFeature(
                "rate",
                chunk.getFeature("duration")
                        / (chunk.getTranscript().chars().filter(c -> c == (int) ' ').count() + 1));

        return message;
    }
}
