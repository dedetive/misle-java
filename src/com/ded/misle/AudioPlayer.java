package com.ded.misle;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

import static com.ded.misle.ChangeSettings.getPath;

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

	/**
	 * Plays an audio with the given name.
	 *
	 * @param audioName
	 */
	public static void playThis(String audioName) {
		new AudioPlayer(String.valueOf(getPath().resolve("resources/audio/" + audioName + ".wav"))).play();
	}

	public static void stopThis(String audioName) {
		new AudioPlayer(String.valueOf(getPath().resolve("resources/audio/" + audioName + ".wav"))).stop();
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
}
