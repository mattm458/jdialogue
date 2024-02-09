package org.brooklynspeech.pipeline.data;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class BSLConversation<TurnType extends BSLTurn> {

    private final int conversationId;

    private final LinkedList<TurnType> chunks = new LinkedList<>();
    private final LinkedList<TurnType> partnerChunks = new LinkedList<>();
    private final LinkedList<TurnType> usChunks = new LinkedList<>();

    private final Semaphore conversation = new Semaphore(1);

    public BSLConversation(int conversationId) {
        this.conversationId = conversationId;
    }

    public int getConversationId() {
        return this.conversationId;
    }

    public void acquireConversation() throws InterruptedException {
        this.conversation.acquire();
    }

    public void releaseConversation() {
        this.conversation.release();
    }

    public void commitChunk(TurnType f) throws InterruptedException {
        this.conversation.acquire();

        this.chunks.add(f);

        if (f.getSpeaker() == BSLTurn.Speaker.partner) {
            this.partnerChunks.add(f);
        } else {
            this.usChunks.add(f);
        }

        this.conversation.release();
    }

    public int getChunkSize() {
        return this.chunks.size();
    }

    public Iterator<TurnType> getChunksIterator() {
        return this.chunks.iterator();
    }

    public Iterator<TurnType> getPartnerChunksIterator() {
        return this.partnerChunks.iterator();
    }

    public List<TurnType> getPartnerChunks() {
        return this.partnerChunks;
    }
}
