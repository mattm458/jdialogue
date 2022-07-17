package org.brooklynspeech.jdialogue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import org.brooklynspeech.audio.sink.FileSink;
import org.brooklynspeech.audio.sink.SocketSink;
import org.brooklynspeech.audio.source.MicrophoneSource;

import org.brooklynspeech.client.subject.SubjectClient;

public class Jdialogue {

    public static final AudioFormat FORMAT = new AudioFormat(16000, 16, 1, true, false);

    public static void main(String[] args) {
        final AudioInputStream stream;

        try {
            stream = AudioSystem.getAudioInputStream(
                    Jdialogue.class
                            .getClassLoader()
                            .getResourceAsStream("wav/GAME_speakerA.wav")
            );
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return;
        }

        final SubjectClient subjectClient;
        try {
            subjectClient = new SubjectClient(
                    new MicrophoneSource(FORMAT), 16000, 16, 1, 1024);
            subjectClient.addSink(new SocketSink("localhost", 9001));
            subjectClient.addSink(new FileSink("test.wav", FORMAT));
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(1);
            return;
        }

        Thread thread = new Thread(() -> {
            try {
                subjectClient.start();
            } catch (Exception e) {
                e.printStackTrace(System.out);
                System.exit(1);
            }
        });

        thread.start();

        try {
            Thread.currentThread().sleep(5000);
        } catch (InterruptedException e) {
        }

        subjectClient.stop();
    }
}
