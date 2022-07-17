package org.brooklynspeech.audio.sink;

import java.io.IOException;

public interface Sink {

    void write(byte[] b, int len) throws IOException;

    void close() throws IOException;
}
