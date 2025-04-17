package com.ded.misle.audio;

import javax.sound.sampled.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.ded.misle.core.SettingsManager.getPath;

public class AudioPlayer {
	private Clip clip;

	public AudioPlayer(String filePath) {
		try {
			File audioFile = new File(filePath);
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
			clip = AudioSystem.getClip();
			clip.open(audioStream);
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	public enum AudioFile {
		consume_small_pot,
		consume_medium_pot,
		drop_item,
		collect_item

		;

		public final String path;
		public final ArrayList<AudioPlayer> audioPlayerList = new ArrayList<>();

        AudioFile () {
            int AUDIO_PLAYER_COUNT = 8;
			this.path = getPath().resolve("resources/audio/" + this + ".wav").toString();
            for (int i = 0; i < AUDIO_PLAYER_COUNT; i++) {
				this.audioPlayerList.add(new AudioPlayer(this.path));
			}
		}
	}

	/**
	 * Plays an audio with the given name.
	 *
	 * @param audio
	 */
	public static void playThis(AudioFile audio) {
		for (AudioPlayer audioPlayer : audio.audioPlayerList) {
			boolean isFree = !audioPlayer.clip.isRunning();
			if (isFree) {
				audioPlayer.play();

				break;
			}
		}
	}

	public static void stopThis(AudioFile audio) {
		new AudioPlayer(audio.path).stop();
	}

	private void play() {
		if (clip != null) {
			clip.setFramePosition(0);
			clip.start();
		}
	}

	private void stop() {
		if (clip != null && clip.isRunning()) {
			clip.stop();
		}
	}

	// AUDIO MANIPULATION
		// Here should be pitch and speed alteration, reverb, echo, and more effects
}
