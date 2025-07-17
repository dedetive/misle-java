package com.ded.misle.input;

import java.util.Objects;

public final class Key {
	private final int keyCode;
	private final Action action;
	private final KeyInputType keyInputType;

	private final long cooldown;
	public long lastTimeActivated = 0;

	public Key(int keyCode, Action action, KeyInputType keyInputType, long cooldown) {
		this.keyCode = keyCode;
		this.action = action;
		this.keyInputType = keyInputType;
		this.cooldown = cooldown;
	}

	public Key(int keyCode, Action action, KeyInputType keyInputType) {
		this(keyCode, action, keyInputType, 0);
	}

	public int keyCode() {
		return keyCode;
	}

	public Action action() {
		return action;
	}

	public KeyInputType keyInputType() {
		return keyInputType;
	}


	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (Key) obj;
		return this.keyCode == that.keyCode &&
				Objects.equals(this.action, that.action) &&
				Objects.equals(this.keyInputType, that.keyInputType);
	}

	@Override
	public int hashCode() {
		return Objects.hash(keyCode, action, keyInputType);
	}

	public boolean onCooldown() {
		return System.currentTimeMillis() - lastTimeActivated > cooldown;
	}

	@Override
	public String toString() {
		return "Key{" +
				"keyCode=" + keyCode + ", " +
				"action=" + action + ", " +
				"keyInputType=" + keyInputType + '}';
	}

}