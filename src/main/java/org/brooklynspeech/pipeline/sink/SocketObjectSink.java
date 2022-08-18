package org.brooklynspeech.pipeline.sink;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.brooklynspeech.pipeline.core.Sink;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SocketObjectSink<T> extends Sink<T> {
    private final Socket socket;

    public SocketObjectSink(InetAddress address, int port) throws IOException {
        super();
        this.socket = new Socket(address, port);
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream stream = new ObjectOutputStream(this.socket.getOutputStream());

            ObjectMapper mapper = new ObjectMapper();

            while (!Thread.currentThread().isInterrupted()) {
                T object = this.inQueue.take();

                stream.writeObject(mapper.writeValueAsString(object));
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(1);
        }
    }
}
