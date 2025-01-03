package com.ded.misle.input_handler;

import com.ded.misle.boxes.Box;
import com.ded.misle.boxes.BoxHandling;
import com.ded.misle.items.Item;
import com.ded.misle.player.PlayerStats;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Arrays;
import java.util.Objects;

import static com.ded.misle.GamePanel.*;
import static com.ded.misle.Launcher.scale;
import static com.ded.misle.boxes.Box.clearSelectedBoxes;
import static com.ded.misle.input_handler.KeyHandler.pressUseButton;
import static com.ded.misle.npcs.NPCDialog.getCurrentTalkingTo;
import static com.ded.misle.player.PlayerStats.Direction.LEFT;
import static com.ded.misle.player.PlayerStats.Direction.RIGHT;
import static com.ded.misle.renderer.InventoryRenderer.*;
import static com.ded.misle.renderer.PlayingRenderer.inventoryBarY;
import static com.ded.misle.renderer.PlayingRenderer.slotStartX;

public class MouseHandler implements MouseListener, MouseMotionListener {

	private static final int[] barSlotX = new int[7];
	private static final int[] slotX = new int[7];
	private static final int[] slotY = new int[4];
    private int hoveredBarSlot = -1;
	private int[] hoveredSlot = new int[]{-1, -1};
	private static final int[] extraSlotX = new int[2];
	private static final int[] extraSlotY = new int[2];
	private int[] extraHoveredSlot = new int[]{-1, -1};

	private static double relativeMouseRotation;

	public double getRelativeMouseRotation() {
		return relativeMouseRotation;
	}

	public double getMouseHorizontalRotation() {
		double rotation = 0;

		if (relativeMouseRotation >= 0 && relativeMouseRotation <= 90) {
			rotation = relativeMouseRotation;
		} else if (relativeMouseRotation > 90 && relativeMouseRotation <= 270) {
			rotation = relativeMouseRotation - 180;
			rotation = Math.min(rotation, 90 - 35);
		} else if (relativeMouseRotation > 270 && relativeMouseRotation <= 360) {
			rotation = relativeMouseRotation;
			rotation = Math.max(rotation, 270 + 35);
		}

		System.out.println(rotation);
		return rotation;
	}

	public PlayerStats.Direction getMouseHorizontalDirection() {
		PlayerStats.Direction direction;

		if ((relativeMouseRotation >= 0 && relativeMouseRotation <= 90) ||
			(relativeMouseRotation >= 270 && relativeMouseRotation <= 360)) {
			direction = RIGHT;
		} else {
			direction = LEFT;
		}

		return direction;
	}

	public static void updateMouseVariableScales() {
		// PLAYING

		for (int i = 0; i < 7; i++) {
			barSlotX[i] = slotStartX + i * (slotSize[0] + slotSpacing[0]);
		}

		// INVENTORY

		int[] rowOrder = {1, 2, 3, 0};

		for (int j = 0; j < 4; j++) {
			for (int i = 0; i < 7; i++) {
				slotX[i] = gridOffset[0][0] + i * (slotSize[0] + slotSpacing[0]);
				slotY[rowOrder[j]] = gridOffset[0][1] + j * (slotSize[0] + slotSpacing[0]);
			}
		}

		// INVENTORY EXTRA SLOTS

		for (int j = 0; j < 2; j++) {
			for (int i = 0; i < 2; i++) {
				extraSlotX[i] = gridOffset[1][0] + i * (slotSize[1] + slotSpacing[1]);
				extraSlotY[j] = gridOffset[1][1] + j * (slotSize[1] + slotSpacing[1]);
			}
		}
	}

	private int mouseX, mouseY;
	private boolean isLeftPressed;
	private boolean isRightPressed;
	private boolean isReleased;

	public MouseHandler() {
		isLeftPressed = false;
		isRightPressed = false;
		// For initializing variables
	}

