package org.brooklynspeech.client.audio.sender;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.TargetDataLine;

import org.brooklynspeech.client.audio.common.AudioSocket;

public class MicrophoneSender extends AudioSocket {

    private TargetDataLine mic;

    public MicrophoneSender(InetAddress address, int port, AudioFormat format, int bufferSize) throws IOException {
        super(address, port, format, bufferSize);
    }

    @Override
    public void run() {
        this.open = true;
        try {
            OutputStream outputStream = this.socket.getOutputStream();

            this.mic = AudioSystem.getTargetDataLine(this.format);
            this.mic.open(this.format, this.bufferSize);
            this.mic.start();

            byte[] buffer = new byte[this.bufferSize];

            while (this.open && mic.isOpen()) {
                int len = mic.read(buffer, 0, this.bufferSize);

                if (len == this.bufferSize) {
                    outputStream.write(buffer, 0, len);
                }
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
