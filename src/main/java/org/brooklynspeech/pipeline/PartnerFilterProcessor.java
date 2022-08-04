package org.brooklynspeech.pipeline;

import org.brooklynspeech.pipeline.data.Features;

public class PartnerFilterProcessor extends Processor<Features, Features>{

    @Override
    public Features doProcess(Features features) {
        if(features.getSpeaker() == Features.Speaker.partner) {
            return null;
        }

        return features;
    }
    
}
