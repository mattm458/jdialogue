package org.brooklynspeech.client.audio.common;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import javax.sound.sampled.AudioFormat;

public abstract class AudioSocket extends Thread {
    protected final DatagramSocket socket;

    protected final InetAddress remoteAudioAddress;
    protected final int remoteAudioPort;
    protected final AudioFormat format;
    protected final int bufferSize;

    protected boolean open;

    public AudioSocket(InetAddress remoteAudioAddress, int remoteAudioPort, AudioFormat format, int bufferSize)
            throws SocketException {
        this.socket = new DatagramSocket();

        this.remoteAudioAddress = remoteAudioAddress;
        this.remoteAudioPort = remoteAudioPort;
        this.format = format;
        this.bufferSize = bufferSize;

        this.open = false;
    }

    public void close() {
        this.open = false;
    }
}
