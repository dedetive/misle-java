package com.ded.misle.audio;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioPlayer {
	private Clip clip;
	private AudioManipulator manipulator;

	public AudioPlayer(String filePath) {
		try {
			File audioFile = new File(filePath);
			AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
			clip = AudioSystem.getClip();
			clip.open(audioStream);
			manipulator = new AudioManipulator(clip);
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Plays an audio with the given name.
	 */
	public static void playThis(AudioFile audio) {
		audio.getFreeAudioPlayer().play();
	}

	public static void stopThis(AudioFile audio) {
		audio.stop();
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

	public Clip getClip() {
		return clip;
	}
}
