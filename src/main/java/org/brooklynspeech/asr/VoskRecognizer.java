package org.brooklynspeech.asr;

import java.io.IOException;
import org.brooklynspeech.audio.source.Source;
import org.vosk.Model;
import org.vosk.Recognizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.concurrent.BlockingQueue;
import javax.sound.sampled.AudioFormat;
import org.brooklynspeech.asr.alignment.Transcript;
import org.brooklynspeech.audio.sink.ArraySink;
import org.brooklynspeech.pipeline.Chunk;
import org.brooklynspeech.pipeline.Context;

public class VoskRecognizer {

    private final Recognizer recognizer;
    private final Source source;
    private final AudioFormat format;
    
    private final Context context;

    private final int chunkSize;
    private boolean stopped;

    private final ObjectMapper objectMapper;

    private final BlockingQueue<Chunk> q;

    public VoskRecognizer(Source source, int chunkSize, AudioFormat format, BlockingQueue<Chunk> q) throws IOException {
        this.source = source;
        this.recognizer = new Recognizer(new Model("vosk-model-en-us-0.21"), 16000);
        this.recognizer.setWords(true);
        this.chunkSize = chunkSize;
        this.stopped = false;

        this.format = format;

        this.objectMapper = new ObjectMapper();

        this.q = q;
        this.context = new Context();
    }

    public void start() throws IOException {
        // Byte buffer for reading from the source and passing to the ASR
        byte[] b = new byte[this.chunkSize];
        int len;

        // ArraySink buffer for accumulating audio data to break into chunks for analysis
        ArraySink buffer = new ArraySink(this.chunkSize);

        while (!this.stopped && this.source.isOpen()) {
            // Read audio data from the source, and terminate if there is nothing to be read
            len = this.source.read(b, 0, b.length);
            if (len == -1) {
                break;
            }

            // Save the audio data in the buffer
            buffer.write(b, len);

            // Did the ASR recognize a complete utterance?
            if (this.recognizer.acceptWaveForm(b, len)) {
                // Retrieve and parse the ASR results
                String json = this.recognizer.getResult();
                Transcript t = this.objectMapper.readValue(json, Transcript.class);

                if (t.result.isEmpty()) {
                    continue;
                }

                System.out.println(t.text);

                // Compute the buffer start and end indices from the transcript data
                float start = t.result.get(0).start;
                float end = t.result.get(t.result.size() - 1).end;
                int startIdx = (int) (start * this.format.getSampleRate() * this.format.getSampleSizeInBits() / 8);
                int endIdx = (int) (end * this.format.getSampleRate() * this.format.getSampleSizeInBits() / 8);

                // Retrieve the wav data from the buffer for the chunk
                byte[] wavData = buffer.range(startIdx, endIdx);

                // Create a new Chunk with the transcript and wav data
                this.q.add(new Chunk(this.context, t, wavData, 0));
            }
        }
    }

    public void stop() {
        this.stopped = true;
    }
}
