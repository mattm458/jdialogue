package org.hunterspeech.pipeline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.common.core.PassthroughStreamProcessor;
import org.hunterspeech.messages.HSLTurn;

public class HSLPraatFeatureProcessor<TurnType extends HSLTurn>
        extends PassthroughStreamProcessor<HSLTurn> {

    @Override
    public HSLTurn doProcess(HSLTurn message) {
        final String wavPath = message.getWavPath();

        // TODO - Yuwen - change this to invoke your Praat script
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
                /*
                 * TODO - Yuwen
                 * Read output from the Praat script line-by-line and save it to the message
                 * object
                 */
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.exit(1);
            return null;
        }

        return message;
    }
}
