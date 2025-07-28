package com.ded.misle.input;

import com.ded.misle.renderer.*;
import com.ded.misle.world.boxes.BoxManipulation;
import com.ded.misle.world.data.Direction;
import com.ded.misle.world.entities.Entity;
import com.ded.misle.world.logic.TurnManager;

import java.awt.*;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.ded.misle.game.GamePanel.*;
import static com.ded.misle.input.KeyHelper.removeExtraChars;
import static com.ded.misle.renderer.DialogRenderer.fillLetterDisplay;
import static com.ded.misle.renderer.DialogRenderer.isLetterDisplayFull;
import static com.ded.misle.renderer.MainRenderer.softGameStart;
import static com.ded.misle.renderer.MenuButton.clearButtons;
import static com.ded.misle.renderer.MenuRenderer.pauseGame;
import static com.ded.misle.renderer.SaveCreator.playerName;
import static com.ded.misle.renderer.SaveSelector.askingToDelete;
import static com.ded.misle.renderer.SettingsMenuRenderer.moveSettingMenu;
import static com.ded.misle.renderer.image.ImageManager.getCurrentScreen;
import static com.ded.misle.renderer.image.ImageManager.saveScreenshot;
import static com.ded.misle.world.data.Direction.*;
import static com.ded.misle.world.data.items.Item.createItem;
import static com.ded.misle.world.entities.npcs.NPCDialog.getCurrentTalkingTo;
import static com.ded.misle.world.logic.PhysicsEngine.isSpaceOccupied;

