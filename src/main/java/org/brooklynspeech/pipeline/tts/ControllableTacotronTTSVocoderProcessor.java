package org.brooklynspeech.pipeline.tts;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import org.brooklynspeech.pipeline.data.BSLTurnConversation;
import org.brooklynspeech.pipeline.data.BSLTurnFeatures;
import org.common.core.PassthroughStreamProcessor;
import org.brooklynspeech.pipeline.data.BSLFeatureConversation;
import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;

public class ControllableTacotronTTSVocoderProcessor<ChunkType extends BSLTurnFeatures, ConversationType extends BSLFeatureConversation<ChunkType>>
        extends PassthroughStreamProcessor<BSLTurnConversation<ChunkType, ConversationType>> {

    private final Module model;
    private final HashMap<Character, Long> charMap;

    private final int mels;
    private final int maxMelLen;

    private boolean hasEndToken = false;
    private char endToken;

    public ControllableTacotronTTSVocoderProcessor(String modelPath, String allowedChars, int mels, int maxMelLen) {
        this.model = Module.load(modelPath);

        this.mels = mels;
        this.maxMelLen = maxMelLen;

        // Replicate the behavior of scikit-learn's OrdinalEncoder class.
        // The encoder reorders the input string alphabetically before mapping each
        // character to a number.
        char[] allowedCharsSorted = allowedChars.toCharArray();
        Arrays.sort(allowedCharsSorted);

        this.charMap = new HashMap<>();
        for (int i = 0; i < allowedCharsSorted.length; i++) {
            // The plus one replicates Tacotron encoding behavior. It does this so 0 can be
            // a valueless padding character, though this processor does not currently use
            // input padding because it does not batch inputs
            this.charMap.put(allowedCharsSorted[i], (long) i + 1);
        }
    }

    public ControllableTacotronTTSVocoderProcessor(String modelPath, String allowedChars, char endToken, int mels,
            int maxMelLen) {
        this(modelPath, allowedChars + endToken, mels, maxMelLen);

        this.hasEndToken = true;
        this.endToken = endToken;
    }

    public ControllableTacotronTTSVocoderProcessor(String modelPath) {
        // Default allowed characters borrowed from the Tacotron 2 source.
        this(modelPath, "!'(),.:;? \\-ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz", '^', 80, 500);
    }

    public BSLTurnConversation<ChunkType, ConversationType> doProcess(BSLTurnConversation<ChunkType, ConversationType> message) {
        long startTime = System.currentTimeMillis();

        ChunkType chunk = message.chunk;
        ConversationType conversation = message.conversation;

        String transcript = chunk.getTranscript();
        System.out.println("ControllableTacotronTTSVocoderProcessor: " + transcript);

        LinkedList<Long> charsIdx = new LinkedList<>();
        for (int i = 0; i < transcript.length(); i++) {
            char c = transcript.charAt(i);

            if (this.charMap.containsKey(c)) {
                charsIdx.add(this.charMap.get(c));
            }
        }

        if (this.hasEndToken) {
            charsIdx.add(this.charMap.get(this.endToken));
        }
        long[] charsIdxArr = new long[charsIdx.size()];
        Arrays.setAll(charsIdxArr, charsIdx::get);

        HashMap<String, IValue> ttsData = new HashMap<>();
        ttsData.put("chars_idx", IValue.from(Tensor.fromBlob(charsIdxArr, new long[] { 1, charsIdxArr.length })));
        ttsData.put("mel_spectrogram", IValue.from(
                Tensor.fromBlob(new float[this.mels * this.maxMelLen], new long[] { 1, this.maxMelLen, this.mels })));
        IValue ttsDataIValue = IValue.dictStringKeyFrom(ttsData);

        float[] featuresArr = new float[BSLTurnFeatures.featureKeys.length];
        for (int i = 0; i < BSLTurnFeatures.featureKeys.length; i++) {
            featuresArr[i] = chunk.getNormalizedFeature(BSLTurnFeatures.featureKeys[i]);
        }

        HashMap<String, IValue> ttsMetadata = new HashMap<>();
        ttsMetadata.put("chars_idx_len",
                IValue.from(Tensor.fromBlob(new long[] { charsIdxArr.length }, new long[] { 1 })));
        ttsMetadata.put("mel_spectrogram_len",
                IValue.from(Tensor.fromBlob(new long[] { this.maxMelLen }, new long[] { 1 })));
        ttsMetadata.put("features", IValue.from(Tensor.fromBlob(featuresArr, new long[] { 1, featuresArr.length })));
        IValue ttsMetadataIValue = IValue.dictStringKeyFrom(ttsMetadata);

        // Forward pass parameters
        IValue teacherForcing = IValue.from(false);
        IValue sigmoidGates = IValue.from(false);
        String melPath = null;

        IValue saveMel = IValue.from(melPath);
        IValue forceToGpu = IValue.from("cuda:1");
        IValue ttsInput = IValue.tupleFrom(ttsDataIValue, ttsMetadataIValue);

        IValue result = this.model.forward(ttsInput, teacherForcing, sigmoidGates, saveMel, forceToGpu);

        // Result tuple: mels, mels_post, gates, alignments
        IValue resultsTuple[] = result.toTuple();

        // Find the gate index indicating the spectrogram ending
        float[] wav = resultsTuple[1].toTensor().getDataAsFloatArray();
        byte[] waveform = new byte[wav.length * 4];

        int byteIdx = 0;
        for (float f : wav) {
            short s = (short) Math.floor(f * 32767);

            byte[] bytes = shortToByteArray(s);
            for (byte b : bytes) {
                waveform[byteIdx] = b;
                byteIdx++;
            }
        }

        chunk.setWavData(waveform);

        long endTime = System.currentTimeMillis();

        System.out.println((endTime - startTime));

        return message;
    }

    public static byte[] shortToByteArray(short value) {
        return new byte[] {
                (byte) (value), (byte) (value >> 8) };
    }
}
