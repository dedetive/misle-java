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
	}

	/**
	 * Plays an audio with the given name.
	 *
	 * @param audioName
	 */
	public static void playThis(AudioFile audioName) {
		new AudioPlayer(String.valueOf(getPath().resolve("resources/audio/" + audioName + ".wav"))).play();
	}

	public static void stopThis(AudioFile audioName) {
		new AudioPlayer(String.valueOf(getPath().resolve("resources/audio/" + audioName + ".wav"))).stop();
	}

	public static void playWithSpeed(String filePath, float speedFactor) {
		try {
			File audioFile = new File(filePath);
			AudioInputStream originalStream = AudioSystem.getAudioInputStream(audioFile);

			AudioFormat originalFormat = originalStream.getFormat();
			AudioFormat newFormat = new AudioFormat(
				originalFormat.getEncoding(),
				originalFormat.getSampleRate() * speedFactor, // Adjust sample rate
				originalFormat.getSampleSizeInBits(),
				originalFormat.getChannels(),
				originalFormat.getFrameSize(),
				originalFormat.getFrameRate() * speedFactor,
				originalFormat.isBigEndian()
			);

			AudioInputStream adjustedStream = AudioSystem.getAudioInputStream(newFormat, originalStream);

			Clip clip = AudioSystem.getClip();
			clip.open(adjustedStream);
			clip.start();

			// Wait for the clip to finish playing
			while (clip.isRunning()) {
				Thread.sleep(20);
			}

			clip.close();
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException | InterruptedException e) {
			e.printStackTrace();
		}
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
