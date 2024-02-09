package org.brooklynspeech.pipeline.entrainment;

import java.util.List;

import org.brooklynspeech.pipeline.data.BSLFeatureConversation;
import org.brooklynspeech.pipeline.data.BSLTurnConversation;
import org.brooklynspeech.pipeline.data.BSLTurnFeatures;
import org.common.core.PassthroughStreamProcessor;

public class MatchingEntrainmentStrategyProcessor<TurnType extends BSLTurnFeatures, ConversationType extends BSLFeatureConversation<TurnType>>
        extends PassthroughStreamProcessor<BSLTurnConversation<TurnType, ConversationType>> {

    @Override
    public BSLTurnConversation<TurnType, ConversationType> doProcess(
            BSLTurnConversation<TurnType, ConversationType> message) {
        ConversationType conversation = message.conversation;
        TurnType chunk = message.chunk;

        try {
            conversation.acquireConversation();
            conversation.acquireStats();
        } catch (InterruptedException e) {
            e.printStackTrace(System.out);
            System.exit(1);
        }

        List<TurnType> partnerChunks = conversation.getPartnerChunks();
        TurnType lastPartnerFeatures = partnerChunks.get(partnerChunks.size() - 1);

        conversation.releaseConversation();

        for (String key : TurnType.featureKeys) {
            float partnerFeatureVal = lastPartnerFeatures.getFeature(key);

            float ourFeatureNorm = (partnerFeatureVal - conversation.getPartnerMean(key))
                    / conversation.getPartnerStd(key);

            chunk.setNormalizedFeature(key, ourFeatureNorm);
        }

        conversation.releaseStats();

        return message;
    }
}
