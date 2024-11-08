package com.ded.misle;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import static com.ded.misle.GamePanel.*;
import static com.ded.misle.Launcher.scale;

public class MouseHandler implements MouseListener, MouseMotionListener {

	private static int slotWidth;
	private static int slotHeight;
	private static final int[] slotX = new int[7];
	private static final int[] slotY = new int[7];
	private static int inventoryBarWidth;
	private static int inventoryBarHeight;
	private static int inventoryBarX;
	private static int inventoryBarY;
	private static int slotSpacing;
	private static int totalSlotsWidth;
	private static int slotStartX;
	private int hoveredSlot = -1;

	static {
		int inventoryBarWidth = (int) (120 * scale);
		int inventoryBarHeight = (int) (20 * scale);
		int inventoryBarX = (int) (screenWidth - inventoryBarWidth) / 2;
		int inventoryBarY = (int) (screenHeight - inventoryBarHeight - 60);

		slotWidth = (int) (30 * scale);
		slotHeight = (int) (30 * scale);
		int slotSpacing = (int) (3 * scale);

		int totalSlotsWidth = 7 * slotWidth + (6 * slotSpacing);
		int slotStartX = inventoryBarX + (inventoryBarWidth - totalSlotsWidth) / 2;

		for (int i = 0; i < 7; i++) {
			slotX[i] = slotStartX + i * (slotWidth + slotSpacing);
			slotY[i] = inventoryBarY + (inventoryBarHeight - slotHeight) / 2;
		}
	}

	public static void updateVariableScales() {
		inventoryBarWidth = (int) (120 * scale);
		inventoryBarHeight = (int) (20 * scale);
		inventoryBarX = (int) (screenWidth - inventoryBarWidth) / 2;
		inventoryBarY = (int) (screenHeight - inventoryBarHeight - 60);

		slotWidth = (int) (30 * scale);
		slotHeight = (int) (30 * scale);
		slotSpacing = (int) (3 * scale);

		totalSlotsWidth = 7 * slotWidth + (6 * slotSpacing);
		slotStartX = inventoryBarX + (inventoryBarWidth - totalSlotsWidth) / 2;

		for (int i = 0; i < 7; i++) {
			slotX[i] = slotStartX + i * (slotWidth + slotSpacing);
			slotY[i] = inventoryBarY + (inventoryBarHeight - slotHeight) / 2;
		}
	}

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
		updateHoveredSlot();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		updateHoveredSlot();
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
					for (int i = 0; i < 7; i++) {
						// Check if the mouse position is within this slot's boundaries
						if (mouseX >= slotX[i] && mouseX <= slotX[i] + slotWidth &&
								mouseY >= slotY[i] && mouseY <= slotY[i] + slotHeight) {
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

	private void updateHoveredSlot() {
		hoveredSlot = -1; // Reset hovered slot
		for (int i = 0; i < 7; i++) {
			if (mouseX >= slotX[i] && mouseX <= slotX[i] + slotWidth &&
					mouseY >= slotY[i] && mouseY <= slotY[i] + slotHeight) {
				hoveredSlot = i; // Set the hovered slot index
				break;
			}
		}
	}

	public int getHoveredSlot() {
		return hoveredSlot;
	}
}
