package org.brooklynspeech.client.subject;

public class SubjectClient  {

//    final AudioSource source;
//    final int chunkSize;
//
//    final ArrayList<Sink> sinks;
//
//    boolean stop;
//
//    public SubjectClient(AudioSource source, int samples, int bitrate, int channels, int chunkSize)
//            throws IOException, UnknownHostException {
//        this.source = source;
//        this.chunkSize = chunkSize;
//
//        this.sinks = new ArrayList<>();
//
//        this.stop = false;
//    }
//
//    public void addSink(Sink sink) {
//        this.sinks.add(sink);
//    }
//
//    public void start() throws SocketException, IOException {
//        this.source.start();
//        
//        byte[] b = new byte[this.chunkSize];
//        int len;
//
//        while (!this.stop && this.source.isOpen()) {
//            len = this.source.read(b, 0, b.length);
//
//            if (len == -1) {
//                break;
//            }
//
//            for (Sink s : this.sinks) {
//                s.write(b, len);
//            }
//        }
//
//        this.source.stop();
//        this.source.close();
//
//        for (Sink s : this.sinks) {
//            s.close();
//        }
//    }
//
//    @Override
//    public void stop() {
//        this.stop = true;
//    }
}
