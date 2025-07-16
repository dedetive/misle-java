package com.ded.misle.input;

import com.ded.misle.game.GamePanel;
import com.ded.misle.renderer.*;
import com.ded.misle.world.boxes.BoxManipulation;
import com.ded.misle.world.data.Direction;
import com.ded.misle.world.entities.Entity;
import com.ded.misle.world.logic.TurnManager;

import java.awt.*;
import java.util.Arrays;
import java.util.function.Consumer;

import static com.ded.misle.game.GamePanel.*;
import static com.ded.misle.input.KeyHandlerDep.removeExtraChars;
import static com.ded.misle.renderer.DialogRenderer.fillLetterDisplay;
import static com.ded.misle.renderer.DialogRenderer.isLetterDisplayFull;
import static com.ded.misle.renderer.MenuButton.clearButtons;
import static com.ded.misle.renderer.SaveCreator.playerName;
import static com.ded.misle.renderer.SettingsMenuRenderer.moveSettingMenu;
import static com.ded.misle.renderer.image.ImageManager.getCurrentScreen;
import static com.ded.misle.renderer.image.ImageManager.saveScreenshot;
import static com.ded.misle.world.data.Direction.*;
import static com.ded.misle.world.data.items.Item.createItem;
import static com.ded.misle.world.entities.npcs.NPCDialog.getCurrentTalkingTo;
import static com.ded.misle.world.logic.PhysicsEngine.isSpaceOccupied;

