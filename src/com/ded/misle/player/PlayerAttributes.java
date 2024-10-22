package com.ded.misle.player;

import static com.ded.misle.GamePanel.tileSize;
import static com.ded.misle.Launcher.scale;

public class PlayerAttributes {

	private double playerSpeed;
	private double playerSpeedModifier;
	private double width;
	private double height;
	private double hp;
	private double maxHP;
	private double defense;
	private double xp;
	private double XPtoLevelUp;
	private int level = 1;

	public PlayerAttributes() {
			this.setPlayerSpeedModifier(1);
			this.setPlayerWidth(tileSize);
			this.setPlayerHeight(tileSize);
			this.setPlayerMaxHP(100);
			this.setPlayerHP(getPlayerMaxHP());
			this.updateXPtoLevelUp();
	}

	// PLAYER SPEED

	public double getPlayerSpeed() {
		return playerSpeed;
	}

	public double getPlayerSpeedModifier() {
		return playerSpeedModifier;
	}

	public void setPlayerSpeedModifier(double playerSpeedModifier) {
		this.playerSpeedModifier = playerSpeedModifier;
		this.playerSpeed = playerSpeedModifier * (scale * 2 + 0.166) / 3;
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
		double damageToReceive;
		switch (reason) {
			case "normal", "absolute":
				damageToReceive = calculateDamage(damage, reason);
				this.hp = (Math.max(this.hp - damageToReceive, 0));
				break;

			case "post-mortem", "absolute post-mortem":
				damageToReceive = calculateDamage(damage, reason);
				this.hp -= damageToReceive;
				break;
			default:
				throw (new IllegalArgumentException("Invalid reason: " + reason));
		}
		return damageToReceive;
	}

	/**
	 *
	 * The documentation for the method {@link #takeDamage(double, String)} is valid for this method too.
	 *
	 */
	public double calculateDamage(double damage, String reason) {
		double theoreticalDamage;
		double defenseCalculation = damage - ((damage * this.defense) / (this.defense + 100)) - this.defense / 2;
		return switch (reason) {
			case "normal" -> {              // NORMAL: defense and item effects take place normally
				theoreticalDamage = Math.min(defenseCalculation, this.hp);
				yield theoreticalDamage;
			}
			case "absolute" ->              // ABSOLUTE: defense and item effects are ignored
					Math.min(damage, this.hp);
			case "post-mortem" -> {         // POST-MORTEM: damage will be dealt past 0 into the negatives
				theoreticalDamage = defenseCalculation;
				yield theoreticalDamage;
			}
			case "absolute post-mortem" ->  // ABSOLUTE POST-MORTEM: damage will be dealt past 0 and
					damage;                 // defense and item effects are ignored
			default -> throw (new IllegalArgumentException("Invalid reason: " + reason));
		};
	}

	/**
	 * REASONS: <br>
	 * - "normal": the heal can be affected by external forces, but is limited by max HP <br> <br>
	 * - "overheal": the heal can be affected by external forces, but is NOT limited by max HP <br> <br>
	 * - "absolute overheal": the heal will be received no matter what, and is NOT limited by max HP <br> <br>
	 * - "absolute": the heal will be received no matter what, unless max HP is hit <br> <br>
	 *
	 * @param heal the heal to be received
	 * @param reason the kind of heal that's taking place; see above for a list
	 * @return Final heal received
	 */
	public double receiveHeal(double heal, String reason) {
		if (heal <= 0) {
			return 0;
		}
		double healToReceive;
		switch (reason) {
			case "normal", "overheal", "absolute", "absolute overheal":
				healToReceive = calculateHeal(heal, reason);
				this.hp += healToReceive;
				break;
			default:
				throw (new IllegalArgumentException("Invalid reason: " + reason));
		}
		return healToReceive;
	}

	/**
	 *
	 * The documentation for the method {@link #receiveHeal(double, String)} is valid for this method too.
	 *
	 */
	public double calculateHeal(double heal, String reason) {
		if (heal <= 0) {
			return 0;
		}
		double theoreticalHeal;
		return switch (reason) {
			case "normal" -> {              // NORMAL: heals as per usual, possibly being diminished by other effects
				theoreticalHeal = Math.min(heal, this.maxHP - this.hp);
				yield theoreticalHeal;
			}
			case "overheal" -> {            // OVERHEAL: ignores max HP and heals over that. Might be diminished by other effects
				theoreticalHeal = heal;
				yield theoreticalHeal;
			}
			case "absolute" -> {            // ABSOLUTE: heals no matter the circumstance, although doesn't overheal
				theoreticalHeal = Math.min(heal, this.maxHP - this.hp);
				yield theoreticalHeal;
			}
			case "absolute overheal" -> {   // ABSOLUTE OVERHEAL: ignores max HP and heals over that under any circumstance
				theoreticalHeal = heal;
				yield theoreticalHeal;
			}
			default -> throw (new IllegalArgumentException("Invalid reason: " + reason));
		};
	}

	public double getPlayerDefense() {
		return defense;
	}

	public void setPlayerDefense(double defense) {
		this.defense = defense;
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
