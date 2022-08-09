package org.brooklynspeech.pipeline.asr;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;

import org.brooklynspeech.audio.sink.ArraySink;
import org.brooklynspeech.pipeline.core.Processor;
import org.brooklynspeech.pipeline.data.Context;
import org.brooklynspeech.pipeline.data.Features;
import org.brooklynspeech.pipeline.data.Transcript;
import org.vosk.Model;
import org.vosk.Recognizer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VoskProcessor extends Processor<byte[], Features> {

    private final Recognizer recognizer;
    private final AudioFormat format;

    private final ObjectMapper objectMapper;
    private final Context context;

    private final ArraySink buffer;

    public VoskProcessor(String model, AudioFormat format, Context context) throws IOException {
        this.recognizer = new Recognizer(new Model(model), (int) format.getSampleRate());
        this.recognizer.setWords(true); // Causes Vosk to return word alignments

        this.format = format;
        this.objectMapper = new ObjectMapper();
        this.context = context;

        this.buffer = new ArraySink(1024);
    }

    @Override
    public Features doProcess(byte[] input) {
        this.buffer.write(input, input.length);

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

            return new Features(this.context, Features.Speaker.partner, t.text, wavData);
        }

        return null;
    }

}
