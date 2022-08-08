package org.brooklynspeech.pipeline.source;

import java.net.InetAddress;

import javax.sound.sampled.AudioFormat;

import org.brooklynspeech.pipeline.AudioPacket;
import org.brooklynspeech.pipeline.component.Source;

public class SocketSource extends Source<AudioPacket> {
    public SocketSource(InetAddress remoteAudioAddress, int remoteAudioPort, AudioFormat format, int bufferSize) {
        super();
    }
}
