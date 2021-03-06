package org.brooklynspeech.pipeline;

import com.orctom.vad4j.VAD;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import org.brooklynspeech.pipeline.data.Features;

public class VADProcessor extends Processor<Features, Features> {

    private final VAD vad = new VAD();
    private static final int SIZE = 1024 * 4;

    @Override
    public Features doProcess(Features features) {
        byte[] wavData = features.getWavData();

        ByteArrayInputStream stream = new ByteArrayInputStream(wavData);

        byte[] bytes = new byte[SIZE];
        int start = 0;

        int len;

        while (true) {
            len = stream.read(bytes, 0, SIZE);

            if (len <= 0) {
                break;
            }

            if (vad.isSpeech(bytes)) {
                break;
            } else {
                start += len;
            }
        }

        if (start > 0) {

            byte[] trimmed = Arrays.copyOfRange(wavData, start, wavData.length);
            features.setWavData(trimmed);
        }

        return features;
    }

}
