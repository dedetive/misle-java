package com.ded.misle.input;

import java.util.function.Supplier;

public class KeyBuilder {
	private final int keyCode;
	private final Action action;
	private final KeyInputType inputType;

	private Supplier<Object> parameterSupplier = null;
	private boolean mayConflict = true;
	private long cooldown = 0;
	private long initialCooldown = 0;

	public KeyBuilder(int keyCode, Action action, KeyInputType inputType) {
		this.keyCode = keyCode;
		this.action = action;
		this.inputType = inputType;
	}

	public KeyBuilder withParameter(Supplier<Object> supplier) {
		this.parameterSupplier = supplier;
		return this;
	}

	public KeyBuilder withCooldown(long cooldown) {
		this.cooldown = cooldown;
		return this;
	}

	public KeyBuilder withInitialCooldown(long initialCooldown) {
		this.initialCooldown = initialCooldown;
		return this;
	}

	public KeyBuilder allowConflict() {
		this.mayConflict = false;
		return this;
	}

	public Key build() {
		return new Key(keyCode, action, inputType, parameterSupplier, mayConflict, cooldown, initialCooldown);
	}
}
