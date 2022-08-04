package org.brooklynspeech.text;

import java.io.IOException;

public interface TextSource {
    
    String read();

    boolean isOpen();

    void start();

    void stop();

    void close() throws IOException;
}
