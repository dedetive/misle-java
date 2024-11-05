package com.ded.misle;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import static com.ded.misle.GamePanel.*;
import static com.ded.misle.Launcher.scale;

public class MouseHandler implements MouseListener, MouseMotionListener {
	private int mouseX, mouseY;
	private boolean isPressed;

	public MouseHandler() {
		isPressed = false;
		// For initializing variables
	}

	@Override
	public void mousePressed(MouseEvent e) {
		isPressed = true;
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		isPressed = false;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	public int getMouseX() {
		return mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}

	public boolean isMousePressed() {
		return isPressed;
	}

	public void updateMouse() {
		switch (gameState) {
			case GamePanel.GameState.PLAYING:
				if (isMousePressed()) {

					boolean foundWhereClicked = false;

					// Checking if where clicked is inside one of the inventory slots

					int inventoryBarWidth = (int) (120 * scale);
					int inventoryBarHeight = (int) (20 * scale);
					int inventoryBarX = (int) (screenWidth - inventoryBarWidth) / 2;
					int inventoryBarY = (int) (screenHeight - inventoryBarHeight - 60);

					int slotWidth = (int) (30 * scale);
					int slotHeight = (int) (30 * scale);
					int slotSpacing = (int) (3 * scale);

					int totalSlotsWidth = 7 * slotWidth + (6 * slotSpacing);
					int slotStartX = inventoryBarX + (inventoryBarWidth - totalSlotsWidth) / 2;

					for (int i = 0; i < 7; i++) {
						int slotX = slotStartX + i * (slotWidth + slotSpacing);
						int slotY = inventoryBarY + (inventoryBarHeight - slotHeight) / 2;

						// Check if the mouse position is within this slot's boundaries
						if (mouseX >= slotX && mouseX <= slotX + slotWidth &&
								mouseY >= slotY && mouseY <= slotY + slotHeight) {
							player.inv.setSelectedSlot(i); // Select the slot
							foundWhereClicked = true;
							break; // Exit the loop once the correct slot is found
						}
					} if (!foundWhereClicked) {
						player.inv.useItem();
					}
				}
				break;
			case GamePanel.GameState.INVENTORY:
				if (isMousePressed()) {

				}
				break;
		}
	}

	@Override public void mouseClicked(MouseEvent e) {}
	@Override public void mouseEntered(MouseEvent e) {}
	@Override public void mouseExited(MouseEvent e) {}

}
