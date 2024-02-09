package org.brooklynspeech.pipeline.tts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

import javax.sound.sampled.AudioSystem;

import org.brooklynspeech.pipeline.data.BSLTurnConversation;
import org.brooklynspeech.pipeline.data.BSLTurnFeatures;
import org.common.core.PassthroughStreamProcessor;
import org.brooklynspeech.pipeline.data.BSLTurn;
import org.brooklynspeech.pipeline.data.BSLFeatureConversation;
import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;

public class ControllableTacotronTTSProcessor<TurnType extends BSLTurnFeatures, ConversationType extends BSLFeatureConversation<TurnType>>
        extends PassthroughStreamProcessor<BSLTurnConversation<TurnType, ConversationType>> {

    private final Module model;
    private final HashMap<Character, Long> charMap;

    private final int mels;
    private final int maxMelLen;

    private boolean hasEndToken = false;
    private char endToken;

    public ControllableTacotronTTSProcessor(String modelPath, String allowedChars, int mels, int maxMelLen) {
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

    public ControllableTacotronTTSProcessor(String modelPath, String allowedChars, char endToken, int mels,
            int maxMelLen) {
        this(modelPath, allowedChars + endToken, mels, maxMelLen);

        this.hasEndToken = true;
        this.endToken = endToken;
    }

    public ControllableTacotronTTSProcessor(String modelPath) {
        // Default allowed characters borrowed from the Tacotron 2 source.
        this(modelPath, "!'(),.:;? \\-ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz", '^', 80, 500);
    }

    public BSLTurnConversation<TurnType, ConversationType> doProcess(
            BSLTurnConversation<TurnType, ConversationType> message) {
        TurnType chunk = message.chunk;
        ConversationType conversation = message.conversation;

        String transcript = chunk.getTranscript();
        System.out.println("ControllableTacotronTTSProcessor: " + transcript);

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
        IValue sigmoidGates = IValue.from(true);
        String melPath;
        try {
            melPath = Files.createTempFile("dialogue_" + conversation.getConversationId() + "_", ".pt").toString();
        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.exit(1);
            return null;
        }

        IValue saveMel = IValue.from(melPath);
        IValue forceToGpu = IValue.from(true);
        IValue ttsInput = IValue.tupleFrom(ttsDataIValue, ttsMetadataIValue);

        // The code below is a gross hack because I don't feel like implementing
        // Griffin-Lim in Java, and because this needs to be changed to a neural vocoder
        // at some point. Here's what is going on:
        //
        // 1. The forward pass is called with sigmoidGates = true and saveMel = <a path
        // to a tmp file>. This causes the forward pass to apply the sigmoid function to
        // the gate output (which we need to determine the end of the spectrogram), and
        // saves the Mel spectrogram to the specified temporary file.
        //
        // 2. The gate index is determined from the output
        //
        // 3. An external Python script is called that loads the saved Mel spectrogram,
        // cuts off extra frames past the end of the gate, and uses Librosa's
        // spectrogram inversion function to produce waveform data. It saves the wav
        // data in another temporary file, which is loaded and saved in the chunk.
        //
        // The external Python script has been compiled into an executable with Nuitka.
        IValue result = this.model.forward(ttsInput, teacherForcing, sigmoidGates, saveMel, forceToGpu);

        // Result tuple: mels, mels_post, gates, alignments
        IValue resultsTuple[] = result.toTuple();

        // Find the gate index indicating the spectrogram ending
        float[] gates = resultsTuple[2].toTensor().getDataAsFloatArray();
        int melsLength;
        for (melsLength = 0; melsLength < gates.length; melsLength++) {
            if (gates[melsLength] < 0.5) {
                break;
            }
        }

        // Create a wav from spectrogram data
        String wavPath;
        try {
            wavPath = Files.createTempFile("dialogue_" + conversation.getConversationId() + "_", ".wav").toString();
        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.exit(1);
            return null;
        }

        ProcessBuilder processBuilder = new ProcessBuilder("./mel-to-audio", melPath,
                String.valueOf(melsLength),
                wavPath);
        final Process p;

        try {
            p = processBuilder.start();
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(1);
            return null;
        }

        // Load the wav data into the chunk
        try {
            chunk.setWavData(AudioSystem.getAudioInputStream(new File(wavPath)).readAllBytes());
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.exit(1);
            return null;
        }

        return message;
    }
}
