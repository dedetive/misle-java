package com.ded.misle.audio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public enum AudioType {
    SFX,
    SONG;

    private boolean isMute = false;
    private float gain = 1f;
    AudioManipulator manipulator;
    final List<AudioFile> files = new ArrayList<>();

    AudioType() {}

    private AudioType updateManipulator() {
        this.manipulator = new AudioManipulator(this);
        this.manipulator.setGain(gain);
        this.manipulator.setMute(isMute);
        return this;
    }

    public void setGain(float gain) {
        this.gain = gain;
        updateManipulator();
    }

    public float getGain() {
        return gain;
    }

    public boolean isMute() {
        return isMute;
    }

    public void setMute(boolean mute) {
        isMute = mute;
        updateManipulator();
    }

    public void toggleMute() {
        isMute = !isMute;
        updateManipulator();
    }

    public AudioType setType(AudioFile file) {
        files.add(file);
        updateManipulator();
        return this;
    }

    public Collection<? extends AudioFile> getFiles() {
        return files;
    }
}
