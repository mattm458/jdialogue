package org.brooklynspeech.pipeline_old.message;

import java.nio.file.Path;
import org.brooklynspeech.pipeline_old.step.asr.alignment.Transcript;
import org.pytorch.IValue;
import org.pytorch.Tensor;

public class Chunk {

    private final Transcript transcript;
    private final int conversationId;
    private final Context context;

    private byte[] wavData;
    private Path wavPath = null;
    private Feature features;

    private Tensor embeddings;
    private int embeddingsLength;
    private final IValue speaker = IValue.from(Tensor.fromBlob(new double[]{1.0, 0.0}, new long[]{1, 2}));

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

    public void setWavData(byte[] wavData) {
        this.wavData = wavData;
    }

    public int getConversationId() {
        return conversationId;
    }

    public Feature getFeatures() {
        return this.features;
    }

    public void setFeatures(Feature features) {
        this.features = features;
    }

    public IValue getSpeaker() {
        return this.speaker;
    }

    public void setEmbeddings(Tensor embeddings, int embeddingsLength) {
        this.embeddingsLength = embeddingsLength;
        this.embeddings = embeddings;
    }

    public IValue getEmbeddings() {
        return IValue.from(this.embeddings);
    }

    public IValue getEmbeddingsLength() {
        return IValue.from(Tensor.fromBlob(
                new long[]{this.embeddingsLength},
                new long[]{1}
        ));
    }
}
