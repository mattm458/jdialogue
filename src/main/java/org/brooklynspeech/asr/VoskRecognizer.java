package org.brooklynspeech.asr;

import java.io.IOException;
import org.brooklynspeech.audio.source.Source;
import org.vosk.Model;
import org.vosk.Recognizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.brooklynspeech.asr.alignment.Transcript;

public class VoskRecognizer {

    private final Recognizer recognizer;
    private final Source source;

    private final int chunkSize;
    private boolean stopped;

    private final ObjectMapper objectMapper;

    public VoskRecognizer(Source source, int chunkSize) throws IOException {
        this.source = source;
        this.recognizer = new Recognizer(new Model("vosk-model-en-us-0.22"), 16000);
        this.recognizer.setWords(true);
        this.chunkSize = chunkSize;
        this.stopped = false;

        this.objectMapper = new ObjectMapper();
    }

    public void start() throws IOException {
        byte[] b = new byte[this.chunkSize];
        int len;

        while (!this.stopped && this.source.isOpen()) {
            len = this.source.read(b, 0, b.length);

            if (len == -1) {
                break;
            }

            if (this.recognizer.acceptWaveForm(b, len)) {
                Transcript result = this.objectMapper.readValue(this.recognizer.getResult(), Transcript.class);
                System.out.println(result.text);
            }
        }
    }

    public void stop() {
        this.stopped = true;
    }
}
