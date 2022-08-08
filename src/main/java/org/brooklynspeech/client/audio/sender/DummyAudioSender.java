package org.brooklynspeech.client.audio.sender;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.brooklynspeech.client.audio.common.AudioSocket;
import org.brooklynspeech.pipeline.source.AudioFileSource;

public class DummyAudioSender extends AudioSocket {

    private final String wavFile;
    private AudioInputStream stream;

    public DummyAudioSender(InetAddress address, int port, AudioFormat format, int bufferSize,
            String wavFile) throws IOException {
        super(address, port, format, bufferSize);
        this.wavFile = wavFile;
    }

    @Override
    public void run() {
        this.open = true;

        try {
            OutputStream outputStream = this.socket.getOutputStream();

            this.stream = AudioSystem.getAudioInputStream(this.format,
                    AudioSystem.getAudioInputStream(
                            AudioFileSource.class.getClassLoader().getResourceAsStream(this.wavFile)));

            byte[] buffer = new byte[this.bufferSize];
            int len;

            while (this.open && (len = stream.read(buffer, 0, this.bufferSize)) >= 0) {
                if (len > 0) {
                    outputStream.write(buffer, 0, len);
                }
            }

            this.stream.close();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(1);
        }
    }
}
