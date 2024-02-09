package org.brooklynspeech.client.audio.receiver;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

import org.brooklynspeech.client.audio.common.AudioSocket;

public class AudioSocketReceiver extends AudioSocket {

    public AudioSocketReceiver(InetAddress address, int port, AudioFormat format, int bufferSize)
            throws IOException {
        super(address, port, format, bufferSize);
    }

    @Override
    public void run() {
        this.open = true;

        try {
            final SourceDataLine spk = AudioSystem.getSourceDataLine(this.format);
            final InputStream inputStream = this.socket.getInputStream();

            spk.open(this.format);
            spk.start();

            while (this.open && spk.isOpen()) {
                spk.write(inputStream.readNBytes(this.bufferSize), 0, this.bufferSize);
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
