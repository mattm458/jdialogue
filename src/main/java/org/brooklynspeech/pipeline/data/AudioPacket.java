package org.brooklynspeech.pipeline.data;

public class AudioPacket implements Pipelineable {

    public final byte[] bytes;
    public final int len;

    public AudioPacket(byte[] bytes, int len) {
        this.bytes = bytes;
        this.len = len;
    }
}
