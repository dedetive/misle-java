package com.ded.misle.audio;

import com.ded.misle.core.SettingsManager;

import javax.sound.sampled.Clip;
import java.util.ArrayList;

public enum AudioFile {
    consume_small_pot(AudioType.SFX, 1),
    consume_medium_pot(AudioType.SFX, 1),
    drop_item(AudioType.SFX),
    collect_item(AudioType.SFX);

    private static final int DEFAULT_AUDIO_PLAYER_COUNT = 3;
    private final String path;
    private final ArrayList<AudioPlayer> audioPlayerList = new ArrayList<>();
    private final AudioType type;

    AudioFile(AudioType type) {
        this(type, DEFAULT_AUDIO_PLAYER_COUNT);
    }

    AudioFile(AudioType type, final int AUDIO_PLAYER_COUNT) {
        this.path = SettingsManager.getPath(SettingsManager.GetPathTag.RESOURCES).resolve("audio/" + this + ".wav").toString();

        this.type = type;
        type.setType(this);

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

    public AudioType getType() {
        return type;
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

    public Clip[] getAllClips() {
        Clip[] clips = new Clip[audioPlayerList.size()];
        for (int i = 0 ; i < audioPlayerList.size() ; i++) {
            clips[i] = audioPlayerList.get(i).getClip();
        }
        return clips;
    }
}
