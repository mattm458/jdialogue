package org.common.sink;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import org.common.core.Sink;

public class SocketSink extends Sink<byte[]> {

    private final ServerSocket serverSocket;

    public SocketSink(int port) throws IOException {
        super();
        this.serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        try {
            Socket clientSocket = this.serverSocket.accept();
            OutputStream stream = clientSocket.getOutputStream();

            while (!Thread.currentThread().isInterrupted()) {
                byte[] data = this.inQueue.take();

                if (data.length < 1024) {
                    data = Arrays.copyOf(data, 1024);
                }
                
                stream.write(data);
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(1);
        }
    }

}