public class Action {
	//region Game state
	public static final Action PANIC_CRASH = new Action(() -> System.exit(0), e -> true, false);
	public static final Action PAUSE = new Action(() -> {
		pauseGame();
		clearButtons();
	}, e -> gameState == GameState.PLAYING && !player.isWaiting(), false);
	public static final Action UNPAUSE = new Action(() -> {
		softGameStart();
		clearButtons();
	}, e -> gameState == GameState.PAUSE_MENU, false);
	//endregion
	//region Plans
	public static final Action CANCEL_PLANNING = new Action(() -> player.getPlanner().setPlanning(false),
			e -> gameState == GameState.PLAYING && !player.isWaiting() && player.getPlanner().isPlanning() && !player.getPlanner().isExecuting(), false);
	public static final Action START_PLANNING = new Action(() -> player.getNewPlanner().setPlanning(true),
			e -> gameState == GameState.PLAYING && !player.isWaiting() && !player.getPlanner().isPlanning() && !player.getPlanner().isExecuting(), false);
	public static final Action SKIP_STEP = new Action(() -> player.getPlanner().skipStep(),
			e -> gameState == GameState.PLAYING && !player.isWaiting() && player.getPlanner().isExecuting(), false);
	public static final Action TOGGLE_PLAN_QUICK_EXECUTION = new Action(() -> player.getPlanner().toggleQuickExecution(),
			e -> gameState == GameState.PLAYING && !player.isWaiting() && player.getPlanner().isExecuting(), false);
	public static final Action EXECUTE_PLAN = new Action(() -> player.getPlanner().executePlan(),
			e -> gameState == GameState.PLAYING && !player.isWaiting() && player.getPlanner().isPlanning() && !player.getPlanner().isExecuting(), false);
	//endregion
	//region Inventory
	public static final Action SELECT_INVENTORY_SLOT = new Action((slot) -> player.inv.setSelectedSlot((Integer) slot),
			e -> gameState == GameState.PLAYING && !player.isWaiting() && !player.getPlanner().isPlanning(), false);
	public static final Action DROP_SINGLE = new Action(() -> player.inv.dropItem(0, player.inv.getSelectedSlot(), 1),
			e -> gameState == GameState.PLAYING && !player.isWaiting() && !player.getPlanner().isPlanning() && player.inv.hasHeldItem(), true);
	public static final Action DROP_ALL = new Action(() -> player.inv.dropItem(0, player.inv.getSelectedSlot(), player.inv.getSelectedItem().getCount()),
			e -> gameState == GameState.PLAYING && !player.isWaiting() && !player.getPlanner().isPlanning() && player.inv.hasHeldItem(), true);
	public static final Action USE = new Action(KeyHelper::pressUseButton,
			e -> gameState == GameState.PLAYING && !player.isWaiting() && !player.getPlanner().isPlanning() && !player.getPlanner().isExecuting(), true);
	public static final Action TOGGLE_INVENTORY = new Action(() -> gameState = gameState == GameState.PLAYING ? GameState.INVENTORY : GameState.PLAYING,
			e -> (gameState == GameState.PLAYING || gameState == GameState.INVENTORY) && !player.getPlanner().isPlanning(), false);
	public static final Action CLOSE_INVENTORY = new Action(() -> gameState = GameState.PLAYING,
			e -> gameState == GameState.INVENTORY, false);
	public static final Action INVENTORY_SWAP = new Action((pos) -> {
		int[] p = (int[]) pos;
		player.inv.setTempItem(player.inv.getItem(p[0], p[1]));
		player.inv.bruteSetItem(player.inv.getItem(0, p[2]), p[0], p[1]);
		player.inv.bruteSetItem(player.inv.getTempItem(), 0, p[2]);
		player.inv.destroyTempItem();
	}, e -> gameState == GameState.INVENTORY && isValidHoveredSlot(mouseHandler.getHoveredSlot()) && player.inv.getItem(((int[]) e)[0], ((int[]) e)[1]) != null, false);
	public static final Action INVENTORY_DROP_SINGLE = new Action((pos) -> {
		int[] p = (int[]) pos;
		player.inv.dropItem(p[0], p[1], 1);
	}, e -> gameState == GameState.INVENTORY && isValidHoveredSlot(mouseHandler.getHoveredSlot()) && player.inv.getItem(((int[]) e)[0], ((int[]) e)[1]) != null, true);
	public static final Action INVENTORY_DROP_ALL = new Action((pos) -> {
		int[] p = (int[]) pos;
		player.inv.dropItem(p[0], p[1], player.inv.getItem(p[0], p[1]).getCount());
	}, e -> gameState == GameState.INVENTORY && isValidHoveredSlot(mouseHandler.getHoveredSlot()) && player.inv.getItem(((int[]) e)[0], ((int[]) e)[1]) != null, true);
	public static final Action INVENTORY_EXTRA_DROP_SINGLE = new Action((index) -> {
		int i = (Integer) index;
		player.inv.dropItem(i, 1);
	}, e -> gameState == GameState.INVENTORY && isValidExtraSlot(mouseHandler.getExtraHoveredSlot()) && player.inv.getItem((Integer) e) != null, true);
	public static final Action INVENTORY_EXTRA_DROP_ALL = new Action((index) -> {
		int i = (Integer) index;
		player.inv.dropItem(i, player.inv.getItem(i).getCount());
	}, e -> gameState == GameState.INVENTORY && isValidExtraSlot(mouseHandler.getExtraHoveredSlot()) && player.inv.getItem((Integer) e) != null, true);
	//endregion
	//region Movement
		//region Regular
	public static final Action MOVE = new Action((direction) -> {
			player.setWaiting(true);
		BoxManipulation.movePlayer(getDirectionPoint(direction).x, getDirectionPoint(direction).y);
		player.setWaiting(false);
	}, e -> gameState == GameState.PLAYING &&
			!player.isWaiting() &&
			!player.attr.isDead() &&
			!player.getPlanner().isExecuting() &&
			!player.getPlanner().isPlanning() &&
			!isSpaceOccupied(offsetPlayerPos(e).x, offsetPlayerPos(e).y, player), true);
		//endregion
		//region Bump onto entity
		public static final Action MOVE_BUMP_REGULAR = new Action((direction) -> player.updateLastDirection((Direction) direction),
				e -> gameState == GameState.PLAYING &&
						!player.isWaiting() &&
						!player.attr.isDead() &&
						!player.getPlanner().isExecuting() &&
						!player.getPlanner().isPlanning() &&
						!isSpaceOccupied(player.getX(), player.getY(), player) &&
						!(offsetPlayerPos(e).x > 0 && offsetPlayerPos(e).x < worldWidth &&
						offsetPlayerPos(e).y > 0 && offsetPlayerPos(e).y < worldHeight &&
						Arrays.stream(player.pos.world.grid[offsetPlayerPos(e).x][offsetPlayerPos(e).y]).
								anyMatch(box -> box instanceof Entity)),
				true);
	public static final Action MOVE_BUMP_ENTITY = new Action((direction) -> {
			player.updateLastDirection((Direction) direction);
			player.attack();
		}, e -> gameState == GameState.PLAYING &&
				!player.isWaiting() &&
				!player.attr.isDead() &&
				!player.getPlanner().isExecuting() &&
				!player.getPlanner().isPlanning() &&
				!isSpaceOccupied(player.getX(), player.getY(), player) &&
				(offsetPlayerPos(e).x > 0 && offsetPlayerPos(e).x < worldWidth &&
						offsetPlayerPos(e).y > 0 && offsetPlayerPos(e).y < worldHeight &&
						Arrays.stream(player.pos.world.grid[offsetPlayerPos(e).x][offsetPlayerPos(e).y]).
								anyMatch(box -> box instanceof Entity)), true);
		//endregion
		//region Stuck
		public static final Action MOVE_STUCK = new Action((direction) -> {
			BoxManipulation.movePlayer(getDirectionPoint(direction).x, getDirectionPoint(direction).y);
			player.takeDamage(5 + player.getMaxHP() / 120, Entity.DamageFlag.of(
					Entity.DamageFlag.ABSOLUTE,
					Entity.DamageFlag.LOCKER
			), 100, NONE);
		}, e -> gameState == GameState.PLAYING &&
				!player.isWaiting() &&
				!player.attr.isDead() &&
				!player.getPlanner().isExecuting() &&
				isSpaceOccupied(player.getX(), player.getY(), player), true);
		//endregion
		//region Plan
		public static final Action MOVE_PLAN = new Action((direction) -> {
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
		}, e -> gameState == GameState.PLAYING &&
				!player.isWaiting() &&
				player.getPlanner().isPlanning() &&
				!player.getPlanner().isExecuting(), false);
		//endregion
	//endregion
	//region Save creator
		public static final Action APPEND_NAME_CHAR = new Action((ch) -> {
		if (playerName.length() < 16)
				playerName.append(removeExtraChars((Character) ch));
		}, e -> gameState == GameState.SAVE_CREATOR && playerName.length() < 16, false);
	public static final Action REMOVE_NAME_CHAR = new Action(() -> playerName.setLength(Math.max(playerName.length() - 1, 0)),
			e -> gameState == GameState.SAVE_CREATOR && !playerName.isEmpty(), false);
	public static final Action REMOVE_WHOLE_NAME = new Action(() -> playerName.setLength(0),
			e -> gameState == GameState.SAVE_CREATOR && !playerName.isEmpty(), false);
	public static final Action CONFIRM_NAME = new Action(SaveCreator::confirmName,
			e -> gameState == GameState.SAVE_CREATOR, false);
	//endregion
	//region Dialog
	public static final Action ADVANCE_DIALOG = new Action(() -> {
		if (isLetterDisplayFull()) {
			getCurrentTalkingTo().incrementDialogIndex();
		} else {
			fillLetterDisplay();
		}
	}, e -> gameState == GameState.DIALOG, false);
//endregion
	//region Menus
public static final Action GO_TO_PREVIOUS_MENU = new Action(MenuRenderer::goToPreviousMenu,
		e -> (gameState == GameState.OPTIONS_MENU || gameState == GameState.SAVE_SELECTOR || gameState == GameState.SAVE_CREATOR) && askingToDelete == -1, false);
	public static final Action SAVE_SELECTOR_CANCEL_DELETE = new Action(() -> {
		askingToDelete = -1;
		clearButtons();
	}, e -> gameState == GameState.SAVE_SELECTOR && askingToDelete > -1, false);
	public static final Action SETTING_MENU_MOVE_LEFT = new Action(() -> {
		moveSettingMenu(-1);
		SettingsMenuRenderer.leftKeyIndicatorWidth = 19;
	}, e -> gameState == GameState.OPTIONS_MENU, false);
	public static final Action SETTING_MENU_MOVE_RIGHT = new Action(() -> {
		moveSettingMenu(1);
		SettingsMenuRenderer.rightKeyIndicatorWidth = 19;
	}, e -> gameState == GameState.OPTIONS_MENU, false);
	//endregion
	//region Misc
	public static final Action SCREENSHOT = new Action(() -> saveScreenshot(getCurrentScreen()),
			e -> true, false);
	public static final Action TRIGGER_LOGIC = new Action(() -> {},
			e -> !player.getPlanner().isPlanning(), true);
	//endregion
	//region Debug
	public static final Action DEBUG_GIVE_ITEMS = new Action(() -> {
		for (int i = 1; i <= 27; i++) {
			if (i != 5) player.inv.addItem(createItem(i, 1));
		}
	}, e -> true, false);
	public static final Action DEBUG_CLEAR_INV = new Action(() -> player.inv.clearInventory(), e -> true, false);
//endregion

