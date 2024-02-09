package org.brooklynspeech.pipeline.entrainment;

import org.brooklynspeech.pipeline.data.BSLTurnConversation;
import org.brooklynspeech.pipeline.data.BSLTurnFeatures;
import org.common.core.PassthroughStreamProcessor;
import org.brooklynspeech.pipeline.data.BSLFeatureConversation;

public class NeutralEntrainmentStrategyProcessor<TurnType extends BSLTurnFeatures, ConversationType extends BSLFeatureConversation<TurnType>>
        extends PassthroughStreamProcessor<BSLTurnConversation<TurnType, ConversationType>> {

    @Override
    public BSLTurnConversation<TurnType, ConversationType> doProcess(
            BSLTurnConversation<TurnType, ConversationType> message) {
        TurnType chunk = message.chunk;

        for (String key : BSLTurnFeatures.featureKeys) {
            chunk.setNormalizedFeature(key, 0.0f);
        }

        return message;
    }
}
