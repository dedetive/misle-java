package com.ded.misle;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

	public boolean upPressed, downPressed, leftPressed, rightPressed;


	@Override
	public void keyTyped(KeyEvent e) {
		// THIS BAD
	}

	int KeyUp = KeyEvent.VK_UP;
	int KeyDown = KeyEvent.VK_DOWN;
	int KeyLeft = KeyEvent.VK_LEFT;
	int KeyRight = KeyEvent.VK_RIGHT;


	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if (code == KeyUp) {
			upPressed = true;
		}
		if (code == KeyDown) {
			downPressed = true;
		}
		if (code == KeyLeft) {
			leftPressed = true;
		}
		if (code == KeyRight) {
			rightPressed = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int code = e.getKeyCode();;
		if (code == KeyUp) {
			upPressed = false;
		}
		if (code == KeyDown) {
			downPressed = false;
		}
		if (code == KeyLeft) {
			leftPressed = false;
		}
		if (code == KeyRight) {
			rightPressed = false;
		}
	}
}
