package org.brooklynspeech.pipeline.tts.freetts;

import java.io.ByteArrayOutputStream;

import javax.sound.sampled.AudioFormat;

import com.sun.speech.freetts.audio.AudioPlayer;

public class BufferAudioPlayer implements AudioPlayer {
    private ByteArrayOutputStream stream;
    private AudioFormat format;

    @Override
    public void begin(int size) {
        this.stream = new ByteArrayOutputStream(size);
    }

    @Override
    public boolean write(byte[] audioData) {
        return write(audioData, 0, audioData.length);
    }

    @Override
    public boolean write(byte[] audioData, int offset, int size) {
        this.stream.write(audioData, offset, size);
        return true;
    }

    public byte[] toByteArray() {
        return this.stream.toByteArray();
    }

    @Override
    public void startFirstSampleTimer() {
    }

    @Override
    public void cancel() {
    }

    @Override
    public float getVolume() {
        return -1;
    }

    @Override
    public void resume() {
    }

    @Override
    public void setVolume(float volume) {
    }

    @Override
    public void showMetrics() {
    }

    @Override
    public boolean drain() {
        return true;
    }

    @Override
    public boolean end() {
        return true;
    }

    @Override
    public void setAudioFormat(AudioFormat format) {
        this.format = format;
    }

    @Override
    public AudioFormat getAudioFormat() {
        return this.format;
    }

    @Override
    public void close() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void reset() {
    }

    @Override
    public void resetTime() {
    }

    @Override
    public long getTime() {
        return 0;
    }

}
