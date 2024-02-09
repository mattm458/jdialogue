package org.brooklynspeech.jdialogue;

import java.net.InetAddress;

import javax.sound.sampled.AudioFormat;

import org.brooklynspeech.pipeline.asr.VoskProcessor;
import org.brooklynspeech.pipeline.audio.AudioSocketServerProcessor;
import org.brooklynspeech.pipeline.audio.VADProcessor;
import org.brooklynspeech.pipeline.core.Pipeline;
import org.brooklynspeech.pipeline.entrainment.NeuralEntrainmentTurnFeatures;
import org.brooklynspeech.pipeline.sink.SocketObjectSink;
import org.brooklynspeech.pipeline.source.SocketSource;

public class VoskJDialogue {
    public static final AudioFormat FORMAT = new AudioFormat(16000, 16, 1, true, false);

    private static final int RAW_TRANSMIT_PORT = 9994;
    private static final int MIC_AUDIO_IN_PORT = 9991;

    public static void main(String[] args) throws Exception {
        final Pipeline<NeuralEntrainmentTurnFeatures> pipeline = new Pipeline<>(new SocketSource(MIC_AUDIO_IN_PORT, 1024))
                .addProcessor(new AudioSocketServerProcessor(RAW_TRANSMIT_PORT))
                .addProcessor(new VoskProcessor<>(NeuralEntrainmentTurnFeatures.class,
                        "vosk-model-small-en-us-0.15", FORMAT))
                .addProcessor(new VADProcessor<>())
                .setSink(new SocketObjectSink<>(InetAddress.getByName("localhost"), 9990));

        pipeline.start();
    }
}
