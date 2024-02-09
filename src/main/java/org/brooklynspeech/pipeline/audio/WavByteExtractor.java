package org.brooklynspeech.pipeline.audio;

import org.brooklynspeech.pipeline.data.BSLTurn;
import org.brooklynspeech.pipeline.data.BSLTurnConversation;
import org.common.core.StreamProcessor;
import org.brooklynspeech.pipeline.data.BSLConversation;

public class WavByteExtractor<TurnType extends BSLTurn, ConversationType extends BSLConversation<TurnType>>
        extends StreamProcessor<BSLTurnConversation<TurnType, ConversationType>, byte[]> {

    public byte[] doProcess(BSLTurnConversation<TurnType, ConversationType> message) {
        return message.chunk.getWavData();
    }
}
