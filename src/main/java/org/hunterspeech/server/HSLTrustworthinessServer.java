package org.hunterspeech.server;

import java.net.InetAddress;

import javax.sound.sampled.AudioFormat;

import org.common.core.Pipeline;
import org.common.pipeline.asr.VoskProcessor;
import org.common.pipeline.vad.VADProcessor;
import org.common.sink.SocketObjectSink;
import org.common.source.SocketByteSource;
import org.hunterspeech.messages.HSLScore;
import org.hunterspeech.messages.HSLTurn;
import org.hunterspeech.pipeline.HSLPraatFeatureProcessor;
import org.hunterspeech.pipeline.HSLTrustworthinessModel;

public class HSLTrustworthinessServer {
    private final static int IN_PORT = 9999;
    private final static int BUFFER_SIZE = 512;
    private final static String SCORE_HOST = "localhost";
    private final static int SCORE_PORT = 9992;
    public static final AudioFormat FORMAT = new AudioFormat(16000, 16, 1, true, false);

    public static void main(String[] args) throws Exception {
        final Pipeline<HSLScore> pipeline = new Pipeline<>(new SocketByteSource(IN_PORT, BUFFER_SIZE))
                .addProcessor(new VoskProcessor<>(HSLTurn.class, "vosk-model-small-en-us-0.15", FORMAT))
                .addProcessor(new VADProcessor<>())
                .addProcessor(new HSLPraatFeatureProcessor<>())
                .addProcessor(new HSLTrustworthinessModel<>())
                .setSink(new SocketObjectSink<HSLScore>(InetAddress.getByName(SCORE_HOST), SCORE_PORT));

        pipeline.start();
    }

}
