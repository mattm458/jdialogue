package org.brooklynspeech.jdialogue;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import org.brooklynspeech.audio.source.AudioInputStreamSource;
import org.brooklynspeech.server.Server;

public class Jdialogue {

    public static final AudioFormat FORMAT = new AudioFormat(16000, 16, 1, true, false);

    public static void main(String[] args) {
//        try {
//            TarsosSubjectClient client = new TarsosSubjectClient(16000, 1024);
//            client.start();
//        } catch (Exception e) {
//            e.printStackTrace(System.out);
//            System.exit(1);
//        }






//        final AudioInputStream stream;
//
//        try {
//            stream = AudioSystem.getAudioInputStream(
//                    Jdialogue.class
//                            .getClassLoader()
//                            .getResourceAsStream("wav/GAME_speakerA.wav")
//            );
//        } catch (Exception e) {
//            e.printStackTrace(System.out);
//            return;
//        }
//
//        final SubjectClient subjectClient;
//        try {
//            subjectClient = new SubjectClient(
//                    new MicrophoneSource(FORMAT), 16000, 16, 1, 1024);
//            subjectClient.addSink(new SocketSink("localhost", 9001));
//            subjectClient.addSink(new FileSink("test.wav", FORMAT));
//        } catch (Exception e) {
//            e.printStackTrace(System.out);
//            System.exit(1);
//            return;
//        }
//
//        Thread thread = new Thread(() -> {
//            try {
//                subjectClient.start();
//            } catch (Exception e) {
//                e.printStackTrace(System.out);
//                System.exit(1);
//            }
//        });
//
//        thread.start();
//
//        try {
//            Thread.currentThread().sleep(5000);
//        } catch (InterruptedException e) {
//        }
//
//        subjectClient.stop();



        final AudioInputStream stream;
        final Server server;

        try {
            stream = AudioSystem.getAudioInputStream(
                    Jdialogue.class
                            .getClassLoader()
                            .getResourceAsStream("wav/GAME_speakerB.wav")
            );
            server = new Server(new AudioInputStreamSource(stream), 2048, FORMAT);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(1);
            return;
        }
        
        server.run();




//        final AudioInputStream stream;
//        final Server server;
//
//        try {
//            stream = AudioSystem.getAudioInputStream(
//                    Jdialogue.class
//                            .getClassLoader()
//                            .getResourceAsStream("wav/GAME_speakerA.wav")
//            );
//            server = new Server(stream, 1024, FORMAT);
//        } catch (Exception e) {
//            e.printStackTrace(System.out);
//            System.exit(1);
//            return;
//        }
        
        
        
        
//        try {
//            ProcessBuilder pb = new ProcessBuilder("praat", "--run");
//            Process p = pb.start();
//            p.waitFor();
//            
//            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
//            String line;
//            while ((line = input.readLine()) != null) {
//                System.out.println(line);
//            }
//        } catch (Exception e) {
//            e.printStackTrace(System.out);
//            System.exit(1);
//        }
    }
}
