package org.brooklynspeech.pipeline;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import java.io.StringReader;
import org.brooklynspeech.pipeline_old.message.Chunk;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class EmbeddingProcessor extends Processor<Chunk, Chunk> {

    private static HashMap<String, double[]> embeddings = null;
    private static double[] zeros;

    public EmbeddingProcessor() throws FileNotFoundException, IOException {
        if (EmbeddingProcessor.embeddings == null) {
            EmbeddingProcessor.embeddings = new HashMap<>();
            EmbeddingProcessor.zeros = new double[300];

            File file = new File("/home/mmcneil/datasets/glove.6B.300d.txt");
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            String line;
            while ((line = br.readLine()) != null) {
                String[] lineData = line.split(" ");
                String key = lineData[0];

                double[] emb = new double[lineData.length - 1];
                for (int i = 1; i < lineData.length; i++) {
                    emb[i - 1] = Double.parseDouble(lineData[i]);
                }
                EmbeddingProcessor.embeddings.put(key, emb);
            }
        }
    }

    @Override
    public Chunk doProcess(Chunk chunk) {
        // Documentation for the arguments below:
        // https://nlp.stanford.edu/nlp/javadoc/javanlp/edu/stanford/nlp/process/PTBTokenizer.html
        PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(
                new StringReader(chunk.getTranscript().text),
                new CoreLabelTokenFactory(),
                "ptb3Escaping=false,splitAssimilations=false"
        );

        LinkedList<double[]> embeddingsList = new LinkedList<>();

        while (ptbt.hasNext()) {
            CoreLabel label = ptbt.next();
            String value = label.value();

            double[] emb = EmbeddingProcessor.embeddings.getOrDefault(value, EmbeddingProcessor.zeros);
            embeddingsList.add(emb);

        }

        double[] embeddingsFlattened = embeddingsList.stream().flatMapToDouble(Arrays::stream).toArray();
        chunk.setEmbeddings(embeddingsFlattened, embeddingsList.size());

        return chunk;
    }
}
