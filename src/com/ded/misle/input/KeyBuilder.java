package com.ded.misle.input;

import com.ded.misle.input.interaction.InputInteraction;
import com.ded.misle.input.interaction.KeyboardInteraction;

import java.util.*;
import java.util.function.Supplier;

public class KeyBuilder {
	private final InputInteraction interaction;
	private final Action action;
	private final KeyInputType inputType;

	private Supplier<Object> parameterSupplier = null;
	private boolean mayConflict = true;
	private long cooldown = 0;
	private long initialCooldown = 0;
	private final List<Integer> dependencies = new ArrayList<>();

	public KeyBuilder(InputInteraction interaction, Action action, KeyInputType inputType) {
		this.interaction = interaction;
		this.action = action;
		this.inputType = inputType;
	}

	/**
	 * This assumes keyboard interaction.
	 */
	public KeyBuilder(int keyboardCode, Action action, KeyInputType inputType) {
		this(new KeyboardInteraction(keyboardCode), action, inputType);
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

	public KeyBuilder withDependencies(Integer... dependencies) {
		Collections.addAll(this.dependencies, dependencies);
		return this;
	}

	public Key build() {
		return new Key(interaction, action, inputType, parameterSupplier, mayConflict, cooldown, initialCooldown, dependencies);
	}
}