public enum Action {
	//region Game state
	PANIC_CRASH(() -> System.exit(0)),
	PAUSE_GAME(MenuRenderer::pauseGame),
	//endregion
	//region Plans
	CANCEL_PLANNING(() -> player.getPlanner().setPlanning(false)),
	START_PLANNING(() -> player.getNewPlanner().setPlanning(true)),
	SKIP_STEP(() -> player.getPlanner().skipStep()),
	TOGGLE_PLAN_QUICK_EXECUTION(() -> player.getPlanner().toggleQuickExecution()),
	EXECUTE_PLAN(() -> player.getPlanner().executePlan()),
	//endregion
	//region Inventory
	SELECT_INVENTORY_SLOT((slot) -> player.inv.setSelectedSlot((Integer) slot)),
	DROP_SINGLE(() -> player.inv.dropItem(0, player.inv.getSelectedSlot(), 1)),
	DROP_ALL(() -> player.inv.dropItem(0, player.inv.getSelectedSlot(), player.inv.getSelectedItem().getCount())),
	USE(KeyHandlerDep::pressUseButton),
	TOGGLE_INVENTORY(() ->
			gameState =
					gameState == GameState.PLAYING ? GameState.INVENTORY : GameState.PLAYING),
	INVENTORY_SWAP((pos) -> {
		int[] p = (int[]) pos;
		player.inv.setTempItem(player.inv.getItem(p[0], p[1]));
		player.inv.bruteSetItem(player.inv.getItem(0, p[2]), p[0], p[1]);
		player.inv.bruteSetItem(player.inv.getTempItem(), 0, p[2]);
		player.inv.destroyTempItem();
	}),
	INVENTORY_DROP_SINGLE((pos) -> {
		int[] p = (int[]) pos;
		player.inv.dropItem(p[0], p[1], 1);
	}),
	INVENTORY_DROP_ALL((pos) -> {
		int[] p = (int[]) pos;
		player.inv.dropItem(p[0], p[1], player.inv.getItem(p[0], p[1]).getCount());
	}),
	INVENTORY_EXTRA_DROP_SINGLE((index) -> {
		int i = (Integer) index;
		player.inv.dropItem(i, 1);
	}),
	INVENTORY_EXTRA_DROP_ALL((index) -> {
		int i = (Integer) index;
		player.inv.dropItem(i, player.inv.getItem(i).getCount());
	}),
	//endregion
	//region Movement
		//region Regular
		MOVE((direction) -> {
			switch ((Direction) direction) {
				case UP -> BoxManipulation.movePlayer(player.getX(), player.getY() - 1);
				case DOWN -> BoxManipulation.movePlayer(player.getX(), player.getY() + 1);
				case LEFT -> BoxManipulation.movePlayer(player.getX() - 1, player.getY());
				case RIGHT -> BoxManipulation.movePlayer(player.getX() + 1, player.getY());
			}
		}),
		//endregion
		//region Bump onto entity
		MOVE_BUMP_REGULAR((direction) -> player.updateLastDirection((Direction) direction)),
		MOVE_BUMP_ENTITY((direction) -> {
			player.updateLastDirection((Direction) direction);
			player.attack();
		}),
		//endregion
		//region Stuck
		MOVE_STUCK((direction) -> {
			switch ((Direction) direction) {
				case UP -> BoxManipulation.movePlayer(player.getX(), player.getY() - 1);
				case DOWN -> BoxManipulation.movePlayer(player.getX(), player.getY() + 1);
				case LEFT -> BoxManipulation.movePlayer(player.getX() - 1, player.getY());
				case RIGHT -> BoxManipulation.movePlayer(player.getX() + 1, player.getY());
			}
			player.takeDamage(5 + player.getMaxHP() / 120, Entity.DamageFlag.of(
					Entity.DamageFlag.ABSOLUTE,
					Entity.DamageFlag.LOCKER
			), 100, NONE);
		}),
		//endregion
		//region Plan
		MOVE_PLAN((direction) -> {
			Point last = player.getPlanner().getEnd();
			int x = last != null ? last.x : player.getX();
			int y = last != null ? last.y : player.getY();
			Point target = switch ((Direction) direction) {
				case UP -> new Point(x, y - 1);
				case DOWN -> new Point(x, y + 1);
				case LEFT -> new Point(x - 1, y);
				case RIGHT -> new Point(x + 1, y);
				default -> new Point(x, y);
			};
			if (!isSpaceOccupied(target.x, target.y)) {
				player.getPlanner().attemptToMove(target);
			} else if (player.getX() == target.x && player.getY() == target.y) {
				player.getPlanner().attemptToMove(target);
			} else if (target.x > 0 && target.x < worldWidth &&
					target.y > 0 && target.y < worldHeight &&
					Arrays.stream(player.pos.world.grid[target.x][target.y]).anyMatch(box -> box instanceof Entity)) {
				player.getPlanner().addEnemyPoint(target);
			}
		}),
		//endregion
	//endregion
	//region Save creator
	APPEND_NAME_CHAR((ch) -> {
		if (playerName.length() < 16)
				playerName.append(removeExtraChars((Character) ch));
		}),
	REMOVE_NAME_CHAR(() -> playerName.setLength(Math.max(playerName.length() - 1, 0))),
	CONFIRM_NAME(SaveCreator::confirmName),
	//endregion
	//region Dialog
	ADVANCE_DIALOG(() -> {
		if (isLetterDisplayFull()) {
			getCurrentTalkingTo().incrementDialogIndex();
		} else {
			fillLetterDisplay();
		}
	}),
//endregion
	//region Menus
	GO_TO_PREVIOUS_MENU(MenuRenderer::goToPreviousMenu),
	SAVE_SELECTOR_CANCEL_DELETE(() -> {
		SaveSelector.askingToDelete = -1;
		clearButtons();
	}),
	SETTING_MENU_MOVE_LEFT(() -> {
		moveSettingMenu(-1);
		SettingsMenuRenderer.leftKeyIndicatorWidth = 19;
	}),
	SETTING_MENU_MOVE_RIGHT(() -> {
		moveSettingMenu(1);
		SettingsMenuRenderer.rightKeyIndicatorWidth = 19;
	}),
	//endregion
	//region Misc
	SCREENSHOT(() -> saveScreenshot(getCurrentScreen())),
	TRIGGER_LOGIC(TurnManager::requestNewTurn),
	//endregion
	//region Debug
	DEBUG_GIVE_ITEMS(() -> {
		for (int i = 1; i <= 27; i++) {
			if (i != 5) player.inv.addItem(createItem(i, 1));
		}
	}),
	DEBUG_CLEAR_INV(() -> player.inv.clearInventory()),
//endregion

	//region
	;
	private final Consumer<Object> paramAction;
	private final Runnable noParamAction;

	Action(Consumer<Object> paramAction) {
		this.paramAction = paramAction;
		this.noParamAction = null;
	}

	Action(Runnable noParamAction) {
		this.noParamAction = noParamAction;
		this.paramAction = null;
	}

	public void execute() {
		if (noParamAction == null) {
			System.err.println("Action " + this.name() + " requires an object!");
			return;
		}
		noParamAction.run();
	}

	public <T> void execute(T obj) {
		if (paramAction == null) {
			System.err.println("Action " + this.name() + " does not accept parameters, but received: " + obj);
			execute();
			return;
		}
		try {
			paramAction.accept(obj);
		} catch (ClassCastException e) {
			System.err.println("Action " + this.name() +
					" received an object of invalid type: " +
					(obj != null ? obj.getClass().getName() : "null"));
		}
	}
	//endregion
}
