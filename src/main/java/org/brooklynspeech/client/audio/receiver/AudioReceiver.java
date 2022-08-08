package org.brooklynspeech.client.audio.receiver;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

import org.brooklynspeech.client.audio.common.AudioSocket;

public class AudioReceiver extends AudioSocket {
    public AudioReceiver(InetAddress remoteAudioAddress, int remoteAudioPort, AudioFormat format, int bufferSize)
            throws SocketException {
        super(remoteAudioAddress, remoteAudioPort, format, bufferSize);
    }

    @Override
    public void run() {
        this.open = true;

        try {
            final SourceDataLine spk = AudioSystem.getSourceDataLine(this.format);

            spk.open(this.format);
            spk.start();

            byte[] buffer = new byte[this.bufferSize];
            int length = 0;

            while (this.open && spk.isOpen() && (length >= 0)) {
                DatagramPacket chunk = new DatagramPacket(buffer, this.bufferSize);
                socket.receive(chunk);
                length = chunk.getLength();
                spk.write(chunk.getData(), 0, chunk.getLength());
            }

            this.open = false;
            spk.stop();
            spk.close();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(1);
            return;
        }
    }
}
