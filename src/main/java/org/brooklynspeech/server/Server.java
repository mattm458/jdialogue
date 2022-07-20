package org.brooklynspeech.server;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import javax.sound.sampled.AudioFormat;
import org.brooklynspeech.asr.VoskRecognizer;
import org.brooklynspeech.audio.source.Source;
import org.brooklynspeech.filewriter.TempFileWriter;
import org.brooklynspeech.pipeline.Chunk;

public class Server {

    private final VoskRecognizer recognizer;
    private final TempFileWriter tempFileWriter;

    public Server(Source voiceIn, int chunkSize, AudioFormat format) throws IOException {
        LinkedBlockingQueue<Chunk> q = new LinkedBlockingQueue<>();

        this.recognizer = new VoskRecognizer(voiceIn, chunkSize, format, q);
        this.tempFileWriter = new TempFileWriter(q, format);
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

        recognizerThread.start();
        tempFileWriterThread.start();
    }
}
