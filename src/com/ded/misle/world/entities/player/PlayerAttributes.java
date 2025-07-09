package com.ded.misle.world.entities.player;

import com.ded.misle.audio.AudioFile;
import com.ded.misle.renderer.smoother.SyncedValue;

import java.util.Timer;
import java.util.TimerTask;

import static com.ded.misle.audio.AudioPlayer.playThis;
import static com.ded.misle.game.GamePanel.player;
import static com.ded.misle.renderer.MainRenderer.fader;
import static com.ded.misle.world.data.WorldLoader.unloadBoxes;
import static com.ded.misle.world.entities.player.Inventory.PossibleItemStats.*;
import static com.ded.misle.items.Item.updateInventoryMaxStackSize;

public class PlayerAttributes {

	// STATS ATTRIBUTES

	private double maxEntropy;
	private double entropy;
	private double strength;

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
		STRENGTH
	}

	// COINS

	private SyncedValue balance = new SyncedValue(0);

	// LEVEL ATTRIBUTES

	double levelMaxHP;
	double levelMaxEntropy;
	double levelDefense;
	double levelRegenerationQuality;
	double levelStrength;

	// EQUIPMENT ATTRIBUTES

	double equipmentMaxHP;
	double equipmentMaxEntropy;
	double equipmentDefense;
	double equipmentRegenerationQuality;
	double equipmentInversion;
	double equipmentStrength;

	public enum LevelStat {
		MAX_HP,
		MAX_ENTROPY,
		DEFENSE,
		REGENERATION_QUALITY,
		STRENGTH
	}
	public enum Stat {
		MAX_HP,
		MAX_ENTROPY,
		DEFENSE,
		REGENERATION_QUALITY,
		INVERSION,
		STRENGTH,
		ALL
	}

	// ETC

	private boolean isDead = false;
	private float maxStackSizeMulti;

	public PlayerAttributes() {
			this.updateStat(Stat.ALL);
			this.updateXPtoLevelUp();
			this.setMaxStackSizeMulti(1);
	}

	// HP, DAMAGE AND HEALS

	public double getStrength() {
		return this.strength;
	}

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

		playThis(AudioFile.player_death_jingle);
		fader.fadeInThenOut(4000);
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
		unloadBoxes();
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
		fader.fadeOut();
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
		updateXPtoLevelUp();
	}

	public void incrementLevel() {
		this.level++;
		updateXPtoLevelUp();
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
			case STRENGTH -> {
				this.levelStrength = amount;
				updateStat(Stat.STRENGTH);
			}
		}
	}

	public double getLevelStat(LevelStat stat) {
		return switch (stat) {
			case MAX_HP -> this.levelMaxHP;
			case MAX_ENTROPY -> this.levelMaxEntropy;
			case DEFENSE -> this.levelDefense;
			case REGENERATION_QUALITY -> this.levelRegenerationQuality;
			case STRENGTH -> this.levelStrength;
		};
	}

	// COINS

	public int getVisualBalance() {
		return balance.getVisualInt();
	}

	public int getRealBalance() {
		return (int) balance.getReal();
	}

	public void updateBalance() {
		balance.update(13f);
	}

	public void setBalance(int balance) {
		this.balance = new SyncedValue(balance);
	}

	public void addBalance(int balance) {
		this.balance.set(this.balance.getReal() + balance);
	}

	// COUNT LIMIT

	public float getMaxStackSizeMulti() {
		return maxStackSizeMulti;
	}

	public void setMaxStackSizeMulti(float maxStackSizeMulti) {
		this.maxStackSizeMulti = maxStackSizeMulti;
		try {
			updateInventoryMaxStackSize();
		} catch (NullPointerException e) {
			System.err.println("Max stack multiplier attribute failed.");
			e.printStackTrace();
		}
	}

	// GENERIC ATTRIBUTES
	final int startingHP = 20;
	final int startingEntropy = 5;
	final int startingRegenerationQuality = 1;

	public void updateStat(Stat stat) {
		try {
			switch (stat) {
				case MAX_HP -> {
					player.setMaxHP(startingHP + levelMaxHP + equipmentMaxHP);
					player.setHP(Math.min(player.getHP(), player.getMaxHP()));
				}
				case MAX_ENTROPY -> this.maxEntropy = startingEntropy + levelMaxEntropy + equipmentMaxEntropy;
				case DEFENSE -> player.setDefense(levelDefense + equipmentDefense);
				case REGENERATION_QUALITY ->
					player.setRegenerationQuality(startingRegenerationQuality + levelRegenerationQuality + equipmentRegenerationQuality);
				case INVERSION -> player.setInversion(this.equipmentInversion);
				case STRENGTH -> this.strength = this.equipmentStrength + this.levelStrength;
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
			case STRENGTH -> {
				this.equipmentStrength = 0;
				for (int i = 0; i < 3; i++) {
					if (player.inv.getItem(i) != null) {
						this.equipmentStrength += player.inv.getItemStat(player.inv.getItem(i), str);
					}
				}
			}
			case REGENERATION_QUALITY -> {}
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
