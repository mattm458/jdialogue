package org.brooklynspeech.pipeline.sink;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import org.brooklynspeech.pipeline.core.Sink;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SocketObjectSink<T> extends Sink<T> {

    private final InetAddress address;
    private final int port ;

    public SocketObjectSink(InetAddress address, int port) throws IOException {
        super();
        this.address=address;
        this.port=port;
    }

    @Override
    public void run() {
        Socket socket ;
        try {
            socket = new Socket(address, port);

            ObjectOutputStream stream = new ObjectOutputStream(socket.getOutputStream());
            ObjectMapper mapper = new ObjectMapper();

            while (!Thread.currentThread().isInterrupted()) {
                T object = this.inQueue.take();

                stream.writeObject(mapper.writeValueAsString(object));
            }

            socket.close();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(1);
        }
    }
}
