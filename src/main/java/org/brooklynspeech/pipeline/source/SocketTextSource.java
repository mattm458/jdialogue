package org.brooklynspeech.pipeline.source;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.brooklynspeech.pipeline.core.Source;
import org.brooklynspeech.pipeline.data.Context;
import org.brooklynspeech.pipeline.data.Features;

public class SocketTextSource extends Source<Features> {

    private final Context context;
    private final int port;

    public SocketTextSource(Context context, int port) {
        this.context = context;
        this.port = port;
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(this.port);
            final Socket clientSocket = serverSocket.accept();

            ObjectInputStream stream = new ObjectInputStream(clientSocket.getInputStream());

            while (!Thread.currentThread().isInterrupted()) {
                String text = (String) stream.readObject();
                this.outQueue.add(new Features(this.context, Features.Speaker.us, text));

            }
        } catch (Exception e) {
        }

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace(System.out);
                System.exit(1);
            }
        }
    }
}