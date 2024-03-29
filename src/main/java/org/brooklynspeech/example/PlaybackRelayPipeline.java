package org.brooklynspeech.example;

import org.common.core.Pipeline;
import org.common.sink.SocketSink;
import org.common.source.AudioFileSource;

public class PlaybackRelayPipeline {
    protected static final String ip = "10.8.0.10";
    protected static final int PACKET_SIZE = 1024;

    public static void main(String[] args) throws Exception {
        final Pipeline<byte[]> relayPipeline = new Pipeline<>(new AudioFileSource("wav/GAME_speakerB.wav", PACKET_SIZE))
                .setSink(new SocketSink(9001));

        relayPipeline.start();
    }
}
