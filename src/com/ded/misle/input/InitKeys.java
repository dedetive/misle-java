package com.ded.misle.input;

import com.ded.misle.world.data.Direction;

import java.awt.event.KeyEvent;
import java.util.*;

import static java.awt.event.KeyEvent.*;

public final class InitKeys {
	private InitKeys() {}

	private static final List<Key> keys = new ArrayList<>(List.of(
		//region Plans
			new KeyBuilder(VK_ENTER, Action.EXECUTE_PLAN, KeyInputType.ON_RELEASE)
					.build(),
			new KeyBuilder(VK_ENTER, Action.TOGGLE_PLAN_QUICK_EXECUTION, KeyInputType.ON_RELEASE)
					.build(),
			new KeyBuilder(VK_SPACE, Action.TOGGLE_PLAN_QUICK_EXECUTION, KeyInputType.ON_RELEASE)
					.build(),
			new KeyBuilder(VK_SPACE, Action.CANCEL_PLANNING, KeyInputType.ON_RELEASE)
					.build(),
			new KeyBuilder(VK_SPACE, Action.START_PLANNING, KeyInputType.ON_RELEASE)
					.build(),
			new KeyBuilder(VK_Z, Action.SKIP_STEP, KeyInputType.ON_RELEASE)
					.build(),
		//endregion
		//region Menus
			new KeyBuilder(VK_ESCAPE, Action.PAUSE, KeyInputType.ON_RELEASE)
					.build(),
			new KeyBuilder(VK_ESCAPE, Action.UNPAUSE, KeyInputType.ON_RELEASE)
					.build(),
			new KeyBuilder(VK_ESCAPE, Action.GO_TO_PREVIOUS_MENU, KeyInputType.ON_RELEASE)
					.build(),
			new KeyBuilder(VK_ESCAPE, Action.SAVE_SELECTOR_CANCEL_DELETE, KeyInputType.ON_RELEASE)
					.build(),
			new KeyBuilder(VK_A, Action.SETTING_MENU_MOVE_LEFT, KeyInputType.ON_RELEASE)
					.build(),
			new KeyBuilder(VK_D, Action.SETTING_MENU_MOVE_RIGHT, KeyInputType.ON_RELEASE)
					.build(),
		//endregion
		//region Inventory
			new KeyBuilder(VK_Q, Action.DROP_SINGLE, KeyInputType.ON_RELEASE)
					.allowConflict()
					.build(),
			new KeyBuilder(VK_Q, Action.DROP_ALL, KeyInputType.ON_RELEASE)
					.withDependencies(VK_SHIFT)
					.build(),
			new KeyBuilder(VK_E, Action.TOGGLE_INVENTORY, KeyInputType.ON_PRESS)
					.build(),
			new KeyBuilder(VK_ESCAPE, Action.CLOSE_INVENTORY, KeyInputType.ON_RELEASE)
					.build(),
			new KeyBuilder(VK_Z, Action.USE, KeyInputType.ON_RELEASE)
					.build(),
		//endregion
		//region Save Creator
			new KeyBuilder(VK_BACK_SPACE, Action.REMOVE_WHOLE_NAME, KeyInputType.ON_PRESS)
					.withDependencies(VK_CONTROL)
					.build(),
			new KeyBuilder(VK_BACK_SPACE, Action.REMOVE_NAME_CHAR, KeyInputType.ON_PRESS)
					.build(),
			new KeyBuilder(VK_BACK_SPACE, Action.REMOVE_NAME_CHAR, KeyInputType.ON_HOLD)
					.withInitialCooldown(500)
					.withCooldown(50)
					.build(),
			new KeyBuilder(VK_ENTER, Action.CONFIRM_NAME, KeyInputType.ON_RELEASE)
					.build(),
		//endregion
		//region Misc
			new KeyBuilder(VK_F12, Action.SCREENSHOT, KeyInputType.ON_PRESS)
					.build(),
			new KeyBuilder(VK_MINUS, Action.PANIC_CRASH, KeyInputType.ON_HOLD)
					.withDependencies(VK_CONTROL, VK_SHIFT, VK_ALT)
					.withInitialCooldown(3000)
					.build(),
			new KeyBuilder(VK_OPEN_BRACKET, Action.DEBUG_GIVE_ITEMS, KeyInputType.ON_PRESS)
					.build(),
			new KeyBuilder(VK_CLOSE_BRACKET, Action.DEBUG_CLEAR_INV, KeyInputType.ON_PRESS)
					.build(),
		//endregion
		//region Dependency helpers
			new KeyBuilder(VK_CONTROL, null, KeyInputType.NONE)
					.build(),
			new KeyBuilder(VK_SHIFT, null, KeyInputType.NONE)
					.build(),
			new KeyBuilder(VK_ALT, null, KeyInputType.NONE)
					.build()
		//endregion
		));

