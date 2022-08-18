package org.brooklynspeech.pipeline.asr;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;

import org.brooklynspeech.audio.sink.ArraySink;
import org.brooklynspeech.pipeline.core.StreamProcessor;
import org.brooklynspeech.pipeline.data.Chunk;
import org.brooklynspeech.pipeline.data.Transcript;
import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VoskProcessor<ChunkType extends Chunk> extends StreamProcessor<byte[], ChunkType> {

    private final Class<ChunkType> C;

    private Recognizer recognizer;
    private final AudioFormat format;

    private final ObjectMapper objectMapper;

    private final ArraySink buffer;

    public VoskProcessor(Class<ChunkType> C, String model, AudioFormat format)
            throws IOException {
        this.C = C;
        LibVosk.setLogLevel(LogLevel.DEBUG);

        this.recognizer = new Recognizer(new Model(model), (int) format.getSampleRate());
        this.recognizer.setWords(true); // Causes Vosk to return word alignments

        this.format = format;
        this.objectMapper = new ObjectMapper();

        this.buffer = new ArraySink(1024);
    }

    @Override
    public ChunkType doProcess(byte[] input) {
        this.buffer.write(input, input.length);

        synchronized (this) {
            if (this.recognizer.acceptWaveForm(input, input.length)) {
                final String json = this.recognizer.getResult();

                final Transcript t;

                try {
                    t = this.objectMapper.readValue(json, Transcript.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace(System.out);
                    System.exit(1);
                    return null;
                }

                if (t.result.isEmpty()) {
                    return null;
                }

                System.out.println("Vosk ASR: " + t.text);

                float start = t.result.get(0).start;
                float end = t.result.get(t.result.size() - 1).end;
                int startIdx = (int) (start * this.format.getSampleRate() * this.format.getSampleSizeInBits() / 8);
                int endIdx = (int) (end * this.format.getSampleRate() * this.format.getSampleSizeInBits() / 8);

                final byte[] wavData = buffer.range(startIdx, endIdx);

                ChunkType chunk;
                try {
                    chunk = C.getDeclaredConstructor().newInstance();
                    chunk.setSpeaker(Chunk.Speaker.partner);
                    chunk.setTranscript(t.text);
                    chunk.setWavData(wavData);
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                    System.exit(1);
                    return null;
                }

                return chunk;
            }

            return null;
        }
    }

}
