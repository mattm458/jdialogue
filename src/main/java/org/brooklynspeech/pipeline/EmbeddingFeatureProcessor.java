package org.brooklynspeech.pipeline;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import java.io.StringReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.brooklynspeech.pipeline.Processor;
import org.brooklynspeech.pipeline.data.Features;

public class EmbeddingFeatureProcessor extends Processor<Features, Features> {

    private static HashMap<String, double[]> embeddings = null;
    private static double[] zeros;

    private static int embeddingDim;

    public static List<double[]> getEmbeddings(String text) {
        // Documentation for the arguments below:
        // https://nlp.stanford.edu/nlp/javadoc/javanlp/edu/stanford/nlp/process/PTBTokenizer.html
        PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(
                new StringReader(text),
                new CoreLabelTokenFactory(),
                "ptb3Escaping=false,splitAssimilations=false"
        );

        LinkedList<double[]> embeddingsList = new LinkedList<>();

        while (ptbt.hasNext()) {
            CoreLabel label = ptbt.next();
            String value = label.value();

            double[] emb = EmbeddingFeatureProcessor.embeddings.getOrDefault(value, EmbeddingFeatureProcessor.zeros);
            embeddingsList.add(emb);
        }

        return embeddingsList;
    }

    public EmbeddingFeatureProcessor(String embeddingPath, int embeddingDim) throws FileNotFoundException, IOException {
        if (EmbeddingFeatureProcessor.embeddings == null) {
            EmbeddingFeatureProcessor.embeddings = new HashMap<>();
            EmbeddingFeatureProcessor.zeros = new double[embeddingDim];
            EmbeddingFeatureProcessor.embeddingDim = embeddingDim;

            File file = new File(embeddingPath);
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
                EmbeddingFeatureProcessor.embeddings.put(key, emb);
            }
        }
    }

    @Override
    public Features doProcess(Features features) {
        List<double[]> embeddingsList = EmbeddingFeatureProcessor.getEmbeddings(features.getTranscript());
        features.setEmbeddings(embeddingsList);

        return features;
    }
}
