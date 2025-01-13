package com.ded.misle.audio;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

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

		AudioFile () {
			this.path = getPath().resolve("resources/audio/" + this + ".wav").toString();
		}
	}

	/**
	 * Plays an audio with the given name.
	 *
	 * @param audio
	 */
	public static void playThis(AudioFile audio) {
		new AudioPlayer(audio.path).play();
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
