package org.brooklynspeech.pipeline.sink;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;

import org.brooklynspeech.pipeline.core.Sink;

public class SocketSink extends Sink<byte[]> {
    private final DatagramSocket socket;

    public SocketSink(BlockingQueue<byte[]> queue, InetAddress address, int port) throws SocketException {
        super(queue);
        this.socket = new DatagramSocket(port, address);
    }

    @Override
    public void run() {

    }

}
