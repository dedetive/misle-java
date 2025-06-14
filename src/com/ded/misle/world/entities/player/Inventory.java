package com.ded.misle.world.entities.player;

import com.ded.misle.renderer.FloatingText;
import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.boxes.BoxHandling;
import com.ded.misle.items.Item;
import com.ded.misle.renderer.PlayingRenderer;
import com.ded.misle.world.data.Direction;
import com.ded.misle.world.entities.Entity;
import com.ded.misle.world.logic.TurnTimer;

import javax.swing.*;
import java.text.DecimalFormat;

import static com.ded.misle.audio.AudioFile.*;
import static com.ded.misle.audio.AudioPlayer.playThis;
import static com.ded.misle.game.GamePanel.player;
import static com.ded.misle.world.boxes.BoxManipulation.*;
import static com.ded.misle.world.entities.Entity.HealFlag.NORMAL;
import static com.ded.misle.world.entities.player.PlayerAttributes.Stat.ALL;
import static com.ded.misle.renderer.ColorManager.entropyGainColor;
import static com.ded.misle.renderer.ColorManager.healColor;
import static java.lang.System.currentTimeMillis;

public class Inventory {
	private final Item[][] inventory;
	private final int rows = 4;
	private final int cols = 7;
	private int selectedSlot = 0;
	private Item draggedItem;
	private Item tempItem;
	private final Item[] extraSlots;

	public Inventory() {
		this.inventory = new Item[rows][cols];
		this.extraSlots = new Item[4];
	}

	public boolean addItem(Item item, int row, int col) {
		if (row < 0 || row >= rows || col < 0 || col >= cols) {
			return false; // Position out of bounds
		}

		int remainingCount = item.getCount();

		// Attempt to place items in the specified slot
		if (inventory[row][col] == null) {
			int toAdd = Math.min(remainingCount, item.getCountLimit());
			try {
				inventory[row][col] = new Item(item.getId(), toAdd);
			} catch (Exception e) {
				e.printStackTrace();
			}
			remainingCount -= toAdd;
		} else if (inventory[row][col].getId() == item.getId() && inventory[row][col].getCount() < inventory[row][col].getCountLimit()) {
			int availableSpace = inventory[row][col].getCountLimit() - inventory[row][col].getCount();
			int toAdd = Math.min(remainingCount, availableSpace);
			inventory[row][col].setCount(inventory[row][col].getCount() + toAdd);
			remainingCount -= toAdd;
		}

		// If there's an overflow, find the next available slot
		for (int i = 0; i < rows && remainingCount > 0; i++) {
			for (int j = 0; j < cols && remainingCount > 0; j++) {
				if (inventory[i][j] == null) {
					int toAdd = Math.min(remainingCount, item.getCountLimit());
					try {
						inventory[i][j] = new Item(item.getId(), toAdd);
					} catch (Exception e) {
						e.printStackTrace();
					}
					remainingCount -= toAdd;
				} else if (inventory[i][j].getId() == item.getId() && inventory[i][j].getCount() < inventory[i][j].getCountLimit()) {
					int availableSpace = inventory[i][j].getCountLimit() - inventory[i][j].getCount();
					int toAdd = Math.min(remainingCount, availableSpace);
					inventory[i][j].setCount(inventory[i][j].getCount() + toAdd);
					remainingCount -= toAdd;
				}
			}
		}

		return remainingCount == 0; // Return true if all items were added, false if some couldn't fit
	}

	public boolean addItem(Item item) {
		if (item == null) {
			return false;
		}
		Item newItem;
		try {
			newItem = new Item(item.getId(), item.getCount());
			player.turnRegenerationDoubledOn();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j ++) {
				if (inventory[i][j] != null && inventory[i][j].getId() == item.getId() && inventory[i][j].getCount() < inventory[i][j].getCountLimit()) {
					int itemCount = inventory[i][j].getCount();
					inventory[i][j].setCount(Math.min(inventory[i][j].getCount() + item.getCount(), item.getCountLimit()));
					item.setCount(item.getCount() - (item.getCountLimit() - itemCount));
					if (item.getCount() <= 0) {
						return true;
					}
				}
			}
		}

