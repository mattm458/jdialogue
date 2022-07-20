package org.brooklynspeech.pipeline;

import java.nio.file.Path;
import org.brooklynspeech.asr.alignment.Transcript;

public class Chunk {

    private final Transcript transcript;
    private final byte[] wavData;
    private final int conversationId;
    private final Context context;

    private Path wavPath = null;

    public Chunk(Context context, Transcript transcript, byte[] wavData, int conversationId) {
        this.context = context;
        this.transcript = transcript;
        this.wavData = wavData;
        this.conversationId = conversationId;
    }

    public Path getWavPath() {
        return wavPath;
    }

    public void setWavPath(Path wavPath) {
        this.wavPath = wavPath;
    }

    public Context getContext() {
        return context;
    }

    public Transcript getTranscript() {
        return transcript;
    }

    public byte[] getWavData() {
        return wavData;
    }

    public int getConversationId() {
        return conversationId;
    }
}