	public static void init() {
		keys.addAll(getDirectedMoves(VK_LEFT, Direction.LEFT));
		keys.addAll(getDirectedMoves(VK_A, Direction.LEFT));
		keys.addAll(getDirectedMoves(VK_RIGHT, Direction.RIGHT));
		keys.addAll(getDirectedMoves(VK_D, Direction.RIGHT));
		keys.addAll(getDirectedMoves(VK_UP, Direction.UP));
		keys.addAll(getDirectedMoves(VK_W, Direction.UP));
		keys.addAll(getDirectedMoves(VK_DOWN, Direction.DOWN));
		keys.addAll(getDirectedMoves(VK_S, Direction.DOWN));
		keys.addAll(getInventoryNumbers());
		keys.addAll(getCharacterInputKeys());

		for (Key key : keys) {
			KeyRegistry.addKey(key);
		}
	}

//region Aux
	private static List<Key> getDirectedMoves(int keyCode, Direction direction) {
		return List.of(
				new KeyBuilder(keyCode, Action.MOVE, KeyInputType.ON_RELEASE)
						.withParameter(() -> direction)
						.build(),

				new KeyBuilder(keyCode, Action.MOVE, KeyInputType.ON_HOLD)
						.withParameter(() -> direction)
						.withCooldown(300)
						.withInitialCooldown(800)
						.build(),

				new KeyBuilder(keyCode, Action.MOVE_BUMP_REGULAR, KeyInputType.ON_RELEASE)
						.withParameter(() -> direction)
						.build(),

				new KeyBuilder(keyCode, Action.MOVE_BUMP_ENTITY, KeyInputType.ON_RELEASE)
						.withParameter(() -> direction)
						.build(),

				new KeyBuilder(keyCode, Action.MOVE_PLAN, KeyInputType.ON_RELEASE)
						.withParameter(() -> direction)
						.build(),

				new KeyBuilder(keyCode, Action.SKIP_STEP, KeyInputType.ON_RELEASE)
						.build()
		);
	}

	private static List<Key> getInventoryNumbers() {
		return new ArrayList<>() {{
			for (int i = 0; i < 7; i++) {
				int slot = i - 1;
				this.add(new KeyBuilder(VK_0 + i, Action.SELECT_INVENTORY_SLOT, KeyInputType.ON_RELEASE)
						.withParameter(() -> slot)
						.build());
			}
		}};
	}

	private static List<Key> getCharacterInputKeys() {
		List<Key> keys = new ArrayList<>();
		for (int keyCode = KeyEvent.VK_A; keyCode <= KeyEvent.VK_Z; keyCode++) {
			int finalKeyCode = keyCode;
			keys.add(new KeyBuilder(keyCode, Action.APPEND_NAME_CHAR, KeyInputType.ON_PRESS)
					.withParameter(() -> KeyEvent.getKeyText(finalKeyCode).toUpperCase().charAt(0))
					.withDependencies(VK_SHIFT)
					.allowConflict()
					.build());
			keys.add(new KeyBuilder(keyCode, Action.APPEND_NAME_CHAR, KeyInputType.ON_PRESS)
					.withParameter(() -> KeyEvent.getKeyText(finalKeyCode).toLowerCase().charAt(0))
					.withDependencies(-VK_SHIFT)
					.allowConflict()
					.build());
		}
		for (int keyCode = KeyEvent.VK_0; keyCode <= KeyEvent.VK_9; keyCode++) {
			int finalKeyCode = keyCode;
			keys.add(new KeyBuilder(keyCode, Action.APPEND_NAME_CHAR, KeyInputType.ON_PRESS)
					.withParameter(() -> KeyEvent.getKeyText(finalKeyCode).charAt(0))
					.allowConflict()
					.build());
		}
		keys.add(new KeyBuilder(KeyEvent.VK_SPACE, Action.APPEND_NAME_CHAR, KeyInputType.ON_PRESS)
				.withParameter(() -> ' ')
				.allowConflict()
				.build());
		return keys;
	}

//endregion
}