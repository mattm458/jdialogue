package org.brooklynspeech.client.audio.common;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.sound.sampled.AudioFormat;

public abstract class AudioSocket extends Thread {
    protected final Socket socket;
    protected final AudioFormat format;
    protected final int bufferSize;

    protected boolean open;

    public AudioSocket(InetAddress address, int port, AudioFormat format, int bufferSize)
            throws IOException {
        this.socket = new Socket(address, port);
        this.format = format;
        this.bufferSize = bufferSize;
        this.open = false;
    }

    public void close() {
        this.open = false;
    }
}
