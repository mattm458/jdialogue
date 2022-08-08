package org.brooklynspeech.client.audio.sender;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;

import org.brooklynspeech.client.audio.common.AudioSocket;

public class MicrophoneSender extends AudioSocket {

    private TargetDataLine mic;
    private final String outFilename;

    public MicrophoneSender(InetAddress remoteAudioAddress, int remoteAudioPort, AudioFormat format, int bufferSize,
            String outFilename) throws SocketException {
        super(remoteAudioAddress, remoteAudioPort, format, bufferSize);
        this.outFilename = outFilename;
    }

    @Override
    public void run() {
        this.open = true;
        try {
            this.mic = AudioSystem.getTargetDataLine(this.format);
            this.mic.open(this.format, this.bufferSize);
            this.mic.start();

            byte[] buffer = new byte[this.bufferSize];

            while (this.open && mic.isOpen()) {
                int len = mic.read(buffer, 0, this.bufferSize);
                socket.send(new DatagramPacket(buffer, len, this.remoteAudioAddress, this.remoteAudioPort));
            }

            this.mic.stop();
            this.mic.close();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(1);
            return;
        }
    }
}
