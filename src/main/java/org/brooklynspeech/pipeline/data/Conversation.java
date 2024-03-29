package org.brooklynspeech.pipeline.data;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Conversation<T extends Turn> {

    private final int conversationId;

    private final LinkedList<T> chunks = new LinkedList<>();
    private final LinkedList<T> partnerChunks = new LinkedList<>();
    private final LinkedList<T> usChunks = new LinkedList<>();

    private final Semaphore conversation = new Semaphore(1);

    public Conversation(int conversationId) {
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

    public void commitChunk(T f) throws InterruptedException {
        this.conversation.acquire();

        this.chunks.add(f);

        if (f.getSpeaker() == Turn.Speaker.partner) {
            this.partnerChunks.add(f);
        } else {
            this.usChunks.add(f);
        }

        this.conversation.release();
    }

    public int getChunkSize() {
        return this.chunks.size();
    }

    public Iterator<T> getChunksIterator() {
        return this.chunks.iterator();
    }

    public Iterator<T> getPartnerChunksIterator() {
        return this.partnerChunks.iterator();
    }

    public List<T> getPartnerChunks() {
        return this.partnerChunks;
    }
}
