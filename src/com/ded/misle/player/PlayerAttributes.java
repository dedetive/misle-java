package com.ded.misle.player;

import com.ded.misle.boxes.Box;

import java.util.Timer;
import java.util.TimerTask;

import static com.ded.misle.GamePanel.player;
import static com.ded.misle.player.Inventory.PossibleItemStats.*;
import static com.ded.misle.renderer.MainRenderer.fadeInThenOut;
import static com.ded.misle.renderer.MainRenderer.fadeOut;
import static com.ded.misle.Launcher.scale;
import static com.ded.misle.items.Item.updateMaxStackSize;

@SuppressWarnings("ConditionalExpressionWithIdenticalBranches")
public class PlayerAttributes {

	// BASE ATTRIBUTES

	private double playerSpeed;

	// STATS ATTRIBUTES

	private double maxEntropy;
	private double playerSpeedModifier;
	private double entropy;
	private double environmentSpeedModifier;

	// XP

	private double xp;
	private double XPtoLevelUp;
	private int level = 1;
	private int levelUpPoints;
	public enum LevelUpPointsUsage {
		MAX_HP,
		MAX_ENTROPY,
		DEFENSE,
		REGENERATION,
		SPEED
	}

	// COINS

	private int balance;

	// LEVEL ATTRIBUTES

	double levelMaxHP;
	double levelMaxEntropy;
	double levelDefense;
	double levelRegenerationQuality;
	double levelSpeed;

	// EQUIPMENT ATTRIBUTES

	double equipmentMaxHP;
	double equipmentMaxEntropy;
	double equipmentDefense;
	double equipmentRegenerationQuality;
	double equipmentSpeed;
	double equipmentInversion;

	public enum LevelStat {
		MAX_HP,
		MAX_ENTROPY,
		DEFENSE,
		REGENERATION_QUALITY,
		SPEED
	}
	public enum Stat {
		MAX_HP,
		MAX_ENTROPY,
		DEFENSE,
		REGENERATION_QUALITY,
		SPEED,
		INVERSION,
		ALL
	}

	// ETC

	private boolean isDead = false;
	private Box lastVelocityBox = null;
	private float maxStackSizeMulti;
	public enum KnockbackDirection {
		NONE,
		LEFT,
		RIGHT,
		UP,
		DOWN
	}

	public PlayerAttributes() {
			this.setSpeedModifier(1);
			this.setEnvironmentSpeedModifier(1);
			this.updateStat(Stat.ALL);
			this.updateXPtoLevelUp();
			this.setMaxStackSizeMulti(1);
	}

	// PLAYER SPEED

	public double getSpeed() {
		return playerSpeed;
	}

	public double getSpeedModifier() {
		return playerSpeedModifier;
	}

	public double getEnvironmentSpeedModifier() {
		return this.environmentSpeedModifier;
	}

	public void setEnvironmentSpeedModifier(double environmentSpeedModifier)
	{
		this.environmentSpeedModifier = Math.max(environmentSpeedModifier, 0.025);
		updateStat(Stat.SPEED);
	}

	public void setSpeedModifier(double playerSpeedModifier) {
		this.playerSpeedModifier = playerSpeedModifier;
		updateStat(Stat.SPEED);
	}

	public Box getLastVelocityBox() {
		return lastVelocityBox;
	}

	public void setLastVelocityBox(Box box) {
		this.lastVelocityBox = box;
	}

	// HP, DAMAGE AND HEALS

	public double getEntropy() {
		return entropy;
	}

	public double reduceEntropy(double entropy) {
		this.entropy -= Math.max(this.entropy - entropy, 0);
		return entropy;
	}

	public double addEntropy(double entropy) {
		this.entropy += calculateEntropyGain(entropy);
		return entropy;
	}

	public double calculateEntropyGain(double entropy) {
		return Math.min(this.entropy + entropy, this.maxEntropy) - this.entropy;
	}

	public double getMaxEntropy() {
		return maxEntropy;
	}

