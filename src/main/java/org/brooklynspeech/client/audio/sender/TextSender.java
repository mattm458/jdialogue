package org.brooklynspeech.client.audio.sender;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class TextSender {

    private final Socket socket;
    private final ObjectOutputStream stream;

    public TextSender(InetAddress address, int port) throws IOException {
        this.socket = new Socket(address, port);
        this.stream = new ObjectOutputStream(this.socket.getOutputStream());
    }

    public void send(String string) throws IOException {
        this.stream.writeObject(string);
    }
}
