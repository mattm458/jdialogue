package org.brooklynspeech.util;

import javax.sound.sampled.*;
import java.net.*;
import java.io.*;

public class AudioRecorder {

    // 16kHz, 16-bit samples, one channel, signed, big-endian
    public static final AudioFormat FORMAT = new AudioFormat(16000, 16, 1, true, true);
    // payload per udp datagram (in bytes)
    public static final int CHUNK_SIZE = 1024;
    // microphone from which audio data is captured
    TargetDataLine mic;
    // output, to remote receiver and local file
    int remoteAudioPort;
    String remoteAudioIP;
    String outFilename;

    public AudioRecorder(int remoteAudioPort, String remoteAudioIP, String outFilename) {
        this.remoteAudioPort = remoteAudioPort;
        this.remoteAudioIP = remoteAudioIP;
        this.outFilename = outFilename;
    }

    void startRecording() {
        try {
            // obtain mic and start recording
            mic = AudioSystem.getTargetDataLine(FORMAT);
            mic.open(FORMAT);
            mic.start();

            // prepare udp connection
            InetAddress address = InetAddress.getByName(remoteAudioIP);
            System.out.println("sending audio to " + remoteAudioIP + " at port " + remoteAudioPort);
            DatagramSocket socket = new DatagramSocket();

            // read audio in chunks, send them and store them in memory
            byte[] b = new byte[CHUNK_SIZE];
            int len = -1;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.out.println("AudioRecorder: starting");
            while (mic.isOpen()) {
                len = mic.read(b, 0, b.length);
                DatagramPacket chunk = new DatagramPacket(b, len, address, remoteAudioPort);
                socket.send(chunk);
                out.write(b, 0, len);
            }
            System.out.println("AudioRecorder: done recording");

            // recording has stopped, write from memory to file
            byte rec[] = out.toByteArray();
            AudioInputStream ais = new AudioInputStream(
                new ByteArrayInputStream(rec), 
                FORMAT, 
                rec.length / FORMAT.getFrameSize());
            AudioSystem.write(ais, 
                              AudioFileFormat.Type.WAVE,
                              new File(outFilename));
            System.out.println("AudioRecorder: all done");
        } catch (LineUnavailableException e) {
            System.err.println("LineUnavailableException: " + e.toString());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("IOException: " + e.toString());
            System.exit(1);
        }
    }

    void stopRecording() {
        mic.stop();
        mic.close();
    }
}
