package org.brooklynspeech.audio.sink;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class SocketSink implements Sink {

    private final InetAddress address;
    private final int port;
    private final DatagramSocket socket;

    public SocketSink(String address, int port) throws UnknownHostException, SocketException {
        this.address = InetAddress.getByName(address);
        this.port = port;
        socket = new DatagramSocket();

    }

    @Override
    public void write(byte[] b, int len) throws IOException {
        DatagramPacket chunk = new DatagramPacket(b, len, this.address, this.port);
        this.socket.send(chunk);
    }

    @Override
    public void close() throws IOException {
    }
}
