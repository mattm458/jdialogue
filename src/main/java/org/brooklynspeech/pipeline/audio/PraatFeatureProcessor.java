package org.brooklynspeech.pipeline.audio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.brooklynspeech.pipeline.data.TurnConversation;
import org.brooklynspeech.pipeline.data.Conversation;
import org.brooklynspeech.pipeline.data.TurnFeatures;
import org.common.core.PassthroughStreamProcessor;

public class PraatFeatureProcessor<ChunkType extends TurnFeatures, ConversationType extends Conversation<ChunkType>>
        extends PassthroughStreamProcessor<TurnConversation<ChunkType, ConversationType>> {

    @Override
    public TurnConversation<ChunkType, ConversationType> doProcess(TurnConversation<ChunkType, ConversationType> message) {
        ChunkType chunk = message.chunk;

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
