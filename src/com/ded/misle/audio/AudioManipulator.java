package com.ded.misle.audio;

import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class AudioManipulator {
    Clip[] clips;
    boolean isMute;

    public AudioManipulator(Clip clip) {
        this.clips = new Clip[] { clip };
    }

    public AudioManipulator(Clip[] clips) {
        this.clips = clips;
    }

    public AudioManipulator(AudioFile file) {
        this.clips = file.getAllClips();
    }

    public AudioManipulator(AudioType type) {
        List<AudioFile> files = new ArrayList<>(type.getFiles());
        List<Clip> clips = new ArrayList<>(files.size());
        for (AudioFile file : files) {
            clips.addAll(Arrays.asList(file.getAllClips()));
        }
        this.clips = clips.toArray(new Clip[0]);
    }

    public AudioManipulator setGain(float gain) {
        iterateAllClips(clip -> {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float range = gainControl.getMaximum() - gainControl.getMinimum();
            float value = (gain * range) + gainControl.getMinimum();
            gainControl.setValue(value);
        });
        return this;
    }

    public AudioManipulator setMute(boolean mute) {
        iterateAllClips(clip -> {
            BooleanControl booleanControl = (BooleanControl) clip.getControl(BooleanControl.Type.MUTE);
            booleanControl.setValue(mute);
        });
        isMute = mute;
        return this;
    }

    public AudioManipulator toggleMute() {
        setMute(!isMute);
        return this;
    }

    public AudioManipulator setSampleRate(float modifier) {
        iterateAllClips(clip -> {
            FloatControl sampleRate = (FloatControl) clip.getControl(FloatControl.Type.SAMPLE_RATE);
            float range = sampleRate.getMaximum() - sampleRate.getMinimum();
            float value = (modifier * range) + sampleRate.getMinimum();
            sampleRate.setValue(value);
        });
        return this;
    }

    private void iterateAllClips(Consumer<Clip> action) {
        Arrays.stream(clips).iterator().forEachRemaining(action);
    }
private void iterateAllFiles(Consumer<AudioFile> action) {
        Arrays.stream(files).iterator().forEachRemaining(action);
    }
}
