package org.brooklynspeech.client.subject;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import org.brooklynspeech.audio.sink.Sink;
import org.brooklynspeech.audio.source.Source;

public class SubjectClient {

    final Source source;
    final int chunkSize;

    final ArrayList<Sink> sinks;

    public SubjectClient(Source source, int samples, int bitrate, int channels, int chunkSize)
            throws IOException, UnknownHostException {
        this.source = source;
        this.chunkSize = chunkSize;

        this.sinks = new ArrayList<>();
    }

    public void addSink(Sink sink) {
        this.sinks.add(sink);
    }

    public void start() throws SocketException, IOException {
        byte[] b = new byte[this.chunkSize];
        int len;

        while (this.source.isOpen()) {
            len = this.source.read(b, 0, b.length);

            if (len == -1) {
                break;
            }

            for (Sink s : this.sinks) {
                s.write(b, len);
            }
        }
    }
    
    public void stop() {
        this.source.stop();
        for (Sink s: this.sinks) {
            s.close();
        }
    }
}