	@Override
	public void mousePressed(MouseEvent e) {
		switch (e.getButton()) {
			case MouseEvent.BUTTON1 -> isLeftPressed = true;
			case MouseEvent.BUTTON3 -> isRightPressed = true;
		}

		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		isLeftPressed = false;
		isRightPressed = false;
		isReleased = true;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		double worldMouseX = mouseX + player.pos.getCameraOffsetX();
		double worldMouseY = mouseY + player.pos.getCameraOffsetY();
		double deltaX = worldMouseX - player.getX();
		double deltaY = worldMouseY - player.getY();

		relativeMouseRotation = Math.toDegrees(Math.atan2(deltaY, deltaX));
		if (relativeMouseRotation < 0) {
			relativeMouseRotation += 360;
		}

		switch (gameState) {
			case PLAYING -> updateHoveredBarSlot();
			case INVENTORY -> updateHoveredSlot();
		}
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

	public boolean isAnyPressed() {
		return isLeftPressed || isRightPressed;
	}

	public boolean isLeftPressed() {
		return isLeftPressed;
	}

	public boolean isRightPressed() {
		return isRightPressed;
	}

	public void updateMouse() {
        switch (gameState) {
            case GameState.PLAYING -> {
                if (isAnyPressed()) {
                    boolean foundWhereClicked = false;

                    if (getHoveredBarSlot() >= 0) {
                        player.inv.setSelectedSlot(getHoveredBarSlot()); // Select the slot
                        foundWhereClicked = true;
                    }
                    if (!foundWhereClicked) {
                        pressUseButton();
                    }
                }
            }
            case GameState.INVENTORY -> {
                Item draggedItem = player.inv.getDraggedItem();
                if (isLeftPressed() || (isRightPressed() && !hasDraggedItem())) {
					switch (getSlotType()) {
                        // If not pressed a slot, drop item
						case EMPTY -> { if (hasDraggedItem()) player.inv.dropDraggedItem(draggedItem.getCount()); }
						case INVENTORY -> {
							if (isSlotOccupied()) {
								if (hasDraggedItem())
									// Swap
									player.inv.initDraggingItem(getHoveredSlot()[0], getHoveredSlot()[1], draggedItem.getCount(), false);
									// Quick equip item
								else if ((player.keys.keyPressed.get(KeyHandler.Key.SHIFT)) && Objects.equals(player.inv.getItem(getHoveredSlot()[0], getHoveredSlot()[1]).getSubtype(), "ring")) {
										boolean[] isSpaceOccupied = new boolean[3];
										int firstValidPosition = -1;
										boolean isAnyEmpty = false;
										for (int i = 0; i < 3; i++) {
											isSpaceOccupied[i] = player.inv.getItem(i) != null;
											if (!isSpaceOccupied[i]) firstValidPosition = i;
											if (!isSpaceOccupied[i]) {
												isAnyEmpty = true;
												break;}
										}
										if (isAnyEmpty) {
											player.inv.bruteSetItem(player.inv.getItem(getHoveredSlot()[0], getHoveredSlot()[1]), firstValidPosition);
											player.inv.removeItem(getHoveredSlot()[0], getHoveredSlot()[1]);
										}
								}
								// Grab item
								else player.inv.initDraggingItem(getHoveredSlot()[0], getHoveredSlot()[1], -1, false);
							} else if (hasDraggedItem()) {
								// Place item into slot if it's empty and has dragged item
								player.inv.putDraggedItem(getHoveredSlot()[0], getHoveredSlot()[1], draggedItem.getCount(), false);
							}
						}
						case RING -> {
							boolean isRing = hasDraggedItem() && Objects.equals(player.inv.getDraggedItem().getSubtype(), "ring");
                            if (isSlotOccupied()) {
								if (isRing)
									// Swap only if it is a ring
									player.inv.initDraggingItem(getExtraHoveredSlot()[0], getExtraHoveredSlot()[1], draggedItem.getCount(), true);
									// Quick unequip ring
								else if (player.keys.keyPressed.get(KeyHandler.Key.SHIFT)) {
									boolean[] isSpaceOccupied = new boolean[4];
									int[] firstValidPosition = new int[]{-1, -1};
									boolean isAnyEmpty = false;
									for (int i = 0; i < 4; i++) {
										for (int j = 0; j < 7; j++) {
											isSpaceOccupied[i] = player.inv.getItem(i, j) != null;
											if (!isSpaceOccupied[i] && Arrays.equals(firstValidPosition, new int[]{-1, -1})) firstValidPosition = new int[]{i, j};
											if (!isSpaceOccupied[i]) {
												isAnyEmpty = true;
												break; }
										}
										if (isAnyEmpty) break;
									}
									if (isAnyEmpty) {
										player.inv.bruteSetItem(player.inv.getItem(getExtraHoveredSlot()[1] * 2 + getExtraHoveredSlot()[0]), firstValidPosition[0], firstValidPosition[1]);
										player.inv.removeItem(getExtraHoveredSlot()[1] * 2 + getExtraHoveredSlot()[0]);
									}
								}
								// Grab item
								else if (!hasDraggedItem()) player.inv.initDraggingItem(getExtraHoveredSlot()[0], getExtraHoveredSlot()[1], -1, true);
							} else if (isRing) {
								// Place item into slot if it's empty and has dragged item
								player.inv.putDraggedItem(getExtraHoveredSlot()[0], getExtraHoveredSlot()[1], draggedItem.getCount(), true);
							}
						}
                    }
                } else if (isRightPressed() && hasDraggedItem()) {
					switch (getSlotType()) {
                    	// Drop outside
						case EMPTY -> player.inv.dropDraggedItem(1);
						case INVENTORY -> {
							if (isSlotOccupied()) {
								// Swap
								player.inv.initDraggingItem(getHoveredSlot()[0], getHoveredSlot()[1], draggedItem.getCount(), false);
							} else {
								// Put one into slot
								player.inv.putDraggedItem(getHoveredSlot()[0], getHoveredSlot()[1], 1, false);
							}
						}
						case RING -> {
							if (player.inv.getDraggedItem().getSubtype().equals("ring")) {
								if (isSlotOccupied()) {
									// Swap
									player.inv.initDraggingItem(getExtraHoveredSlot()[0], getExtraHoveredSlot()[1], -1, true);
								} else {
									// Put into slot
									player.inv.putDraggedItem(getExtraHoveredSlot()[0], getExtraHoveredSlot()[1], 1, true);
								}
							}
						}
					}
                }
            }
            case GameState.LEVEL_DESIGNER -> {
                if (isLeftPressed()) {
                    // detect box in click position
                } else if (isReleased) {
                    // Get x, y game world coordinates of mouse click and select nearby boxes of that position
                    int mouseGameX = (int) ((player.pos.getCameraOffsetX() + mouseX) / scale);
                    int mouseGameY = (int) ((player.pos.getCameraOffsetY() + mouseY) / scale);
                    clearSelectedBoxes();
                    System.out.println();
                    System.out.println("mouse: " + mouseGameX + ", " + mouseGameY);
                    for (Box box : BoxHandling.getCachedBoxesInRange(mouseGameX, mouseGameY, 6)) {
                        box.addSelectedBox();
                        System.out.println("box: " + box.getX() * scale + ", " + box.getY() * scale);
                    }
                }
            }
			case GameState.DIALOG -> {
				if (isLeftPressed()) {
					getCurrentTalkingTo().incrementDialogIndex();
				}
			}
        }
		isLeftPressed = false;
		isRightPressed = false;
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
			if (mouseX >= barSlotX[i] && mouseX <= barSlotX[i] + slotSize[0] &&
				mouseY >= inventoryBarY && mouseY <= inventoryBarY + slotSize[0]) {
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
		extraHoveredSlot = new int[]{-1, -1};

		// Regular inventory
		for (int j = 0; j < 4; j++) {
			for (int i = 0; i < 7; i++) {
				if (mouseX >= slotX[i] && mouseX <= slotX[i] + slotSize[0] &&
					mouseY >= slotY[j] && mouseY <= slotY[j] + slotSize[0]) {
					hoveredSlot = new int[]{j, i}; // Set the hovered slot index
					return;
				}
			}
		}

		// Extra slots (rings and trophy)
		for (int j = 0; j < 2; j++) {
			for (int i = 0; i < 2; i++) {
				if (mouseX >= extraSlotX[i] && mouseX <= extraSlotX[i] + slotSize[1] &&
					mouseY >= extraSlotY[j] && mouseY <= extraSlotY[j] + slotSize[1]) {
					extraHoveredSlot = new int[]{j, i};
					return;
				}
			}
		}
	}

	public int[] getHoveredSlot() {
		return hoveredSlot;
	}

	public int[] getExtraHoveredSlot() {
		return extraHoveredSlot;
	}
	
	private boolean isSlotOccupied() {
		return (player.inv.getItem(hoveredSlot[0], hoveredSlot[1]) != null) || (player.inv.getItem(extraHoveredSlot[0], extraHoveredSlot[1]) != null);
	}

	private enum SlotType {
		EMPTY,
		INVENTORY,
		RING,
		TROPHY
	}

	private SlotType getSlotType() {
		if (getHoveredSlot()[0] >= 0 && getHoveredSlot()[1] >= 0) return SlotType.INVENTORY;
		if ((getExtraHoveredSlot()[0] >= 0 && getExtraHoveredSlot()[1] >= 0) && !(getExtraHoveredSlot()[0] == 1 && getExtraHoveredSlot()[1] == 1)) return SlotType.RING;
		if (getExtraHoveredSlot()[0] == 1 && getExtraHoveredSlot()[1] == 1) return SlotType.TROPHY;
		else return SlotType.EMPTY;
	}

	private boolean hasDraggedItem() {
		return player.inv.getDraggedItem() != null;
	}
}
