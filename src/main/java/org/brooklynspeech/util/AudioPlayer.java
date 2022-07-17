package org.brooklynspeech.util;

import java.io.*;
import java.net.*;
import javax.sound.sampled.*;
import java.util.concurrent.Semaphore;

public class AudioPlayer {
    // speakers to which audio data is sent
    SourceDataLine spk;
    int localAudioPort;
    String localAudioIP;
    String outFilename;
    // SourceDataLine is more sensitive to interruptions than TargetDataLine in AudioRecorder;
    // Semaphore is used to ensure that line is not closed while being written to
    private Semaphore sem = new Semaphore(1, true);

    public AudioPlayer(int localAudioPort, String localAudioIP, String outFilename) {
        this.localAudioPort = localAudioPort;
        this.localAudioIP = localAudioIP;
        this.outFilename = outFilename;
    }

    private void acquireSemaphore() {
        try {
            sem.acquire();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void releaseSemaphore() {
        sem.release();
    }

    void startPlaying() {
        try {
            // obtain speakers and start playing
            spk = AudioSystem.getSourceDataLine(AudioRecorder.FORMAT);
            spk.open(AudioRecorder.FORMAT);
            spk.start();

            // prepare udp connection
            InetAddress address = InetAddress.getByName(localAudioIP);
            System.out.println("AudioPlayer: receiving audio at " + localAudioIP + ", port " + localAudioPort);
            DatagramSocket socket = new DatagramSocket(localAudioPort, address);
            byte[] b = new byte[AudioRecorder.CHUNK_SIZE];
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            int len = -1;

            // receive and play until speaker is closed or no more audio received
            while (true) {
                acquireSemaphore();
                if (!spk.isOpen() || (len != -1 && len < AudioRecorder.CHUNK_SIZE)) {
                    releaseSemaphore();
                    break;
                }
                DatagramPacket chunk = new DatagramPacket(b, b.length);
                socket.receive(chunk);
                len = chunk.getLength();
                spk.write(chunk.getData(), 0, chunk.getLength());
                out.write(chunk.getData(), 0, chunk.getLength());
                releaseSemaphore();
            }

            // recording has stopped, write from memory to file
            byte rec[] = out.toByteArray();
            AudioInputStream ais = new AudioInputStream(
                new ByteArrayInputStream(rec), 
                AudioRecorder.FORMAT, 
                rec.length / AudioRecorder.FORMAT.getFrameSize());
            AudioSystem.write(ais, 
                              AudioFileFormat.Type.WAVE,
                              new File(outFilename));
            System.out.println("AudioPlayer: all done");
        } catch (SocketTimeoutException e) {
            System.err.println("SocketTimeoutException: " + e.toString());
            System.exit(1);
        } catch (LineUnavailableException e) {
            System.err.println("LineUnavailableException: " + e.toString());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("IOException: " + e.toString());
            System.exit(1);
        }
    }

    void stopPlaying() {
        acquireSemaphore();
        spk.stop();
        spk.close();
        releaseSemaphore();
    }
}
