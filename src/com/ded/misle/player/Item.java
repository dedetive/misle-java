package com.ded.misle.player;

public class Item {
	private final String name;
	private final int Count;
	private final String type;

	public Item(String name, int quantity, String type) {
		this.name = name;
		this.Count = quantity;
		this.type = type;
	}

	public String getName() { return name; }
	public int getCount() { return Count; }
	public String getType() { return type; }
}

