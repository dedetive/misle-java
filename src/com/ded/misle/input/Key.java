package com.ded.misle.input;

import java.util.*;
import java.util.function.Supplier;

public final class Key {
	private final Object inputIdentifier;
	private final Action action;
	private final KeyInputType keyInputType;
	private final Supplier<Object> parameterSupplier;
	private final boolean mayConflict;

	private final long cooldown;
	private final long initialCooldown;
	private boolean isInitialCooldown = false;
	private long lastTimeActivated = 0;
	private final List<Integer> dependencies;

	private Key(Object input, Action action, KeyInputType keyInputType, Supplier<Object> parameterSupplier, boolean mayConflict, long cooldown, long initialCooldown, List<Integer> dependencies) {
		this.inputIdentifier = input;
		this.action = action;
		this.keyInputType = keyInputType;
		this.parameterSupplier = parameterSupplier;
		this.mayConflict = mayConflict;
		this.cooldown = cooldown;
		this.initialCooldown = initialCooldown;
		this.dependencies = dependencies;
	}

	public Key(int keyCode, Action action, KeyInputType keyInputType, Supplier<Object> parameterSupplier, boolean mayConflict, long cooldown, long initialCooldown, List<Integer> dependencies) {
		this((Object) keyCode, action, keyInputType, parameterSupplier, mayConflict, cooldown, initialCooldown, dependencies);
	}

	public Key(MouseInteraction mouseInteraction, Action action, KeyInputType keyInputType, Supplier<Object> parameterSupplier, boolean mayConflict, long cooldown, long initialCooldown, List<Integer> dependencies) {
		this((Object) mouseInteraction, action, keyInputType, parameterSupplier, mayConflict, cooldown, initialCooldown, dependencies);
	}

	public int keyCode() {
		return isKeyboard() ? (int) inputIdentifier : 0;
	}

	public MouseInteraction mouseInteraction() {
		return isMouse() ? (MouseInteraction) inputIdentifier : null;
	}

	public Action action() {
		return action;
	}

	public KeyInputType keyInputType() {
		return keyInputType;
	}

	public boolean isKeyboard() {
		return inputIdentifier instanceof Integer;
	}

	public boolean isMouse() {
		return inputIdentifier instanceof MouseInteraction;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) return true;
		if (obj == null || obj.getClass() != this.getClass()) return false;
		var that = (Key) obj;
		return  Objects.equals(this.inputIdentifier, that.inputIdentifier) &&
				Objects.equals(this.action, that.action) &&
				Objects.equals(this.keyInputType, that.keyInputType) &&
				Objects.equals(this.parameterSupplier.get(), that.parameterSupplier.get()) &&
				this.mayConflict == that.mayConflict &&
				this.cooldown == that.cooldown &&
				this.initialCooldown == that.initialCooldown &&
				this.dependencies.equals(that.dependencies);
	}

	@Override
	public int hashCode() {
		return Objects.hash(inputIdentifier, action, keyInputType, parameterSupplier, mayConflict, cooldown, initialCooldown, dependencies);
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
				"input=" + inputIdentifier + ", " +
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