package org.brooklynspeech.example;

import org.brooklynspeech.pipeline.core.Pipeline;
import org.brooklynspeech.pipeline.sink.SocketSink;
import org.brooklynspeech.pipeline.source.SocketSource;

public class PassthroughRelayPipeline {
    protected static final int SOURCE_PORT = 9001;
    protected static final int SINK_PORT = 9002;
    protected static final int BUFFER_SIZE = 1024;

    public static void main(String[] args) throws Exception {
        final Pipeline<byte[]> passthroughPipeline = new Pipeline<>(new SocketSource(SOURCE_PORT, BUFFER_SIZE))
                .setSink(new SocketSink(SINK_PORT));

        passthroughPipeline.start();
    }
}
