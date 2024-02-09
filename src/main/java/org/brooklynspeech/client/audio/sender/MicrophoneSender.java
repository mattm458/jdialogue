package org.brooklynspeech.client.audio.sender;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;

import org.brooklynspeech.client.audio.common.AudioSocket;

/**
 * A class capable of capturing audio data from a microphone and streaming it
 * over a socket connection.
 */
public class MicrophoneSender extends AudioSocket {

    /**
     * Create a MicrophoneSender object.
     * 
     * @param address    The IP address where audio data should be sent
     * @param port       The port where audio data should be sent
     * @param format     The audio data format
     * @param bufferSize The maximum amount of audio data that will be sent at once
     * @throws IOException
     */
    public MicrophoneSender(InetAddress address, int port, AudioFormat format, int bufferSize) throws IOException {
        super(address, port, format, bufferSize);
    }

    @Override
    public void run() {
        this.open = true;

        TargetDataLine mic;

        try {
            OutputStream outputStream = this.socket.getOutputStream();

            mic = AudioSystem.getTargetDataLine(this.format);
            mic.open(this.format, this.bufferSize);
            mic.start();

            byte[] buffer = new byte[this.bufferSize];

            while (this.open && mic.isOpen()) {
                int len = mic.read(buffer, 0, this.bufferSize);

                if (len == this.bufferSize) {
                    outputStream.write(buffer, 0, len);
                }
            }

            mic.stop();
            mic.close();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(1);
            return;
        }
    }
}
