package com.ded.misle.player;

import com.ded.misle.items.Item;

public class Inventory {
	private final Item[][] inventory;
	private final int rows = 4;
	private final int cols = 7;
	private int selectedSlot = 0;

	public Inventory() {
		this.inventory = new Item[rows][cols];
	}

	public boolean addItem(Item item, int row, int col) {
		if (row >= 0 && row < rows && col >= 0 && col < cols && inventory[row][col] == null) {
			inventory[row][col] = item;
			return true;
		}
		return false; // return false if position is out of bounds or slot is occupied
	}

	public boolean addItem(Item item) {
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (inventory[i][j] == null) {
					inventory[i][j] = item;
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

	public boolean removeItem(int row, int col) {
		if (row >= 0 && row < rows && col >= 0 && col < cols && inventory[row][col] != null) {
			inventory[row][col] = null;
			return true;
		}
		return false; // return false if position is out of bounds or slot is empty
	}

	public Item getItem(int row, int col) {
		if (row >= 0 && row < rows && col >= 0 && col < cols) {
			return inventory[row][col];
		}
		return null; // return null if position is out of bounds
	}

	public void displayInventory() {
		System.out.println("Player Inventory:");
		for (int i = 1; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (inventory[i][j] != null) {
					// Print item name and quantity if available
					System.out.print(inventory[i][j].getName() + " (x" + inventory[i][j].getCount() + ")");
				} else {
					System.out.print("[ ]");
				}
				System.out.print("\t");
			}
			System.out.println();
		} // Leave row 0 to the end
		for (int j = 0; j < cols; j++) {
			if (inventory[0][j] != null) {
				// Print item name and quantity if available
				System.out.print(inventory[0][j].getName() + " (x" + inventory[0][j].getCount() + ")");
			} else {
				System.out.print("[ ]");
			}
			System.out.print("\t");
		}
		System.out.println();
	}

	public int getSelectedSlot() {
		return selectedSlot;
	}

	public void setSelectedSlot(int selectedSlot) {
		this.selectedSlot = selectedSlot;
	}
}
