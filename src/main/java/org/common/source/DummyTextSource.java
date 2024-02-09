package org.brooklynspeech.pipeline.source;

import org.brooklynspeech.pipeline.core.Source;
import org.brooklynspeech.pipeline.data.Turn;
import org.brooklynspeech.pipeline.data.TurnConversation;
import org.brooklynspeech.pipeline.data.Conversation;

public class DummyTextSource<ChunkType extends Turn, ConversationType extends Conversation<ChunkType>>
        extends Source<TurnConversation<ChunkType, ConversationType>> {

    private Class<ChunkType> C;
    private final ConversationType context;
    private final String text;
    private final long interval;

    public DummyTextSource(Class<ChunkType> C, ConversationType context, String text, long interval) {
        super();

        this.C = C;
        this.context = context;
        this.text = text;
        this.interval = interval;
    }

    public DummyTextSource(Class<ChunkType> C, ConversationType context) {
        this(C, context, "testing testing one two three", 5);
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(interval * 1000);

                ChunkType chunk = C.getDeclaredConstructor().newInstance();
                chunk.setSpeaker(Turn.Speaker.us);
                chunk.setTranscript(this.text);

                this.outQueue.add(new TurnConversation<>(chunk, this.context));
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }
}
