package org.brooklynspeech.pipeline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.brooklynspeech.pipeline.core.PassthroughStreamProcessor;
import org.brooklynspeech.pipeline.data.Chunk;
import org.brooklynspeech.pipeline.data.ChunkMessage;
import org.brooklynspeech.pipeline.data.Conversation;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;

public class EmbeddingFeatureProcessor<ChunkType extends Chunk, ConversationType extends Conversation<ChunkType>>
        extends PassthroughStreamProcessor<ChunkMessage<ChunkType, ConversationType>> {

    private final Map<String, float[]> embeddings;
    private final float[] zeros;

    public EmbeddingFeatureProcessor(Map<String, float[]> embeddings, int embeddingDim) {
        this.embeddings = embeddings;
        this.zeros = new float[embeddingDim];
    }

    @Override
    public ChunkMessage<ChunkType, ConversationType> doProcess(ChunkMessage<ChunkType, ConversationType> message) {
        ChunkType chunk = message.chunk;
        chunk.setEmbeddings(getEmbeddings(chunk.getTranscript()));
        return message;
    }

    private float[][] getEmbeddings(String text) {
        // Documentation for the arguments below:
        // https://nlp.stanford.edu/nlp/javadoc/javanlp/edu/stanford/nlp/process/PTBTokenizer.html
        PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(
                new StringReader(text),
                new CoreLabelTokenFactory(),
                "ptb3Escaping=false,splitAssimilations=false");

        LinkedList<float[]> embeddingsList = new LinkedList<>();

        while (ptbt.hasNext()) {
            String value = ptbt.next().value();
            embeddingsList.add(embeddings.getOrDefault(value, this.zeros));
        }

        float[][] output = new float[embeddingsList.size()][];
        return embeddingsList.toArray(output);
    }

    public static Map<String, float[]> load(String embeddingPath, int embeddingDim)
            throws FileNotFoundException, IOException {
        HashMap<String, float[]> embeddings = new HashMap<>();

        File file = new File(embeddingPath);
        BufferedReader br = new BufferedReader(new FileReader(file));

        String line;
        while ((line = br.readLine()) != null) {
            String[] lineData = line.split(" ");
            String key = lineData[0];

            float[] emb = new float[lineData.length - 1];
            for (int i = 1; i < lineData.length; i++) {
                emb[i - 1] = Float.parseFloat(lineData[i]);
            }

            embeddings.put(key, emb);
        }

        br.close();
        return embeddings;
    }
}
