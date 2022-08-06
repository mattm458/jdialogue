package org.brooklynspeech.client.audio.sender;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.brooklynspeech.pipeline.source.AudioFileSource;

public class DummyAudioSender extends Sender {

    private final String wavFile;
    private AudioInputStream stream;

    public DummyAudioSender(InetAddress remoteAudioAddress, int remoteAudioPort, AudioFormat format, int bufferSize,
            String wavFile) throws SocketException {
        super(remoteAudioAddress, remoteAudioPort, format, bufferSize);
        this.wavFile = wavFile;
    }

    @Override
    public void run() {
        try {
            this.stream = AudioSystem.getAudioInputStream(this.format,
                    AudioSystem.getAudioInputStream(
                            AudioFileSource.class.getClassLoader().getResourceAsStream(this.wavFile)));

            byte[] buffer = new byte[this.bufferSize];
            int len;

            while ((len = stream.read(buffer, 0, this.bufferSize)) > 0) {
                socket.send(new DatagramPacket(buffer, len, this.remoteAudioAddress, this.remoteAudioPort));
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(1);
        }
    }

    @Override
    public void cleanup() {
        super.cleanup();

        try {
            this.stream.close();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(1);
        }
    }
}
