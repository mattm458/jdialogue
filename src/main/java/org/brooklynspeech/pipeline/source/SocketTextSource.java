package org.brooklynspeech.pipeline.source;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.brooklynspeech.pipeline.core.Source;
import org.brooklynspeech.pipeline.data.Turn;
import org.brooklynspeech.pipeline.data.TurnConversation;
import org.brooklynspeech.pipeline.data.Conversation;

public class SocketTextSource<ChunkType extends Turn, ConversationType extends Conversation<ChunkType>>
        extends Source<TurnConversation<ChunkType, ConversationType>> {

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
                chunk.setSpeaker(Turn.Speaker.us);
                chunk.setTranscript(text);

                this.outQueue.add(new TurnConversation<>(chunk, this.conversation));

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