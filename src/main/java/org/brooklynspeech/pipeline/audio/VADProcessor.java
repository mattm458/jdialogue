package org.brooklynspeech.pipeline.audio;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

import org.brooklynspeech.pipeline.data.Turn;
import org.common.core.PassthroughStreamProcessor;

import com.orctom.vad4j.VAD;

public class VADProcessor<ChunkType extends Turn>
        extends PassthroughStreamProcessor<ChunkType> {

    private final VAD vad = new VAD();
    private static final int SIZE = 1024 * 4;

    @Override
    public ChunkType doProcess(ChunkType chunk) {
        byte[] wavData = chunk.getWavData();

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
            chunk.setWavData(trimmed);
        }

        return chunk;
    }

}
