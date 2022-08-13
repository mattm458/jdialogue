package org.brooklynspeech.pipeline.data;

public final class NeuralEntrainmentConversation extends FeatureConversation<NeuralEntrainmentChunk> {

    final static int PADDING = 1;

    private float[][] encodedHistory;
    private final int encodedDim;

    public static class MergedEncodedHistory {
        public final int batchSize;
        public final int sequenceLength;
        public final int embeddingSize;

        public final float[][][] encodedHistory;
        public final long[] encodedHistoryLength;
        public final NeuralEntrainmentConversation[] conversations;

        public MergedEncodedHistory(int batchSize, int sequenceLength, int embeddingSize, float[][][] encodedHistory,
                long[] encodedHistoryLength, NeuralEntrainmentConversation[] conversations) {
            this.batchSize = batchSize;
            this.sequenceLength = sequenceLength;
            this.embeddingSize = embeddingSize;

            this.encodedHistory = encodedHistory;
            this.encodedHistoryLength = encodedHistoryLength;
            this.conversations = conversations;
        }
    }

    public static MergedEncodedHistory padAndMergeEncodedHistory(final NeuralEntrainmentConversation[] conversations)
            throws Exception {

        int sequenceLength = 0;
        int encodedDim = -1;
        final int batchSize = conversations.length;

        for (NeuralEntrainmentConversation c : conversations) {
            if (c.encodedDim != encodedDim) {
                if (encodedDim == -1) {
                    encodedDim = c.encodedDim;
                } else {
                    throw new Exception("NeuralEncodedConversation " + c.getConversationId()
                            + " has an inconsistent encoded dimension of " + c.encodedDim);
                }
            }

            if (c.encodedHistory.length > sequenceLength) {
                sequenceLength = c.encodedHistory.length;
            }
        }

        final float mergedHistory[][][] = new float[batchSize][sequenceLength + PADDING][encodedDim];
        final long mergedHistoryLengths[] = new long[batchSize];

        for (int i = 0; i < batchSize; i++) {
            mergedHistoryLengths[i] = conversations[i].encodedHistory.length;

            for (int j = 0; j < conversations[i].encodedHistory.length; j++) {
                for (int k = 0; k < encodedDim; k++) {
                    mergedHistory[i][j][k] = conversations[i].encodedHistory[j][k];
                }
            }
        }

        return new MergedEncodedHistory(batchSize, sequenceLength, encodedDim, mergedHistory, mergedHistoryLengths,
                conversations);
    }

    public static void updateEncodedHistory(MergedEncodedHistory mergedEncodedHistory) {
        for (int i = 0; i < mergedEncodedHistory.conversations.length; i++) {
            mergedEncodedHistory.conversations[i].setEncodedHistory(mergedEncodedHistory.encodedHistory[i]);
        }
    }

    public NeuralEntrainmentConversation(int conversationId, int encodedDim) {
        super(conversationId);
        this.encodedHistory = new float[0][encodedDim];
        this.encodedDim = encodedDim;
    }

    public float[][] getEncodedHistory() {
        return encodedHistory;
    }

    public void setEncodedHistory(float[][] encodedHistory) {
        this.encodedHistory = encodedHistory;
    }
}
