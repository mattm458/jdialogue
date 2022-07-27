package org.brooklynspeech.pipeline;

public class AudioPacket {

    public final byte[] bytes;
    public final int len;

    public AudioPacket(byte[] bytes, int len) {
        this.bytes = bytes;
        this.len = len;
    }
}
