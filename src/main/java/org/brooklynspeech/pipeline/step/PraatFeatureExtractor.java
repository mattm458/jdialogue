package org.brooklynspeech.pipeline.step;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.concurrent.BlockingQueue;
import org.brooklynspeech.pipeline.message.Chunk;

public class PraatFeatureExtractor {

    private final BlockingQueue<Chunk> inQueue, outQueue;
    private boolean running;

    public PraatFeatureExtractor(BlockingQueue<Chunk> inQueue, BlockingQueue<Chunk> outQueue) {
        this.inQueue = inQueue;
        this.outQueue = outQueue;
        this.running = false;
    }

    public void start() {
        this.running = true;
        while (this.running) {
            final Chunk chunk;

            try {
                chunk = inQueue.take();
                Path wavPath = chunk.getWavPath();

                ProcessBuilder pb = new ProcessBuilder("praat", "--run,","extract_features.praat", wavPath.toString());
                Process p = pb.start();
                
                BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;

                try {
                    while ((line = input.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace(System.out);
                System.exit(1);
                return;
            }
        }
    }

    public void stop() {
        this.running = false;
    }
}