	public void fillEntropy() {
		this.entropy = this.maxEntropy;
	}

	// DEATH HANDLING

	public void playerDies() {
		this.isDead = true;

		fadeInThenOut(4000);
		// Schedule playerRespawns() to run after 4 seconds
		Timer timerToRespawn = new Timer();
		timerToRespawn.schedule(new TimerTask() {
			@Override
			public void run() {
				if (isDead) {
					playerRespawns();
				}
			}
		}, 4000);
	}

	private void playerRespawns() {
		player.pos.reloadSpawnpoint();
		player.setHP(player.getMaxHP());
		player.setLockedHP(0);
		this.isDead = false;
	}

	public boolean isDead() {
		return isDead;
	}

	public void playerRevived() {
		this.isDead = false;
		fadeOut();
	}

	// XP

	public double getXPtoLevelUp() {
		return XPtoLevelUp;
	}

	public void updateXPtoLevelUp() {
		this.XPtoLevelUp = Math.floor((4 + Math.pow(this.level, 2) / 250) * Math.pow(this.level, 2));
	}

	public void checkIfLevelUp() {
		if (this.xp >= getXPtoLevelUp()) {
			addXP(-getXPtoLevelUp());
			incrementLevel();
		}
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void incrementLevel() {
		this.level++;
		updateXPtoLevelUp();
		System.out.println("Leveled up! Now at level " + this.level + ".");
		if (level % 10 == 0) {
			addLevelUpPoints(5);
		} else if (level % 5 == 0) {
			addLevelUpPoints(3);
		} else {
			addLevelUpPoints(2);
		}
	}

	public double getXP() {
		return xp;
	}

	public void setXP(double xp) {
		this.xp = xp;
	}

	public void addXP(double xp) {
		this.xp += xp;
	}

	public void addLevelUpPoints(int levelUpPoints) {
		this.levelUpPoints += levelUpPoints;
	}

	public int getLevelUpPoints() {
		return levelUpPoints;
	}

	public void useLevelUpPoints(LevelUpPointsUsage levelUpPointsUsage, int levelUpPoints) {
		switch (levelUpPointsUsage) {
			case MAX_HP -> {
				setLevelStat(LevelStat.MAX_HP,levelMaxHP + levelUpPoints * 5 + 2 * Math.floor((double) levelUpPoints / 5));
			}
			case MAX_ENTROPY -> {
				setLevelStat(LevelStat.MAX_ENTROPY,levelMaxEntropy + levelUpPoints * 3 + Math.floor((double) levelUpPoints / 5));
			}
			case DEFENSE -> {
				setLevelStat(LevelStat.DEFENSE,levelDefense + levelUpPoints + Math.floor((double) levelUpPoints / 5));
			}
			case REGENERATION -> {
				setLevelStat(LevelStat.REGENERATION_QUALITY,levelRegenerationQuality + 0.5 * levelUpPoints + 0.5 * Math.floor((double) levelUpPoints / 5));
			}
			case SPEED -> {
				setLevelStat(LevelStat.SPEED,levelSpeed + 0.25 * levelUpPoints + 0.25 * Math.floor((double) levelUpPoints / 5));
			}
		}
	}

	public void setLevelStat(LevelStat levelStat, double amount) {
		switch (levelStat) {
			case MAX_HP -> {
				this.levelMaxHP = amount;
				updateStat(Stat.MAX_HP);
			}
			case MAX_ENTROPY -> {
				this.levelMaxEntropy = amount;
				updateStat(Stat.MAX_ENTROPY);
			}
			case DEFENSE -> {
				this.levelDefense = amount;
				updateStat(Stat.DEFENSE);
			}
			case REGENERATION_QUALITY -> {
				this.levelRegenerationQuality = amount;
				updateStat(Stat.REGENERATION_QUALITY);
			}
			case SPEED -> {
				this.levelSpeed = amount;
				updateStat(Stat.SPEED);
			}
		}
	}

	public double getLevelStat(LevelStat stat) {
		return switch (stat) {
			case MAX_HP -> this.levelMaxHP;
			case MAX_ENTROPY -> this.levelMaxEntropy;
			case DEFENSE -> this.levelDefense;
			case REGENERATION_QUALITY -> this.levelRegenerationQuality;
			case SPEED -> this.playerSpeed;
		};
	}

	// COINS

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public void addBalance(int balance) {
		this.balance += balance;
	}

	// COUNT LIMIT

	public float getMaxStackSizeMulti() {
		return maxStackSizeMulti;
	}

	public void setMaxStackSizeMulti(float maxStackSizeMulti) {
		this.maxStackSizeMulti = maxStackSizeMulti;
		try {
			updateMaxStackSize();
		} catch (NullPointerException e) {
			System.out.println("Max stack multiplier attribute failed because could not read 'inv' as player is null");
		}
	}

	// GENERIC ATTRIBUTES

	public void updateStat(Stat stat) {
		try {
			switch (stat) {
				case MAX_HP -> player.setMaxHP(100 + levelMaxHP + equipmentMaxHP);
				case MAX_ENTROPY -> this.maxEntropy = 80 + levelMaxEntropy + equipmentMaxEntropy;
				case DEFENSE -> player.setDefense(levelDefense + equipmentDefense);
				case REGENERATION_QUALITY ->
					player.setRegenerationQuality(1 + levelRegenerationQuality + equipmentRegenerationQuality);
				case SPEED ->
					this.playerSpeed = this.playerSpeedModifier * (scale * 2 + 0.166) / 3 * this.environmentSpeedModifier + Math.log10(1 + this.levelSpeed + this.equipmentSpeed);
				case INVERSION -> player.setInversion(this.equipmentInversion);
				case ALL -> {
					for (Stat argument : Stat.values()) {
						if (argument == Stat.ALL)
							continue;
						updateStat(argument);
					}
				}
			}
		} catch (NullPointerException e) {
			// This would mean player has not been initialized yet, so do nothing
		}
	}

	public void updateEquipmentStat(Stat stat) {
		switch (stat) {
			case MAX_HP -> {
				this.equipmentMaxHP = 0;
				for (int i = 0; i < 3; i++) {
					if (player.inv.getItem(i) != null) {
						this.equipmentMaxHP += player.inv.getItemStat(player.inv.getItem(i), vit);
					}
				}
            }
			case MAX_ENTROPY -> {
				this.equipmentMaxEntropy = 0;
				for (int i = 0; i < 3; i++) {
					if (player.inv.getItem(i) != null) {
						this.equipmentMaxEntropy += player.inv.getItemStat(player.inv.getItem(i), ent);
					}
				}
			}
			case DEFENSE -> {
				this.equipmentDefense = 0;
				for (int i = 0; i < 3; i++) {
					if (player.inv.getItem(i) != null) {
						this.equipmentDefense += player.inv.getItemStat(player.inv.getItem(i), def);
					}
				}
			}
			case INVERSION -> {
				this.equipmentInversion = 0;
				for (int i = 0; i < 3; i++) {
					if (player.inv.getItem(i) != null) {
						this.equipmentInversion += player.inv.getItemStat(player.inv.getItem(i), Inventory.PossibleItemStats.inversion);
					}
				}
			}
			case REGENERATION_QUALITY -> {}
			case SPEED -> {}
			case ALL -> {
				for (Stat argument : Stat.values()) {
					if (argument == Stat.ALL)
						continue;
					updateEquipmentStat(argument);
				}
			}
		}
		updateStat(stat);
	}

	// UNLOAD

	public void unloadAttributes() {
		this.setSpeedModifier(1);
		this.setEnvironmentSpeedModifier(1);
		this.updateStat(Stat.ALL);
		player.setHP(player.getMaxHP());
		this.fillEntropy();
		player.setRegenerationQuality(1);
		player.setRegenerationRate(1);
		this.updateXPtoLevelUp();
		this.setBalance(0);
		isDead = false;
		player.unlockHP(player.getLockedHP());
	}
}
