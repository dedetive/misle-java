package com.ded.misle.input;

import java.util.Objects;
import java.util.function.Supplier;

public final class Key {
	private final int keyCode;
	private final Action action;
	private final KeyInputType keyInputType;
	private final Supplier<Object> parameterSupplier;
	private final boolean mayConflict;

	private final long cooldown;
	public long lastTimeActivated = 0;

	public Key(int keyCode, Action action, KeyInputType keyInputType, Supplier<Object> parameterSupplier, boolean mayConflict, long cooldown) {
		this.keyCode = keyCode;
		this.action = action;
		this.keyInputType = keyInputType;
		this.parameterSupplier = parameterSupplier;
		this.mayConflict = mayConflict;
		this.cooldown = cooldown;
	}

	public Key(int keyCode, Action action, KeyInputType keyInputType, Supplier<Object> parameterSupplier, boolean mayConflict) {
		this(keyCode, action, keyInputType, parameterSupplier, mayConflict, 0);
	}

	public static Key of(int keyCode, Action action, KeyInputType keyInputType, long cooldown, boolean mayConflict) {
		return new Key(keyCode, action, keyInputType, null, mayConflict, cooldown);
	}

	public static Key of(int keyCode, Action action, KeyInputType keyInputType, boolean mayConflict) {
		return new Key(keyCode, action, keyInputType, null, mayConflict, 0);
	}

	public Key(int keyCode, Action action, KeyInputType keyInputType, Supplier<Object> parameterSupplier) {
		this(keyCode, action, keyInputType, parameterSupplier, false, 0);
	}

	public static Key of(int keyCode, Action action, KeyInputType keyInputType, long cooldown) {
		return new Key(keyCode, action, keyInputType, null, false, cooldown);
	}

	public static Key of(int keyCode, Action action, KeyInputType keyInputType) {
		return new Key(keyCode, action, keyInputType, null, false, 0);
	}

	public static Key of(int keyCode, Action action, KeyInputType keyInputType, Supplier<Object> parameterSupplier, long cooldown, boolean mayConflict) {
		return new Key(keyCode, action, keyInputType, parameterSupplier, mayConflict, cooldown);
	}

	public static Key of(int keyCode, Action action, KeyInputType keyInputType, Supplier<Object> parameterSupplier, boolean mayConflict) {
		return new Key(keyCode, action, keyInputType, parameterSupplier, mayConflict, 0);
	}

	public static Key of(int keyCode, Action action, KeyInputType keyInputType, Supplier<Object> parameterSupplier, long cooldown) {
		return new Key(keyCode, action, keyInputType, parameterSupplier, false, cooldown);
	}

	public static Key of(int keyCode, Action action, KeyInputType keyInputType, Supplier<Object> parameterSupplier) {
		return new Key(keyCode, action, keyInputType, parameterSupplier, false, 0);
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
		return System.currentTimeMillis() - lastTimeActivated < cooldown;
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
}