		for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
						if (inventory[i][j] == null && item.getCount() <= item.getCountLimit()) {
							// If slot is empty and there are less items than limit
							inventory[i][j] = item;
							return true;
						} else if (inventory[i][j] == null && item.getCount() > item.getCountLimit()) {
							// If slot is empty and there are more items than limit
							inventory[i][j] = newItem;
							inventory[i][j].setCount(item.getCountLimit());
							item.setCount(item.getCount() - item.getCountLimit());
						} else if (inventory[i][j].getId() == item.getId()) {
							if (item.getCount() + inventory[i][j].getCount() <= inventory[i][j].getCountLimit()) {
								// If ID is same and new item count plus old count amount to less or equal than the limit
								inventory[i][j].setCount(item.getCount() + inventory[i][j].getCount());
								return true;
							} else if (inventory[i][j].getCount() < inventory[i][j].getCountLimit()) {
								// If item count overflows
								item.setCount(item.getCount() - (inventory[i][j].getCountLimit() - inventory[i][j].getCount()));
								inventory[i][j].setCount(inventory[i][j].getCountLimit());
							}
						}
						if (item.getCount() <= 0) {
							return true;
						}
			}
		}
		return false;
	}

	public void bruteSetItem(Item item, int row, int col) {
		if (row >= 0 && row < rows && col >= 0 && col < cols) {
			inventory[row][col] = item;
		}
	}

	public void bruteSetItem(Item item, int position) {
		this.extraSlots[position] = item;
		player.attr.updateEquipmentStat(ALL);
	}

	public boolean removeItem(int row, int col) {
		if (row >= 0 && row < rows && col >= 0 && col < cols && inventory[row][col] != null) {
			inventory[row][col] = null;
			return true;
		}
		return false; // return false if position is out of bounds or slot is empty
	}

	public boolean removeItem(int row, int col, int quantity) {
		if (row >= 0 && row < rows && col >= 0 && col < cols && inventory[row][col] != null && inventory[row][col].getCount() > quantity) {
			inventory[row][col].setCount(inventory[row][col].getCount() - quantity);
			return true;
		} else if (row >= 0 && row < rows && col >= 0 && col < cols && inventory[row][col] != null && inventory[row][col].getCount() <= quantity) {
			inventory[row][col] = null;
			return true;
		} else {
			System.out.println("removing the item failed: \n" +
					"4 > row >= 0, row = " + row +
					",\n7 > col >= 0, col = " + col +
					",\ninventory[row][col] != null = " + inventory[row][col] +
					",\ninventory[row][col].getCount() >< quantity = " + inventory[row][col].getCount());
			return false; // return false if position is out of bounds or slot is empty
		}
	}

	public boolean removeItem(int position) {
		this.extraSlots[position] = null;
		player.attr.updateEquipmentStat(ALL);
		player.turnRegenerationDoubledOn();
		return true;
	}

	public Item getItem(int row, int col) {
		try {
			if (inventory[row][col].getId() == 0) {
				return null;
			}
		if (row < rows && col < cols) {
			return inventory[row][col];
		}
		} catch (NullPointerException | ArrayIndexOutOfBoundsException ignored) {
        }
		return null;
	}

	public Item getItem(int position) {
		if (position >= 0 && position < 4) {
			return extraSlots[position];
		}
		return null;
	}

	public int getSelectedSlot() {
		return selectedSlot;
	}

	public void setSelectedSlot(int selectedSlot) {
		this.selectedSlot = selectedSlot;
		PlayingRenderer.updateSelectedItemNamePosition();
		if (hasHeldItem()) {
			getSelectedItem().resetAnimation();
		}
	}

	public Item getSelectedItem() {
		if (getItem(0, selectedSlot) != null) {
			return getItem(0, selectedSlot);
		}
		return null;
	}

	public boolean hasHeldItem() {
		return getSelectedItem() != null;
	}

	public void clearInventory() {
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 7; j++) {
				player.inv.removeItem(i, j);
			}
		}
		for (int i = 0; i < 4; i++) {
			player.inv.removeItem(i);
		}
	}

	private final static int DROP_DISTANCE = 2;
	private final static int DROP_ROTATION_DURATION_MS = 250;

	public void dropItem(int row, int col, int quantity) {
		Box droppedItem = BoxHandling.addBoxItem(player.getX(), player.getY(), getItem(row, col).getId(), quantity);
		playThis(drop_item);
		removeItem(row, col, quantity);
		PlayingRenderer.updateSelectedItemNamePosition();
		switch (player.pos.getWalkingDirection()) {
			case Direction.UP -> {
				moveBox(droppedItem, 0, -DROP_DISTANCE);
			}
			case Direction.DOWN -> {
				moveBox(droppedItem, 0, DROP_DISTANCE);
			}
			case Direction.LEFT -> {
				moveBox(droppedItem, -DROP_DISTANCE, 0);
			}
			case Direction.RIGHT -> {
				moveBox(droppedItem, DROP_DISTANCE, 0);
			}
			case null, default -> {
			}
		}
		delayedRotateBox(droppedItem, 360, DROP_ROTATION_DURATION_MS);
	}

	public void dropItem(int position, int quantity) {
		Box droppedItem = BoxHandling.addBoxItem(player.getX(), player.getY(), getItem(position).getId(), quantity);
		playThis(drop_item);
		removeItem(position);
		PlayingRenderer.updateSelectedItemNamePosition();
		switch (player.pos.getWalkingDirection()) {
			case Direction.UP -> {
				moveBox(droppedItem, 0, -DROP_DISTANCE);
			}
			case Direction.DOWN -> {
				moveBox(droppedItem, 0, DROP_DISTANCE);
			}
			case Direction.LEFT -> {
				moveBox(droppedItem, -DROP_DISTANCE, 0);
			}
			case Direction.RIGHT -> {
				moveBox(droppedItem, DROP_DISTANCE, 0);
			}
			case null, default -> {
			}
		}
		delayedRotateBox(droppedItem, 360, DROP_ROTATION_DURATION_MS);
	}

	public void initDraggingItem(int row, int col, int count, boolean isExtra) {
		int position = isExtra ? col * 2 + row : -1; // Use -1 when not extra

		// If no item, start grabbing
		if (getDraggedItem() == null) {
			Item item = isExtra ? getItem(position) : getItem(row, col);
			setDraggedItem(item);
			boolean aux = isExtra ? removeItem(position) : removeItem(row, col);
		}
		// If same item, add count
		else {
			Item targetItem = isExtra ? getItem(position) : getItem(row, col);
			if (targetItem != null && getDraggedItem().getId() == targetItem.getId()) {
				int itemCount = targetItem.getCount();
				int draggedCount = getDraggedItem().getCount();

				if (itemCount + count <= getDraggedItem().getCountLimit()) { 	// Less than limit
					targetItem.setCount(itemCount + count);
					getDraggedItem().setCount(draggedCount - count);
					if (getDraggedItem().getCount() <= 0) destroyGrabbedItem();
				} else { 														// More than limit
					targetItem.setCount(getDraggedItem().getCountLimit());
					getDraggedItem().setCount(draggedCount - count);
				}
			}
			// If any other item, swap
			else {
				Item tempItem = isExtra ? getItem(position) : getItem(row, col);
				if (isExtra) bruteSetItem(getDraggedItem(), position);
				else bruteSetItem(getDraggedItem(), row, col);
				setDraggedItem(tempItem);
			}
		}
	}

	public void putDraggedItem(int row, int col, int count, boolean isExtra) {
		int position = isExtra ? col * 2 + row : -1;
		setTempItem(getDraggedItem());
		tempItem.setCount(count);
		if (isExtra) bruteSetItem(tempItem, position);
		else bruteSetItem(tempItem, row, col);
		draggedItem.setCount(draggedItem.getCount() - count);
		if (draggedItem.getCount() == 0) destroyGrabbedItem();
		destroyTempItem();
	}


	private void setDraggedItem(Item draggedItem) {
		this.draggedItem = draggedItem;
	}

	public Item getDraggedItem() {
		return this.draggedItem;
	}

	public void setTempItem(Item tempItem) {
		try {
			this.tempItem = new Item(tempItem.getId(), tempItem.getCount());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void destroyTempItem() {
		this.tempItem = null;
	}

	public Item getTempItem() {
		return tempItem;
	}

	public void destroyGrabbedItem() {
		this.draggedItem = null;
	}

	public void dropDraggedItem(int count) {
		Box droppedItem = BoxHandling.addBoxItem(player.getX(), player.getY(), getDraggedItem().getId(), count);
		playThis(drop_item);
		setTempItem(getDraggedItem());
		tempItem.setCount(count);
		draggedItem.setCount(draggedItem.getCount() - count);
		if (draggedItem.getCount() == 0) destroyGrabbedItem();
		destroyTempItem();
		PlayingRenderer.updateSelectedItemNamePosition();
		switch (player.pos.getWalkingDirection()) {
			case UP -> {
				moveBox(droppedItem, 0, 1);
			}
			case DOWN -> {
				moveBox(droppedItem, 0, 1);
			}
			case LEFT -> {
				moveBox(droppedItem, -DROP_DISTANCE, 1);
			}
			case RIGHT -> {
				moveBox(droppedItem, DROP_DISTANCE, 1);
			}
			case null, default -> {
			}
		}
		delayedRotateBox(droppedItem, 360, DROP_ROTATION_DURATION_MS);
	}

	public int getEmptySlotCount() {
		int count = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (inventory[i][j] == null) {
					count++;
				}
			}
		}
		return count;
	}

	public int[] getFirstEmptySlot() {
		int count = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (inventory[i][j] == null) {
					return new int[]{i, j};
				}
			}
		}
		return new int[]{-1, -1};
	}

	public int[] getFirstEmptyInventorySlot() {
		int count = 0;
		for (int i = 1; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (inventory[i][j] == null) {
					return new int[]{i, j};
				}
			}
		}
		return new int[]{-1, -1};
	}

	public int[] getFirstEmptyBarSlot() {
		int count = 0;
			for (int j = 0; j < cols; j++) {
				if (inventory[0][j] == null) {
					return new int[]{0, j};
				}
			}
		return new int[]{-1, -1};
	}

	public boolean hasEmptySlot() {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (inventory[i][j] == null) {
					return true;
				}
			}
		}
		return false;
	}

	public void useItem() {
		if (getSelectedItem() != null) { // Ensure something is selected
			String type = getSelectedItem().getType();
			long currentTime = currentTimeMillis();

			switch (type) {
				case "potion" -> usePotion();
				case "weapon" -> useWeapon();
			}
		}
	}

	public void usePotion() {
		int potionDelay = Integer.parseInt(getSelectedItem().getAttributes().get("potionDelay").toString());
		if (getSelectedItem().canUse()) {
			getSelectedItem().setCanUse(false);

			getSelectedItem().setUsageDelay(
				new TurnTimer(potionDelay, e -> getSelectedItem().setCanUse(true))
			);

			int playerScreenX = (int) ((player.getX() - player.pos.getCameraOffsetX()));
			int playerScreenY = (int) ((player.getY() - player.pos.getCameraOffsetY()));
			int randomPosX = (int) ((Math.random() * (40 + 40)) - 40);
			int randomPosY = (int) ((Math.random() * (25 + 25)) - 25);
			DecimalFormat df = new DecimalFormat("#.##");

			switch ((String) getSelectedItem().getAttributes().get("size")) {
				case "small":
					playThis(consume_small_pot);
					break;
				case "medium":
					playThis(consume_medium_pot);
					break;
//				case "big":
//					playThis("consume_big_pot");
//					break;
//				case "huge":
//					playThis("consume_huge_pot");
//					break;
			}

			switch (getSelectedItem().getAttributes().get("subtype").toString()) {
				case "heal" -> {
					double healAmountValue = Double.parseDouble(Integer.toString((Integer) getSelectedItem().getAttributes().get("heal")));
					if (healAmountValue == -1) {
						healAmountValue = player.getMaxHP();
					}
					String formattedHealAmount = df.format(player.receiveHeal(healAmountValue, Entity.HealFlag.of(NORMAL)));
					new FloatingText("+" + formattedHealAmount, healColor, playerScreenX + randomPosX, playerScreenY + randomPosY, true);

					Timer delayToRemove = new Timer(30, e -> {
						getSelectedItem().setCount(getSelectedItem().getCount() - 1);
						if (!getSelectedItem().isActive()) {
							removeItem(0, getSelectedSlot());
						}
					});
					delayToRemove.setRepeats(false);
					delayToRemove.start();
				}
				case "entropy" -> {
					double entropyAmountValue = Double.parseDouble(Integer.toString((Integer) getSelectedItem().getAttributes().get("entropy")));
					if (entropyAmountValue == -1) {
						entropyAmountValue = player.attr.getMaxEntropy();
					}
					String formattedEntropyAmount = df.format(player.attr.calculateEntropyGain(entropyAmountValue));

					new FloatingText("+" + formattedEntropyAmount, entropyGainColor, playerScreenX + randomPosX, playerScreenY + randomPosY, true);
					player.attr.addEntropy(entropyAmountValue);
					getSelectedItem().setCount(getSelectedItem().getCount() - 1);
					if (!getSelectedItem().isActive()) {
						removeItem(0, getSelectedSlot());
					}
				}
			}

		}
	}

	public void useWeapon() {
		int attackDelay = Integer.parseInt(getSelectedItem().getAttributes().get("attackDelay").toString());
		if (getSelectedItem().canUse()) {

			getSelectedItem().setCanUse(false);

			getSelectedItem().setUsageDelay(
				new TurnTimer(attackDelay, e -> getSelectedItem().setCanUse(true))
			);

			switch (getSelectedItem().getAttributes().get("subtype").toString()) {
				case "melee" -> {
					player.animator.update();
					switch (getSelectedItem().getAttributes().get("kind").toString()) {
						case "claw" -> player.animator.animateClaw();
						// Weapon goes upward for a bit and then swings downwards
						// Deals area damage
						case "ranged" -> {     // throw a box projectile to held direction
						}
					}
				}
			}
		}
	}

	public enum PossibleItemStats {
		vit,
		def,
		ent,
		str,
		inversion
	}

	/**
	 *
	 * @param stat as of now, should only be "vit", "def", "ent", "str" or "inversion".
	 * @return integer value
	 */
	public int getItemStat(Item item, PossibleItemStats stat) {
		try {
            return (int) item.getAttributes().get(stat.toString());
		} catch (NullPointerException e) {
			return 0;
		}
	}
}