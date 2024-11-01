package com.ded.misle.player;

import com.ded.misle.boxes.Box;

import java.util.Timer;
import java.util.TimerTask;

import static com.ded.misle.GamePanel.player;
import static com.ded.misle.GamePanel.tileSize;
import static com.ded.misle.Launcher.scale;

@SuppressWarnings("ConditionalExpressionWithIdenticalBranches")
public class PlayerAttributes {

	// BASE ATTRIBUTES

	private double playerSpeed;
	private double width;
	private double height;

	// STATS ATTRIBUTES

	private double hp;
	private double maxHP;
	private double lockedHP;
	private double defense;
	private double playerSpeedModifier;
	private double environmentSpeedModifier;
	private double regenerationQuality;
	private double regenerationRate;

	// XP

	private double xp;
	private double XPtoLevelUp;
	private int level = 1;

	// ETC

	private long lastRegenerationMillis;
	private long lastHitMillis;
	private boolean isDead = false;
	private Box lastVelocityBox = null;

	public PlayerAttributes() {
			this.setPlayerSpeedModifier(1);
			this.setPlayerEnvironmentSpeedModifier(1);
			this.setPlayerWidth(tileSize);
			this.setPlayerHeight(tileSize);
			this.setPlayerMaxHP(100);
			this.setPlayerRegenerationQuality(1);
			this.setPlayerRegenerationRate(1);
			this.setPlayerDefense(0);
			this.setPlayerHP(getPlayerMaxHP());
			this.updateXPtoLevelUp();
	}

	// PLAYER SPEED

	public void updateSpeed() {
		this.playerSpeed = this.playerSpeedModifier * (scale * 2 + 0.166) / 3 * this.environmentSpeedModifier;
	}

	public double getPlayerSpeed() {
		return playerSpeed;
	}

	public double getPlayerSpeedModifier() {
		return playerSpeedModifier;
	}

	public double getPlayerEnvironmentSpeedModifier() {
		return this.environmentSpeedModifier;
	}

	public void setPlayerEnvironmentSpeedModifier(double environmentSpeedModifier) {
		this.environmentSpeedModifier = Math.max(environmentSpeedModifier, 0.025);
		updateSpeed();
	}

	public void setPlayerSpeedModifier(double playerSpeedModifier) {
		this.playerSpeedModifier = playerSpeedModifier;
		updateSpeed();
	}

	public Box getLastVelocityBox() {
		return lastVelocityBox;
	}

	public void setLastVelocityBox(Box box) {
		this.lastVelocityBox = box;
	}

	// PLAYER SIZES

	public double getPlayerWidth() {
		return width;
	}

	public void setPlayerWidth(double playerWidth) {
		this.width = playerWidth;
	}

	public double getPlayerHeight() {
		return height;
	}

	public void setPlayerHeight(double playerHeight) {
		this.height = playerHeight;
	}

	// HP, DAMAGE AND HEALS

	public double getPlayerHP() {
		return hp;
	}

	public double setPlayerHP(double HP) {
		this.hp = HP;
		return HP;
	}

	public double getPlayerMaxHP() {
		return maxHP;
	}

	public double setPlayerMaxHP(double maxHP) {
		this.maxHP = maxHP;
		return maxHP;
	}

	public double getPlayerLockedHP() {
		return lockedHP;
	}

	public double setPlayerLockedHP(double lockedHP) {
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
			theoreticalDamage = Math.min(damage, this.hp);
		} else if (isPostMortem) {
			// Post-mortem: damage will be dealt past 0 into the negatives
			theoreticalDamage = defenseCalculation;
		} else {
			// Normal: defense and item effects take place normally
			theoreticalDamage = Math.min(defenseCalculation, this.hp);
		}

		return theoreticalDamage;
	}

	private void unlockHP(double damage) {
		this.setPlayerLockedHP(Math.max(lockedHP - damage, 0));
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
	 * if (reason.contains("revival exclusive")) {
					if (this.isDead) {
						healingExpression = Math.min(heal, this.maxHP - this.hp);
					} else {
						healingExpression = 0;
					}
				}@param heal the heal to be received
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
			setPlayerHP(getPlayerHP() + healToReceive);

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


	public double getPlayerDefense() {
		return defense;
	}

	public void setPlayerDefense(double defense) {
		this.defense = defense;
	}

	// REGENERATION

	public void updateRegenerationHP(long currentTime) {
		// Calculate the interval for healing 1 HP, based on the existing regeneration rate and quality
		double regenerationInterval = ((2500L / regenerationRate) / 10 / regenerationQuality);

		if (lastHitMillis + 2500 < currentTime && lastRegenerationMillis + regenerationInterval < currentTime && !this.isDead) {
			receiveHeal(1, "normal");
			lastRegenerationMillis = currentTime;
		}
	}


	public double getPlayerRegenerationQuality() {
		return regenerationQuality;
	}

	public void setPlayerRegenerationQuality(double regenerationQuality) {
		this.regenerationQuality = regenerationQuality;
	}

	public double getPlayerRegenerationRate() {
		return regenerationRate;
	}

	public void setPlayerRegenerationRate(double regenerationRate) {
		this.regenerationRate = regenerationRate;
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
		this.setPlayerHP(getPlayerMaxHP());
		this.setPlayerLockedHP(0);
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
			incrementPlayerLevel();
		}
	}

	public int getPlayerLevel() {
		return level;
	}

	public void incrementPlayerLevel() {
		this.level++;
		updateXPtoLevelUp();
		System.out.println("Leveled up! Now at level " + this.level + ".");
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

	public void unloadAttributes() {
		this.setPlayerSpeedModifier(1);
		this.setPlayerEnvironmentSpeedModifier(1);
		this.setPlayerWidth(tileSize);
		this.setPlayerHeight(tileSize);
		this.setPlayerMaxHP(100);
		this.setPlayerRegenerationQuality(1);
		this.setPlayerRegenerationRate(1);
		this.setPlayerDefense(0);
		this.setPlayerHP(getPlayerMaxHP());
		this.updateXPtoLevelUp();
		isDead = false;
		unlockHP(lockedHP);

	}
}
