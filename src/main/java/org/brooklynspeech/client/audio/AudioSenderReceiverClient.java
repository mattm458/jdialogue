package org.brooklynspeech.client.audio;

import java.net.InetAddress;

import javax.sound.sampled.AudioFormat;

import org.brooklynspeech.client.audio.common.AudioSocket;
import org.brooklynspeech.client.audio.receiver.AudioSocketReceiver;
import org.brooklynspeech.client.audio.sender.AudioFileSender;

public class AudioSenderReceiverClient {

    private static String HOSTNAME = "turbo";
    private static int RECEIVER_PORT = 9002;
    private static int SENDER_PORT = 9001;
    private static int BUFFER_SIZE = 1024;
    private static AudioFormat FORMAT = new AudioFormat(16000, 16, 1, true, false);

    public static void main(String[] args) throws Exception {
        InetAddress address = InetAddress.getByName(HOSTNAME);
        AudioSocket receiver;
        AudioFileSender sender;

        try {
            receiver = new AudioSocketReceiver(address, RECEIVER_PORT, FORMAT, BUFFER_SIZE);
            sender = new AudioFileSender(address, SENDER_PORT, FORMAT, BUFFER_SIZE, "/wav/GAME_speakerB.wav");
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(1);
            return;
        }

        receiver.start();
        sender.start();
    }
}
