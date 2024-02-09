package org.hunterspeech.pipeline;

import org.common.core.StreamProcessor;
import org.hunterspeech.messages.HSLScore;
import org.hunterspeech.messages.HSLTurn;

public class HSLTrustworthinessModel<T extends HSLTurn, S extends HSLScore> extends StreamProcessor<T, HSLScore> {

    @Override
    public HSLScore doProcess(T message) {
        // Todo - Yuwen
        // Call with your model using features from the HSLTurn object,
        // and return a new HSLScore object containing the final score
        return new HSLScore(100.0);
    }
}
