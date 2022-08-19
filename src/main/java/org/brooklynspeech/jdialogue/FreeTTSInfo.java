package org.brooklynspeech.jdialogue;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.util.Utilities;

public class FreeTTSInfo {
    public static void main(String[] args) {
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");

        VoiceManager voiceManager = VoiceManager.getInstance();
        Voice voice = voiceManager.getVoice(Utilities.getProperty("voice16kName", "kevin16"));
        voice.allocate();

        System.out.println("Pitch: " + voice.getPitch());
        System.out.println("Pitch range: " + voice.getPitchRange());
        System.out.println("Pitch shift: " + voice.getPitchShift());
        System.out.println("Rate: " + voice.getRate());
        System.out.println(voice.getDescription());
    }
}
