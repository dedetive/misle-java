package com.ded.misle;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import static com.ded.misle.GamePanel.*;
import static com.ded.misle.Launcher.scale;

public class MouseHandler implements MouseListener, MouseMotionListener {

	private static int slotWidth;
	private static int slotHeight;
	private static final int[] barSlotX = new int[7];
	private static final int[] barSlotY = new int[7];
	private static final int[] slotX = new int[7];
	private static final int[] slotY = new int[7];
	private static int inventoryBarWidth;
	private static int inventoryBarHeight;
	private static int inventoryBarX;
	private static int inventoryBarY;
	private static int slotSpacing;
	private static int totalSlotsWidth;
	private static int slotStartX;
	private static int slotSize;
	private static int gridWidth;
	private static int gridHeight;
	private static int gridX;
	private static int gridY;
	private int hoveredBarSlot = -1;
	private int[] hoveredSlot = new int[]{-1, -1};

	static {

		// PLAYING

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
			barSlotX[i] = slotStartX + i * (slotWidth + slotSpacing);
			barSlotY[i] = inventoryBarY + (inventoryBarHeight - slotHeight) / 2;
		}

		// INVENTORY

		slotSize = (int) (30 * scale);
		slotSpacing = (int) (3 * scale);

		gridWidth = 7 * slotSize + 6 * slotSpacing;
		gridHeight = 4 * slotSize + 3 * slotSpacing;

		gridX = (int) ((screenWidth - gridWidth) / 2);
		gridY = (int) ((screenHeight - gridHeight) / 2);

		for (int j = 0; j < 4; j++) {
			for (int i = 0; i < 7; i++) {
				slotX[i] = gridX + i * (slotSize + slotSpacing);
				slotY[j] = gridY + j * (slotSize + slotSpacing);
			}
		}
	}

	public static void updateVariableScales() {

		// PLAYING

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
			barSlotX[i] = slotStartX + i * (slotWidth + slotSpacing);
			barSlotY[i] = inventoryBarY + (inventoryBarHeight - slotHeight) / 2;
		}

		// INVENTORY

		slotSize = (int) (30 * scale);
		slotSpacing = (int) (3 * scale);

		gridWidth = 7 * slotSize + 6 * slotSpacing;
		gridHeight = 4 * slotSize + 3 * slotSpacing;

		gridX = (int) ((screenWidth - gridWidth) / 2);
		gridY = (int) ((screenHeight - gridHeight) / 2);

		int[] rowOrder = {1, 2, 3, 0};

		for (int j = 0; j < 4; j++) {
			for (int i = 0; i < 7; i++) {
				slotX[i] = gridX + i * (slotSize + slotSpacing);
				slotY[rowOrder[j]] = gridY + j * (slotSize + slotSpacing);
			}
		}
	}

	private int mouseX, mouseY;
	private boolean isPressed;
	private boolean isReleased;

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
		isReleased = true;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		updateHoveredBarSlot();
		updateHoveredSlot();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		updateHoveredBarSlot();
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
			case GameState.PLAYING:
				if (isMousePressed()) {
					boolean foundWhereClicked = false;

					if (getHoveredBarSlot() >= 0) {
						player.inv.setSelectedSlot(getHoveredBarSlot()); // Select the slot
						foundWhereClicked = true;
					}
					if (!foundWhereClicked) {
						player.inv.useItem();
					}
				}
				break;
			case GameState.INVENTORY:
				if (isMousePressed()) {
					if (getHoveredSlot()[0] >= 0 && getHoveredSlot()[1] >= 0) {
						if (player.inv.getItem(getHoveredSlot()[0], getHoveredSlot()[1]) != null) {
							player.inv.initDraggingItem(getHoveredSlot()[0], getHoveredSlot()[1]);
						} else if (player.inv.getDraggedItem() != null && player.inv.getItem(getHoveredSlot()[0], getHoveredSlot()[1]) == null) {
							player.inv.putDraggedItem(getHoveredSlot()[0], getHoveredSlot()[1]);
						}
					} else {
						if (player.inv.getDraggedItem() != null) {
							player.inv.dropDraggedItem();
						}
					}
				}
				break;
			case GameState.LEVEL_DESIGNER:
				if (isMousePressed()) {
					// detect box in click position
				} else if (isReleased) {
					// Get x, y game world coordinates of mouse click and select nearby boxes of that position
				}
				break;
		}
		isPressed = false;
		isReleased = false;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	private void updateHoveredBarSlot() {
		hoveredBarSlot = -1; // Reset hovered slot
		for (int i = 0; i < 7; i++) {
			if (mouseX >= barSlotX[i] && mouseX <= barSlotX[i] + slotWidth &&
					mouseY >= barSlotY[i] && mouseY <= barSlotY[i] + slotHeight) {
				hoveredBarSlot = i; // Set the hovered slot index
				break;
			}
		}
	}

	public int getHoveredBarSlot() {
		return hoveredBarSlot;
	}

	public void updateHoveredSlot() {
		hoveredSlot = new int[]{-1, -1};
		for (int j = 0; j < 4; j++) {
			for (int i = 0; i < 7; i++) {
				if (mouseX >= slotX[i] && mouseX <= slotX[i] + slotWidth &&
						mouseY >= slotY[j] && mouseY <= slotY[j] + slotHeight) {
					hoveredSlot = new int[]{j, i}; // Set the hovered slot index
					break;
				}
			}
		}
	}

	public int[] getHoveredSlot() {
		return hoveredSlot;
	}
}
