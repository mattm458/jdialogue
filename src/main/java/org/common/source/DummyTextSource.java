package org.common.source;

import org.brooklynspeech.pipeline.data.BSLTurn;
import org.brooklynspeech.pipeline.data.BSLTurnConversation;
import org.common.core.Source;
import org.brooklynspeech.pipeline.data.BSLConversation;

public class DummyTextSource<TurnType extends BSLTurn, ConversationType extends BSLConversation<TurnType>>
        extends Source<BSLTurnConversation<TurnType, ConversationType>> {

    private Class<TurnType> C;
    private final ConversationType context;
    private final String text;
    private final long interval;

    public DummyTextSource(Class<TurnType> C, ConversationType context, String text, long interval) {
        super();

        this.C = C;
        this.context = context;
        this.text = text;
        this.interval = interval;
    }

    public DummyTextSource(Class<TurnType> C, ConversationType context) {
        this(C, context, "testing testing one two three", 5);
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Thread.sleep(interval * 1000);

                TurnType chunk = C.getDeclaredConstructor().newInstance();
                chunk.setSpeaker(BSLTurn.Speaker.us);
                chunk.setTranscript(this.text);

                this.outQueue.add(new BSLTurnConversation<>(chunk, this.context));
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }
}
