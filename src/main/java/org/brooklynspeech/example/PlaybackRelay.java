package org.brooklynspeech.example;

import java.net.InetAddress;

import javax.sound.sampled.AudioFormat;

import org.brooklynspeech.pipeline.core.Pipeline;
import org.brooklynspeech.pipeline.sink.SocketSink;
import org.brooklynspeech.pipeline.source.AudioFileSource;

public class PlaybackRelay {
    protected static final AudioFormat FORMAT = new AudioFormat(16000, 16, 1, true, false);
    protected static final String ip = "10.8.0.10";

    public static void main(String[] args) throws Exception {
        final InetAddress address = InetAddress.getByName(ip);

        final Pipeline<byte[]> relayPipeline = new Pipeline<>(new AudioFileSource("wav/GAME_speakerB.wav", 1024))
                .setSink(new SocketSink(address, 9001));

        relayPipeline.start();
    }
}
