package org.brooklynspeech.pipeline.sink;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;

import org.brooklynspeech.pipeline.core.Sink;

public class SocketSink extends Sink<byte[]> {

    private final DatagramSocket socket;
    private final InetAddress address;
    private final int port;

    public SocketSink(BlockingQueue<byte[]> queue, InetAddress address, int port) throws SocketException {
        super();

        this.socket = new DatagramSocket();
        this.address = address;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                byte[] buffer = this.inQueue.take();
                this.socket.send(new DatagramPacket(buffer, buffer.length, this.address, this.port));
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(1);
        }
    }

}
