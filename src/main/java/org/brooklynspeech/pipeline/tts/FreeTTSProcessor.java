package org.brooklynspeech.pipeline.tts;

import org.brooklynspeech.pipeline.core.Processor;
import org.brooklynspeech.pipeline.data.Features;
import org.brooklynspeech.pipeline.tts.freetts.BufferAudioPlayer;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.util.Utilities;

public class FreeTTSProcessor extends Processor<Features, Features> {
    private final Voice voice;
    private final BufferAudioPlayer audioPlayer;

    public FreeTTSProcessor() {
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");

        VoiceManager voiceManager = VoiceManager.getInstance();
        this.voice = voiceManager.getVoice(Utilities.getProperty("voice16kName", "kevin16"));
        this.voice.allocate();

        this.audioPlayer = new BufferAudioPlayer();
        voice.setAudioPlayer(this.audioPlayer);
    }

    @Override
    public Features doProcess(Features features) {
        System.out.println("FreeTTSProcessor: " + features.getTranscript());
        this.voice.speak(features.getTranscript());
        features.setWavData(this.audioPlayer.toByteArray());
        return features;
    }
}
