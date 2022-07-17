package org.brooklynspeech.audio.source;

import java.io.IOException;

public interface Source {

    int read(byte[] b, int off, int len) throws IOException;

    boolean isOpen();

    void start();

    void stop();

    void close() throws IOException;
}
