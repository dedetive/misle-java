package com.ded.misle.player;

import com.ded.misle.boxes.Box;

import static com.ded.misle.GamePanel.tileSize;
import static com.ded.misle.Launcher.scale;

@SuppressWarnings("ConditionalExpressionWithIdenticalBranches")
public class PlayerAttributes {

	private double playerSpeed;
	private double playerSpeedModifier;
	private double environmentSpeedModifier;
	private double width;
	private double height;
	private double hp;
	private double maxHP;
	private double defense;
	private double xp;
	private double XPtoLevelUp;
	private int level = 1;
	private boolean isDead = false;
	private Box lastVelocityBox = null;

	public PlayerAttributes() {
			this.setPlayerSpeedModifier(1);
			this.setPlayerEnvironmentSpeedModifier(1);
			this.setPlayerWidth(tileSize);
			this.setPlayerHeight(tileSize);
			this.setPlayerMaxHP(100);
			this.setPlayerDefense(4);
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

	public void setPlayerHP(double HP) {
		this.hp = HP;
	}

	public double getPlayerMaxHP() {
		return maxHP;
	}

	public void setPlayerMaxHP(double maxHP) {
		this.maxHP = maxHP;
	}

	/**
	 * REASONS: <br>
	 * - "normal": defense and item effects take place normally. <br><br>
	 * - "post-mortem": the damage will be dealt even if the player dies. Defense and item effects take place normally. May result in negative values. <br><br>
	 * - "absolute post-mortem": the damage will be dealt even if the player dies. The damage will be dealt no matter what. May result in negative values. <br><br>
	 * - "absolute": the damage will be dealt no matter what, unless player dies. <br><br>
	 *
	 * @param damage the damage to be dealt
	 * @param reason the kind of damage that's taking place; see above for a list
	 * @return Final damage dealt
	 */
	public double takeDamage(double damage, String reason) {
		// Early exit for invalid damage
		if (damage <= 0) {
			return 0;
		}

		double damageToReceive;

		// Define boolean flags for different conditions
		boolean isNormalOrAbsolute = reason.equals("normal") || reason.equals("absolute");
		boolean isPostMortem = reason.equals("post-mortem") || reason.equals("absolute post-mortem");

		// Calculate damage based on the reason
		if (isNormalOrAbsolute) {
			damageToReceive = calculateDamage(damage, reason);
			this.hp = Math.max(this.hp - damageToReceive, 0); // Ensure HP doesn't go below 0
		} else if (isPostMortem) {
			damageToReceive = calculateDamage(damage, reason);
			this.hp -= damageToReceive; // Apply damage without floor
		} else {
			throw new IllegalArgumentException("Invalid reason: " + reason); // Handle invalid reasons
		}

		// Check if the player dies
		if (this.hp <= 0) {
			playerDies();
		}

		return damageToReceive;
	}


	/**
	 *
	 * The documentation for the method {@link #takeDamage(double, String)} is valid for this method too.
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
			return damage; // Return full damage
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
		if (heal <= 0 || (this.isDead && !reason.contains("revival"))) {
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

	// DEATH HANDLING

	public boolean isDead() {
		return isDead;
	}

	public void playerDies() {
		this.isDead = true;
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
}
