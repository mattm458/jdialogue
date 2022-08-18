package org.brooklynspeech.pipeline;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.LinkedList;

import org.brooklynspeech.pipeline.core.PassthroughStreamProcessor;
import org.brooklynspeech.pipeline.data.Chunk;
import org.brooklynspeech.pipeline.data.ChunkMessage;
import org.brooklynspeech.pipeline.data.Conversation;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;

public class EmbeddingFeatureProcessor<ChunkType extends Chunk, ConversationType extends Conversation<ChunkType>>
        extends PassthroughStreamProcessor<ChunkMessage<ChunkType, ConversationType>> {

    private static HashMap<String, float[]> embeddings = null;
    private static float[] zeros;

    // private static int embeddingDim;

    public static float[][] getEmbeddings(String text) {
        // Documentation for the arguments below:
        // https://nlp.stanford.edu/nlp/javadoc/javanlp/edu/stanford/nlp/process/PTBTokenizer.html
        PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(
                new StringReader(text),
                new CoreLabelTokenFactory(),
                "ptb3Escaping=false,splitAssimilations=false");

        LinkedList<float[]> embeddingsList = new LinkedList<>();

        while (ptbt.hasNext()) {
            CoreLabel label = ptbt.next();
            String value = label.value();

            float[] emb = EmbeddingFeatureProcessor.embeddings.getOrDefault(value, EmbeddingFeatureProcessor.zeros);
            embeddingsList.add(emb);
        }

        float[][] output = new float[embeddingsList.size()][];
        return embeddingsList.toArray(output);
    }

    public EmbeddingFeatureProcessor(String embeddingPath, int embeddingDim) throws FileNotFoundException, IOException {
        if (EmbeddingFeatureProcessor.embeddings == null) {
            EmbeddingFeatureProcessor.embeddings = new HashMap<>();
            EmbeddingFeatureProcessor.zeros = new float[embeddingDim];
            // EmbeddingFeatureProcessor.embeddingDim = embeddingDim;

            File file = new File(embeddingPath);
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            String line;
            while ((line = br.readLine()) != null) {
                String[] lineData = line.split(" ");
                String key = lineData[0];

                float[] emb = new float[lineData.length - 1];
                for (int i = 1; i < lineData.length; i++) {
                    emb[i - 1] = Float.parseFloat(lineData[i]);
                }
                EmbeddingFeatureProcessor.embeddings.put(key, emb);
            }

            br.close();
        }
    }

    @Override
    public ChunkMessage<ChunkType, ConversationType> doProcess(ChunkMessage<ChunkType, ConversationType> message) {
        ChunkType chunk = message.chunk;

        float[][] embeddingsList = EmbeddingFeatureProcessor.getEmbeddings(chunk.getTranscript());
        chunk.setEmbeddings(embeddingsList);

        return message;
    }
}
