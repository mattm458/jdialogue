package org.brooklynspeech.server;

import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import org.brooklynspeech.asr.VoskRecognizer;
import org.brooklynspeech.audio.source.Source;

public class Server {

    private final VoskRecognizer recognizer;

    public Server(Source voiceIn, int chunkSize, AudioFormat format) throws IOException {
        this.recognizer = new VoskRecognizer(voiceIn, chunkSize, format);
    }

    public void run() {
        Thread recognizerThread = new Thread(() -> {
            try {
                this.recognizer.start();
            } catch (Exception e) {
                e.printStackTrace(System.out);
                System.exit(1);
            }
        });

        recognizerThread.start();
    }
}
