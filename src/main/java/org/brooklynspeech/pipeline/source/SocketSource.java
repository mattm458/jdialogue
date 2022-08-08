package org.brooklynspeech.pipeline.source;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

import org.brooklynspeech.pipeline.component.Source;

public class SocketSource extends Source<byte[]> {
    private final InetAddress address;
    private final int port;
    private final int bufferSize;

    public SocketSource(InetAddress address, int port, int bufferSize) {
        super();

        this.address = address;
        this.port = port;
        this.bufferSize = bufferSize;
    }

    @Override
    public void run() {
        final DatagramSocket socket;

        try {
            socket = new DatagramSocket(this.port, this.address);
        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.exit(1);
            return;
        }

        try {
            byte[] buffer = new byte[this.bufferSize];
            int length = 0;

            while (!Thread.currentThread().isInterrupted()) {
                DatagramPacket chunk = new DatagramPacket(buffer, this.bufferSize);
                socket.receive(chunk);
                length = chunk.getLength();

                this.outQueue.add(Arrays.copyOf(buffer, length));
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(1);
            return;
        } finally {
            socket.close();
        }

    }
}
