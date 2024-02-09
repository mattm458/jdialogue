package org.brooklynspeech.pipeline.source;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.brooklynspeech.pipeline.core.Source;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SocketObjectSource<T> extends Source<T> {
    final int port;
    final Class<T> c;

    public SocketObjectSource(Class<T> c, int port) {
        this.c = c;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            final ServerSocket serverSocket = new ServerSocket(this.port);
            final Socket clientSocket = serverSocket.accept();

            ObjectInputStream stream = new ObjectInputStream(clientSocket.getInputStream());

            ObjectMapper mapper = new ObjectMapper();

            while (!Thread.currentThread().isInterrupted()) {
                String json = (String) stream.readObject();
                T object = mapper.readValue(json, this.c);
                this.outQueue.add(object);
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
