package org.brooklynspeech.jdialogue;

import java.net.InetAddress;

import javax.sound.sampled.AudioFormat;

import org.brooklynspeech.pipeline.asr.VoskProcessor;
import org.brooklynspeech.pipeline.audio.VADProcessor;
import org.brooklynspeech.pipeline.core.Pipeline;
import org.brooklynspeech.pipeline.entrainment.NeuralEntrainmentChunk;
import org.brooklynspeech.pipeline.sink.SocketObjectSink;
import org.brooklynspeech.pipeline.source.AudioFileSource;

public class VoskJDialogue {
    public static final AudioFormat FORMAT = new AudioFormat(16000, 16, 1, true, false);

    public static void main(String[] args) throws Exception {
        final Pipeline<NeuralEntrainmentChunk> pipeline = new Pipeline<>(
                new AudioFileSource("/wav/GAME_speakerB.wav", 1024))
                .addProcessor(new VoskProcessor<>(NeuralEntrainmentChunk.class,
                        "vosk-model-small-en-us-0.15", FORMAT))
                .addProcessor(new VADProcessor<>())
                .setSink(new SocketObjectSink<>(InetAddress.getByName("localhost"), 9001));

        pipeline.start();
    }
}
