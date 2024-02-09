package org.brooklynspeech.pipeline.tts;

import org.brooklynspeech.pipeline.data.BSLTurn;
import org.brooklynspeech.pipeline.data.BSLTurnConversation;
import org.brooklynspeech.pipeline.data.BSLConversation;
import org.brooklynspeech.pipeline.tts.freetts.BufferAudioPlayer;
import org.common.core.PassthroughStreamProcessor;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.util.Utilities;

public class FreeTTSProcessor<ChunkType extends BSLTurn, ConversationType extends BSLConversation<ChunkType>>
        extends PassthroughStreamProcessor<BSLTurnConversation<ChunkType, ConversationType>> {

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
    public BSLTurnConversation<ChunkType, ConversationType> doProcess(BSLTurnConversation<ChunkType, ConversationType> message) {
        final ChunkType chunk = message.chunk;

        System.out.println("FreeTTSProcessor: " + chunk.getTranscript());
        this.voice.speak(chunk.getTranscript());
        chunk.setWavData(this.audioPlayer.toByteArray());
        return message;
    }
}
