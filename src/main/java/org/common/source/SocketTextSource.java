package org.common.source;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.brooklynspeech.pipeline.data.BSLTurn;
import org.brooklynspeech.pipeline.data.BSLTurnConversation;
import org.common.core.Source;
import org.brooklynspeech.pipeline.data.BSLConversation;

public class SocketTextSource<ChunkType extends BSLTurn, ConversationType extends BSLConversation<ChunkType>>
        extends Source<BSLTurnConversation<ChunkType, ConversationType>> {

    private final Class<ChunkType> C;
    private final ConversationType conversation;
    private final int port;

    public SocketTextSource(Class<ChunkType> C, ConversationType conversation, int port) {
        this.C = C;
        this.conversation = conversation;
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

                ChunkType chunk = C.getDeclaredConstructor().newInstance();
                chunk.setSpeaker(BSLTurn.Speaker.us);
                chunk.setTranscript(text);

                this.outQueue.add(new BSLTurnConversation<>(chunk, this.conversation));

            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
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