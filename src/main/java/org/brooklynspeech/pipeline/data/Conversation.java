package org.brooklynspeech.pipeline.data;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Conversation<T extends Chunk> {

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

    public void commitFeatures(T f) throws InterruptedException {
        this.conversation.acquire();

        this.chunks.add(f);

        if (f.getSpeaker() == Chunk.Speaker.partner) {
            this.partnerChunks.add(f);
        } else {
            this.usChunks.add(f);
        }

        this.conversation.release();
    }

    public Iterator<T> getFeaturesIterator() {
        return this.chunks.iterator();
    }

    public Iterator<T> getPartnerFeaturesIterator() {
        return this.partnerChunks.iterator();
    }

    public List<T> getPartnerFeatures() {
        return this.partnerChunks;
    }
}
