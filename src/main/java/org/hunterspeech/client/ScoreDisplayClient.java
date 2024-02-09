package org.hunterspeech.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class ScoreDisplayClient {
    private static final String HOSTNAME = "turbo";
    private static final int PORT = 9992;

    public static void main(String[] args) throws IOException {
        InetAddress address = InetAddress.getByName(HOSTNAME);

        final Socket scoreSocket = new Socket(address, PORT);

        // todo - put code here that reads Score objects from the socket
        // and displays them graphically

        scoreSocket.close();
    }
}
