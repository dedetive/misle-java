package com.ded.misle.player;

import com.ded.misle.boxes.Box;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;

import static com.ded.misle.GamePanel.player;
import static com.ded.misle.player.Inventory.PossibleItemStats.*;
import static com.ded.misle.renderer.PlayingRenderer.createFloatingText;
import static com.ded.misle.renderer.PlayingRenderer.showHealthBar;
import static com.ded.misle.Launcher.scale;
import static com.ded.misle.items.Item.updateMaxStackSize;

@SuppressWarnings("ConditionalExpressionWithIdenticalBranches")
public class PlayerAttributes {

	// BASE ATTRIBUTES

	private double playerSpeed;

	// STATS ATTRIBUTES

	private double hp;
	private double maxHP;
	private double lockedHP;
	private double defense;
	private double entropy;
	private double maxEntropy;
	private double playerSpeedModifier;
	private double environmentSpeedModifier;
	private double regenerationQuality;
	private double regenerationRate = 1;
	private boolean isInvulnerable;
	private boolean isRegenerationDoubled;

	// XP

	private double xp;
	private double XPtoLevelUp;
	private int level = 1;
	private int levelUpPoints;
	public static enum LevelUpPointsUsage {
		MAX_HP,
		MAX_ENTROPY,
		DEFENSE,
		REGENERATION,
		SPEED
	}

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
		ALL
	}

	// ETC

	private long lastRegenerationMillis;
	private long lastHitMillis;
	private boolean isDead = false;
	private Box lastVelocityBox = null;
	private float maxStackSizeMulti;

	public PlayerAttributes() {
			this.setSpeedModifier(1);
			this.setEnvironmentSpeedModifier(1);
			this.updateStat(Stat.ALL);
			this.setHP(getMaxHP());
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

	public boolean getIsInvulnerable() { return isInvulnerable; }

	public void setIsInvulnerable(boolean isInvulnerable) { this.isInvulnerable = isInvulnerable; }

	public double getHP() {
		return hp;
	}

	public double setHP(double HP) {
		this.hp = HP;
		return HP;
	}

	public double getMaxHP() {
		return maxHP;
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

	public double getLockedHP() {
		return lockedHP;
	}

	public double setLockedHP(double lockedHP) {
		this.lockedHP = lockedHP;
		return lockedHP;
	}

	/**
	 * REASONS: <br>
	 * - "normal": defense and item effects take place normally. <br><br>
	 * - "post-mortem": the damage will be dealt even if the player dies. Defense and item effects take place normally. May result in negative values. <br><br>
	 * - "absolute post-mortem": the damage will be dealt even if the player dies. The damage will be dealt no matter what. May result in negative values. <br><br>
	 * - "absolute": the damage will be dealt no matter what, unless player dies. <br><br>
	 * - "locker": no damage is actually done. instead, a portion of the HP is locked and is temporarily not considered. Takes args[0] as how many milliseconds it takes for the HP to be unlocked. <br><br>
	 *
	 * @param damage the damage to be dealt
	 * @param reason the kind of damage that's taking place; see above for a list
	 * @return Final damage dealt
	 */
	public double takeDamage(double damage, String reason, String[] args) {
		isRegenerationDoubled = false;

		// Early exit for invalid damage
		if (damage <= 0) {
			return 0;
		}

		double damageToReceive;

		// Define boolean flags for different conditions
		boolean isNormalOrAbsolute = reason.contains("normal") || reason.contains("absolute");
		boolean isPostMortem = reason.contains("post-mortem");
		boolean isLocker = reason.contains("locker");

		// Calculate damage based on the reason
		if (isLocker) {
			damageToReceive = calculateDamage(damage, reason);
			lockedHP += damageToReceive;
			// Schedule unlockHP() to run after a few seconds, based on args[0] in milliseconds
			Timer timerToUnlock = new Timer();
			timerToUnlock.schedule(new TimerTask() {
				@Override
				public void run() {
					unlockHP(damage);
				}
			}, Integer.parseInt(args[0]));

		} else if (isNormalOrAbsolute) {
			damageToReceive = calculateDamage(damage, reason);
			this.hp = Math.max(this.hp - damageToReceive, 0); // Ensure HP doesn't go below 0 for non post mortem

		} else if (isPostMortem) {
			damageToReceive = calculateDamage(damage, reason);
			this.hp -= damageToReceive; // Apply damage without floor so it can go below 0

		} else {
			throw new IllegalArgumentException("Invalid reason: " + reason); // Handle invalid reasons
		}

		if (damageToReceive > 0) {
			lastHitMillis = System.currentTimeMillis();
		}

		// Check if the player dies
		if (this.hp <= 0 || lockedHP > this.hp) {
			playerDies();
		}

		// Displayed numerical value5

		int playerScreenX = (int) ((player.getX() - player.pos.getCameraOffsetX()) / scale);
		int playerScreenY = (int) ((player.getY() - player.pos.getCameraOffsetY()) / scale);
		int randomPosX = (int) ((Math.random() * (40 + 40)) - 40);
		int randomPosY = (int) ((Math.random() * (25 + 25)) - 25);
		DecimalFormat df = new DecimalFormat("#.##");
		String formattedHealAmount = df.format(damageToReceive);
		createFloatingText("-" + formattedHealAmount, Color.decode("#DE4040"), playerScreenX + randomPosX, playerScreenY + randomPosY, true);

		return damageToReceive;
	}

	/**
	 *
	 * The documentation for the method {@link #takeDamage(double, String, String[])} is valid for this method too.
	 *
	 */
	public double calculateDamage(double damage, String reason) {
		// Early exit for invalid damage
		if (damage <= 0) {
			return 0;
		}

		double defenseCalculation = damage - ((damage * this.defense) / (this.defense + 100)) - this.defense / 2;
		double theoreticalDamage;

		// Define boolean flags for different conditions
		boolean isAbsolute = reason.contains("absolute");
		boolean isPostMortem = reason.contains("post-mortem");

		// Calculate damage based on the reason
		if (isAbsolute && isPostMortem) {
			// Absolute post-mortem: damage will be dealt past 0 and defense effects are ignored
			theoreticalDamage = damage; // Return full damage
		} else if (isAbsolute) {
			// Absolute: defense effects are ignored
			theoreticalDamage = Math.max(Math.min(damage, this.hp), 0);
		} else if (isPostMortem && !getIsInvulnerable()) {
			// Post-mortem: damage will be dealt past 0 into the negatives
			theoreticalDamage = defenseCalculation;
		} else if (!getIsInvulnerable()) {
			// Normal: defense and item effects take place normally
			theoreticalDamage = Math.max(Math.min(defenseCalculation, this.hp), 0);
		} else {
			theoreticalDamage = 0;
		}

		return theoreticalDamage;
	}

	private void unlockHP(double damage) {
		this.setLockedHP(Math.max(lockedHP - damage, 0));
	}

	/**
	 * REASONS: <br>
	 * - "normal": the heal can be affected by external forces, but is limited by max HP <br> <br>
	 * - "absolute": the heal will be received no matter what, unless max HP is hit <br> <br>
	 * - "overheal": the heal can be affected by external forces, but is NOT limited by max HP <br> <br>
	 * - "absolute overheal": the heal will be received no matter what, and is NOT limited by max HP <br> <br>
	 * - "revival": the heal will be received regardless of whether the player is dead. This can revive the player. The value entered may be affected by external forces <br><br>
	 * - "absolute revival": the heal will be received regardless of whether the player is dead. This can revive the player. The value entered will not be affected by external forces <br><br>
	 * - "revival exclusive": the heal will only be received if the player is dead. This will revive the player. The value entered may be affected by external forces <br><br>
	 * - "absolute revival exclusive": the heal will only be received if the player is dead. This will revive the player. The value entered will not be affected by external forces
	 *
	 * @param heal the heal to be received
	 * @param reason the kind of heal that's taking place; see above for a list
	 * @return Final heal received
	 */
	public double receiveHeal(double heal, String reason) {
		// Early exit for invalid heal or if the character is dead without revival
		if (heal <= 0 || (this.isDead && (!reason.contains("revival"))) || (this.isDead && lockedHP > this.hp && this.hp >= 0)) {
			return 0;
		}

		double healToReceive;

		// Define boolean flags for different conditions
		boolean isValidReason = reason.contains("normal") || reason.contains("overheal") ||
				reason.contains("revival") || reason.contains("revival exclusive") ||
				reason.contains("absolute");

		// Check for valid healing reasons
		if (isValidReason) {
			healToReceive = calculateHeal(heal, reason);
			setHP(getHP() + healToReceive);

			// Check for revival condition
			if (reason.contains("revival") && this.hp > 0) {
				this.isDead = false; // Set isDead to false if revived
			}
		} else {
			throw new IllegalArgumentException("Invalid reason: " + reason); // Handle invalid reasons
		}

		if (this.hp > lockedHP) {
			this.isDead = false;        // Set isDead to false if new HP is higher than locked HP
		}

		return healToReceive;
	}


	/**
	 *
	 * The documentation for the method {@link #receiveHeal(double, String)} is valid for this method too.
	 *
	 */
	public double calculateHeal(double heal, String reason) {
		// Invalid heal or dead character without revival
		if (heal <= 0 || (this.isDead && !reason.contains("revival"))) {
			return 0;
		}

		double healingExpression = 0;
		boolean isAbsolute = reason.contains("absolute");
		boolean isOverheal = reason.contains("overheal");
		boolean isRevivalExclusive = reason.contains("revival exclusive");
		boolean isRevival = reason.contains("revival");
		boolean isNormal = reason.contains("normal");

		if (isRevivalExclusive) {
			if (!this.isDead) {
				return 0; // Revival exclusive healing only works if the character is dead
			}
			// Overheal logic for revival exclusive
			if (isOverheal) {
				healingExpression = isAbsolute ?
						heal :
						heal;
			} else {
				healingExpression = isAbsolute ?
						Math.min(heal, this.maxHP - this.hp) :
						Math.min(heal, this.maxHP - this.hp);
			}
		} else if (isNormal || isRevival) {
			// Normal or revival healing logic
			healingExpression = isAbsolute ?
					Math.min(heal, this.maxHP - this.hp) :
					Math.min(heal, this.maxHP - this.hp);
		}

		// Overheal logic
		if (isOverheal && !isRevivalExclusive) {
			healingExpression = isAbsolute ?
					heal :
					heal;
		}

		return healingExpression;
	}


	public double getDefense() {
		return defense;
	}

	// REGENERATION

	public void updateRegenerationHP(long currentTime) {
		// Calculate the interval for healing 1 HP, based on the existing regeneration rate and quality
		double regenerationInterval = (250L / regenerationRate);

		if (lastHitMillis + 2500 < currentTime && lastRegenerationMillis + regenerationInterval < currentTime && !isDead && hp < maxHP) {
			receiveHeal(Math.max(getRegenerationQuality(), 1), "normal");
			if (isRegenerationDoubled) receiveHeal(Math.max(getRegenerationQuality(), 1), "normal");
			lastRegenerationMillis = currentTime;
			showHealthBar = true;
		} else {
			showHealthBar = !(hp >= maxHP) || lastRegenerationMillis + 5000 >= currentTime || isDead;
			if (hp >= maxHP) {
				isRegenerationDoubled = false;
			}
		}
	}

	public long getLastRegenerationMillis() {
		return lastRegenerationMillis;
	}

	public double getRegenerationQuality() {
		return regenerationQuality;
	}

	public void setRegenerationQuality(double regenerationQuality) {
		this.regenerationQuality = regenerationQuality;
	}

	public double getRegenerationRate() {
		return regenerationRate;
	}

	public void setRegenerationRate(double regenerationRate) {
		this.regenerationRate = regenerationRate;
	}

	public void turnRegenerationDoubledOn() {
		isRegenerationDoubled = true;
	}

	// DEATH HANDLING

	public boolean isDead() {
		return isDead;
	}

	public void playerDies() {
		this.isDead = true;

		// Schedule playerRespawns() to run after 4 seconds (4000 milliseconds)
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
		this.setHP(getMaxHP());
		this.setLockedHP(0);
		this.isDead = false;
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

	// END XP

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
		switch (stat) {
			case MAX_HP -> this.maxHP = 100 + levelMaxHP + equipmentMaxHP;
			case MAX_ENTROPY -> this.maxEntropy = 80 + levelMaxEntropy + equipmentMaxEntropy;
			case DEFENSE -> this.defense = levelDefense + equipmentDefense;
			case REGENERATION_QUALITY -> this.regenerationQuality = 1 + levelRegenerationQuality + equipmentRegenerationQuality;
			case SPEED -> this.playerSpeed = this.playerSpeedModifier * (scale * 2 + 0.166) / 3 * this.environmentSpeedModifier + Math.log10(1 + this.levelSpeed + this.equipmentSpeed);
			case ALL -> {
				for (Stat argument : Stat.values()) {
					if (argument == Stat.ALL)
						continue;
					updateStat(argument);
				}
			}
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
		this.setHP(getMaxHP());
		this.fillEntropy();
		this.setRegenerationQuality(1);
		this.setRegenerationRate(1);
		this.updateXPtoLevelUp();
		isDead = false;
		unlockHP(lockedHP);
	}
}
