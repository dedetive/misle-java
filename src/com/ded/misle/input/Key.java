package com.ded.misle.input;

import java.util.*;
import java.util.function.Supplier;

public final class Key {
	private final int keyCode;
	private final Action action;
	private final KeyInputType keyInputType;
	private final Supplier<Object> parameterSupplier;
	private final boolean mayConflict;

	private final long cooldown;
	private final long initialCooldown;
	private boolean isInitialCooldown = false;
	private long lastTimeActivated = 0;
	private final List<Integer> dependencies;

	public Key(int keyCode, Action action, KeyInputType keyInputType, Supplier<Object> parameterSupplier, boolean mayConflict, long cooldown, long initialCooldown, List<Integer> dependencies) {
		this.keyCode = keyCode;
		this.action = action;
		this.keyInputType = keyInputType;
		this.parameterSupplier = parameterSupplier;
		this.mayConflict = mayConflict;
		this.cooldown = cooldown;
		this.initialCooldown = initialCooldown;
		this.dependencies = dependencies;
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
		return System.currentTimeMillis() - lastTimeActivated < (isInitialCooldown ? initialCooldown : cooldown);
	}

	public void recountCooldown() {
		this.lastTimeActivated = System.currentTimeMillis();
		this.isInitialCooldown = false;
	}

	public void applyInitialCooldown(boolean resetTime) {
		if (resetTime) this.lastTimeActivated = System.currentTimeMillis();
		this.isInitialCooldown = true;
	}

	@Override
	public String toString() {
		return "Key{" +
				"keyCode=" + keyCode + ", " +
				"action=" + action + ", " +
				"keyInputType=" + keyInputType + '}';
	}

	public Object parameter() {
		return parameterSupplier == null ? null : parameterSupplier.get();
	}

	public boolean mayConflict() {
		return mayConflict;
	}

	public List<Integer> dependencies() {
		return dependencies;
	}
}