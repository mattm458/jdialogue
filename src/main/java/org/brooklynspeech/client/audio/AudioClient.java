package org.brooklynspeech.client.audio;

import java.io.File;
import java.net.InetAddress;

import javax.sound.sampled.AudioFormat;

import org.brooklynspeech.client.audio.common.AudioSocket;
import org.brooklynspeech.client.audio.sender.DummyAudioSender;

public class AudioClient {

    private static String METADATA_HOSTNAME = "localhost";
    private static int METADATA_PORT = 9001;
    private static AudioFormat FORMAT = new AudioFormat(16000, 16, 1, true, true);

    public static void main(String[] args) {
        AudioSocket sender;

        try {
            sender = new DummyAudioSender(InetAddress.getByName("localhost"), 9001, FORMAT, 1024,
                    "wav/GAME_speakerB.wav");
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(1);
            return;
        }

        sender.start();
    }
}
