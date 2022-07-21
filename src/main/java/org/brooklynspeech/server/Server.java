package org.brooklynspeech.server;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import javax.sound.sampled.AudioFormat;
import org.brooklynspeech.pipeline.step.asr.VoskRecognizer;
import org.brooklynspeech.audio.source.Source;
import org.brooklynspeech.pipeline.step.TempFileWriter;
import org.brooklynspeech.pipeline.message.Chunk;
import org.brooklynspeech.pipeline.step.PraatFeatureExtractor;

public class Server {

    private final VoskRecognizer recognizer;
    private final TempFileWriter tempFileWriter;
    private final PraatFeatureExtractor praatFeatureExtractor;

    public Server(Source voiceIn, int chunkSize, AudioFormat format) throws IOException {
        LinkedBlockingQueue<Chunk> q1 = new LinkedBlockingQueue<>();
        LinkedBlockingQueue<Chunk> q2 = new LinkedBlockingQueue<>();
        LinkedBlockingQueue<Chunk> q3 = new LinkedBlockingQueue<>();

        this.recognizer = new VoskRecognizer(voiceIn, chunkSize, format, q1);
        this.tempFileWriter = new TempFileWriter(q1, q2, format);
        this.praatFeatureExtractor = new PraatFeatureExtractor(q2, q3);
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
        Thread tempFileWriterThread = new Thread(() -> {
            try {
                this.tempFileWriter.start();
            } catch (Exception e) {
                e.printStackTrace(System.out);
                System.exit(1);
            }
        });
        Thread praatFeatureExtractorThread = new Thread(() -> {
            try {
                this.praatFeatureExtractor.start();
            } catch (Exception e) {
                e.printStackTrace(System.out);
                System.exit(1);
            }
        });

        recognizerThread.start();
        tempFileWriterThread.start();
        praatFeatureExtractorThread.start();
    }
}
