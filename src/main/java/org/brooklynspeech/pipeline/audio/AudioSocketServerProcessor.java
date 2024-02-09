package org.brooklynspeech.pipeline.audio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.common.core.PassthroughStreamProcessor;

public class AudioSocketServerProcessor extends PassthroughStreamProcessor<byte[]> {

    private final int port;

    private ServerSocket serverSocket;
    private Socket socket;
    private OutputStream stream;

    public AudioSocketServerProcessor(int port) throws IOException {
        this.port = port;
    }

    @Override
    protected void setup() {
        try {
            this.serverSocket = new ServerSocket(port);
            this.socket = this.serverSocket.accept();
            this.stream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.exit(1);
        }

    }

    @Override
    public byte[] doProcess(byte[] bytes) {
        try {
            this.stream.write(bytes);
        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.exit(1);
        }

        return bytes;
    }
}
