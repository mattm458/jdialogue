package org.brooklynspeech.pipeline;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import java.io.StringReader;
import org.brooklynspeech.pipeline_old.message.Chunk;

public class EmbeddingProcessor extends Processor<Chunk, Chunk> {

    public EmbeddingProcessor() {

    }

    @Override
    public Chunk doProcess(Chunk chunk) {
        // Documentation for the arguments below:
        // https://nlp.stanford.edu/nlp/javadoc/javanlp/edu/stanford/nlp/process/PTBTokenizer.html
        PTBTokenizer<CoreLabel> ptbt = new PTBTokenizer<>(
                new StringReader(chunk.getTranscript().text),
                new CoreLabelTokenFactory(),
                "ptb3Escaping=false,splitAssimilations=false");

        while (ptbt.hasNext()) {
            CoreLabel label = ptbt.next();
            System.out.println(label);
        }

        return chunk;
    }
}
