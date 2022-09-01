package org.brooklynspeech.client.audio;

import java.net.InetAddress;

import javax.sound.sampled.AudioFormat;

import org.brooklynspeech.client.audio.common.AudioSocket;
import org.brooklynspeech.client.audio.receiver.SocketAudioReceiver;
import org.brooklynspeech.client.audio.sender.MicrophoneSender;

public class MicrophoneSenderReceiverClient {

    private static String HOSTNAME = "turbo";
    private static int AUDIO_RECEIVE_PORT = 9994;
    private static int AUDIO_SEND_PORT = 9991;
    private static int BUFFER_SIZE = 1024;
    
    private static AudioFormat SEND_FORMAT = new AudioFormat(16000, 16, 1, true, false);
    private static AudioFormat RECEIVE_FORMAT = new AudioFormat(22040, 16, 1, true, false);

    public static void main(String[] args) throws Exception {
        InetAddress address = InetAddress.getByName(HOSTNAME);
        AudioSocket receiver;
        MicrophoneSender sender;

        try {
            receiver = new SocketAudioReceiver(address, AUDIO_RECEIVE_PORT, RECEIVE_FORMAT, BUFFER_SIZE);
            sender = new MicrophoneSender(address, AUDIO_SEND_PORT, SEND_FORMAT, BUFFER_SIZE);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(1);
            return;
        }

        receiver.start();
        sender.start();
    }
}
