package com.ded.misle.audio;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class AudioManipulator {
    Clip clip;

    public AudioManipulator(Clip clip) {
        this.clip = clip;
    }

    public AudioManipulator setGain(float gain) {
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float range = gainControl.getMaximum() - gainControl.getMinimum();
        float value = (gain * range) + gainControl.getMinimum();
        gainControl.setValue(value);
        return this;
    }
}
