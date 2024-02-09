package org.brooklynspeech.pipeline.source;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.brooklynspeech.pipeline.core.Source;

public class SocketSource extends Source<byte[]> {
    private final int port;
    private final int bufferSize;

    public SocketSource(int port, int bufferSize) {
        super();

        this.port = port;
        this.bufferSize = bufferSize;
    }

    @Override
    public void run() {
        try {
            final ServerSocket serverSocket = new ServerSocket(this.port);
            final Socket clientSocket = serverSocket.accept();

            InputStream stream = clientSocket.getInputStream();

            while (!Thread.currentThread().isInterrupted()) {
                this.outQueue.add(stream.readNBytes(this.bufferSize));
            }

            clientSocket.close();
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(1);
            return;
        }
    }
}
