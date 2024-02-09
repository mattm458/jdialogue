package org.brooklynspeech.client.audio;

import java.net.InetAddress;

import javax.sound.sampled.AudioFormat;

import org.brooklynspeech.client.audio.common.AudioSocket;
import org.brooklynspeech.client.audio.receiver.AudioSocketReceiver;

public class AudioReceiverClient {

    private static String HOSTNAME = "turbo";
    private static int PORT = 9992;
    private static int BUFFER_SIZE = 1024;
    private static AudioFormat FORMAT = new AudioFormat(22040, 16, 1, true, false);

    public static void main(String[] args) throws Exception {
        InetAddress address = InetAddress.getByName(HOSTNAME);
        AudioSocket receiver;

        try {
            receiver = new AudioSocketReceiver(address, PORT, FORMAT, BUFFER_SIZE);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(1);
            return;
        }

        receiver.start();
    }
}