	//region
	;

	private final Consumer<Object> paramAction;
	private final Runnable noParamAction;
	private final Predicate<Object> condition;
	private final boolean triggersLogic;

	public Action(Consumer<Object> paramAction, Predicate<Object> condition, boolean triggersLogic) {
		this.paramAction = paramAction;
		this.condition = condition;
		this.triggersLogic = triggersLogic;
		this.noParamAction = null;
	}

	public Action(Runnable noParamAction, Predicate<Object> condition, boolean triggersLogic) {
		this.noParamAction = noParamAction;
		this.condition = condition;
		this.triggersLogic = triggersLogic;
		this.paramAction = null;
	}

	public void execute() {
		if (!canExecute()) return;
		if (noParamAction == null) {
			System.err.println("Action " + this.toString() + " requires an object!");
			return;
		}
		noParamAction.run();
		if (this.triggersLogic) TurnManager.requestNewTurn();
	}

	public <T> void execute(T obj) {
		if (!canExecute(obj)) return;
		if (paramAction == null) {
			System.err.println("Action " + this.toString() + " does not accept parameters, but received: " + obj);
			execute();
			return;
		}
		try {
			paramAction.accept(obj);
			if (this.triggersLogic) TurnManager.requestNewTurn();
		} catch (ClassCastException e) {
			System.err.println("Action " + this.toString() +
					" received an object of invalid type: " +
					(obj != null ? obj.getClass().getName() : "null"));
		}
	}

	public <T> boolean canExecute(T obj) {
		return condition.test(obj);
	}
	public boolean canExecute() {
		return condition.test(null);
	}
	//endregion

	//region Aux
	private static boolean isValidHoveredSlot(int[] hoveredSlot) {
		return hoveredSlot[0] >= 0 && hoveredSlot[1] >= 0;
	}
	private static boolean isValidExtraSlot(int[] extraSlot) {
		return extraSlot[0] >= 0 && extraSlot[1] >= 0;
	}
	private static <T> Point offsetPlayerPos(T direction) {
		return new Point(getDirectionPoint(direction).x + player.getX(), getDirectionPoint(direction).y + player.getY());
	}
	private static <T> Point getDirectionPoint(T direction) {
		return switch ((Direction) direction) {
			case UP -> new Point(0, -1);
			case DOWN -> new Point(0, 1);
			case LEFT -> new Point(-1, 0);
			case RIGHT -> new Point(1, 0);
			default -> new Point(0, 0);
		};
	}
	//endregion
}
