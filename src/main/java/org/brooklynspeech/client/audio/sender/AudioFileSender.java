package org.brooklynspeech.client.audio.sender;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.brooklynspeech.client.audio.common.AudioSocket;

public class AudioFileSender extends AudioSocket {

    private final AudioInputStream stream;

    public AudioFileSender(InetAddress address, int port, AudioFormat format, int bufferSize, String wavFile)
            throws UnsupportedAudioFileException, IOException {
        super(address, port, format, bufferSize);

        System.out.println(wavFile);
        System.out.println(getClass().getResource(wavFile));

        this.stream = AudioSystem.getAudioInputStream(getClass().getResource(wavFile));
    }

    @Override
    public void run() {
        this.open = true;

        try {
            OutputStream outputStream = this.socket.getOutputStream();

            byte[] buffer = new byte[this.bufferSize];
            int length;

            while (this.open && (length = stream.read(buffer, 0, this.bufferSize)) >= 0) {
                if (length > 0) {
                    outputStream.write(Arrays.copyOf(buffer, length), 0, length);
                }
            }

            this.stream.close();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(1);
        }
    }
}
