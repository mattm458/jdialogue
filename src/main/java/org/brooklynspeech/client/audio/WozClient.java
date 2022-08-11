package org.brooklynspeech.client.audio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;

import javax.sound.sampled.AudioFormat;

import org.brooklynspeech.client.audio.common.AudioSocket;
import org.brooklynspeech.client.audio.receiver.SocketAudioReceiver;
import org.brooklynspeech.client.audio.sender.TextSender;

public class WozClient {
    private static String HOSTNAME = "turbo";
    private static int AUDIO_RECEIVER_PORT = 9002;
    private static int TEXT_SENDER_PORT = 9001;
    private static int BUFFER_SIZE = 1024;
    private static AudioFormat FORMAT = new AudioFormat(16000, 16, 1, true, false);

    public static void main(String[] args) throws Exception {
        InetAddress address = InetAddress.getByName(HOSTNAME);

        AudioSocket receiver;
        TextSender textSender;

        try {
            receiver = new SocketAudioReceiver(address, AUDIO_RECEIVER_PORT, FORMAT, BUFFER_SIZE);
            textSender = new TextSender(address, TEXT_SENDER_PORT);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(1);
            return;
        }

        receiver.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        while (true) {
            System.out.print("> ");
            textSender.send(reader.readLine());
        }
    }
}
