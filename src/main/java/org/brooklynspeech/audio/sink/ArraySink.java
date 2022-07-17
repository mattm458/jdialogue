package org.brooklynspeech.audio.sink;

import java.io.IOException;
import java.util.Arrays;

public class ArraySink implements Sink {

    private byte[] buffer;
    private int count;

    public ArraySink(int chunkSize) {
        buffer = new byte[chunkSize];
        count = 0;
    }

    private void grow() {
        this.buffer = Arrays.copyOf(this.buffer, this.buffer.length * 2);
    }
    
    public int size() {
        return this.count;
    }

    @Override
    public void write(byte[] b, int len) {
        if (buffer.length < this.count + len) {
            grow();
        }

        System.arraycopy(b, 0, this.buffer, count, len);
        count += len;
    }

    @Override
    public void close() throws IOException {
    }
    
    public byte[] range(int start, int end) {
        return Arrays.copyOfRange(buffer, start, end);
    }
}
