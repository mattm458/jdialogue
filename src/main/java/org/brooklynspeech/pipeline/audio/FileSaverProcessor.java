package org.brooklynspeech.pipeline.audio;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import org.brooklynspeech.pipeline.core.PassthroughStreamProcessor;
import org.brooklynspeech.pipeline.data.Chunk;
import org.brooklynspeech.pipeline.data.ChunkMessage;
import org.brooklynspeech.pipeline.data.Conversation;

public class FileSaverProcessor<ChunkType extends Chunk, ConversationType extends Conversation<ChunkType>>
        extends PassthroughStreamProcessor<ChunkMessage<ChunkType, ConversationType>> {

    private final AudioFormat format;

    public FileSaverProcessor(AudioFormat format) {
        this.format = format;
    }

    @Override
    public ChunkMessage<ChunkType, ConversationType> doProcess(ChunkMessage<ChunkType, ConversationType> message) {
        ChunkType chunk = message.chunk;
        ConversationType conversation = message.conversation;

        Path wavPath;
        byte[] wavData = chunk.getWavData();

        try {
            wavPath = Files.createTempFile("dialogue_" + conversation.getConversationId() + "_", ".wav");
        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.exit(1);
            return null;
        }

        chunk.setWavPath(wavPath.toString());

        ByteArrayInputStream byteStream = new ByteArrayInputStream(wavData);
        AudioInputStream audioStream = new AudioInputStream(byteStream, this.format, wavData.length);

        try {
            AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, wavPath.toFile());
        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.exit(1);
            return null;
        }

        return message;
    }

}
