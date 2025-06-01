package com.ded.misle.audio;

import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.util.Arrays;

public class AudioManipulator {
    Clip[] clips;

    public AudioManipulator(Clip clip) {
        this.clips = new Clip[] { clip };
    }

    public AudioManipulator(AudioFile file) {
        this.clips = file.getAllClips();
    }

    public AudioManipulator setGain(float gain) {
        Arrays.stream(clips).iterator().forEachRemaining(clip -> {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float range = gainControl.getMaximum() - gainControl.getMinimum();
            float value = (gain * range) + gainControl.getMinimum();
            gainControl.setValue(value);
        });
        return this;
    }

    public AudioManipulator setMute(boolean mute) {
        Arrays.stream(clips).iterator().forEachRemaining(clip -> {
            BooleanControl booleanControl = (BooleanControl) clip.getControl(BooleanControl.Type.MUTE);
            booleanControl.setValue(mute);
        });
        return this;
    }
}
