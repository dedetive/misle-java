package com.ded.misle.audio;

import com.ded.misle.core.SettingsManager;

import java.util.ArrayList;

import static com.ded.misle.core.SettingsManager.getPath;

public enum AudioFile {
    consume_small_pot(1),
    consume_medium_pot(1),
    drop_item,
    collect_item;

    private static final int DEFAULT_AUDIO_PLAYER_COUNT = 4;
    private final String path;
    private final ArrayList<AudioPlayer> audioPlayerList = new ArrayList<>();

    AudioFile() {
        this(DEFAULT_AUDIO_PLAYER_COUNT);
    }

    AudioFile(final int AUDIO_PLAYER_COUNT) {
        this.path = SettingsManager.getPath(SettingsManager.GetPathTag.RESOURCES).resolve("audio/" + this + ".wav").toString();
        for (int i = 0; i < AUDIO_PLAYER_COUNT; i++) {
            this.audioPlayerList.add(new AudioPlayer(this.path));
        }
    }

    public void stop() {
        for (AudioPlayer audioPlayer : audioPlayerList) {
            audioPlayer.getClip().stop();
        }
    }

    public String getPath() {
        return path;
    }

    public AudioPlayer getFreeAudioPlayer() {
        for (AudioPlayer audioPlayer : this.audioPlayerList) {
            boolean isFree = !audioPlayer.getClip().isRunning();
            if (isFree) {
                return audioPlayer;
            }
        }

        return audioPlayerList.getFirst();
    }
}
