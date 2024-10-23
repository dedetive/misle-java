package com.ded.misle.player;

import com.ded.misle.boxes.Box;

import static com.ded.misle.GamePanel.tileSize;
import static com.ded.misle.Launcher.scale;

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
		if (heal <= 0 || (this.isDead && !reason.contains("revival"))) {
			return 0;
		}
		double healToReceive;
		switch (reason) {
			case "normal", "overheal", "absolute", "absolute overheal", "revival", "revival exclusive", "absolute revival", "absolute revival exclusive":
				healToReceive = calculateHeal(heal, reason);
				this.hp += healToReceive;
				break;
			default:
				throw (new IllegalArgumentException("Invalid reason: " + reason));
		}
		if (reason.contains("revival") && this.hp > 0) {
			this.isDead = false;
		}
		return healToReceive;
	}

	/**
	 *
	 * The documentation for the method {@link #receiveHeal(double, String)} is valid for this method too.
	 *
	 */
	public double calculateHeal(double heal, String reason) {
		if (heal <= 0 || (this.isDead && !reason.contains("revival"))) {
			return 0;
		}
		double theoreticalHeal = 0;
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
			case "revival" -> {             // REVIVAL: heals and may revive player
				theoreticalHeal = Math.min(heal, this.maxHP - this.hp);
				yield theoreticalHeal;
			}
			case "revival exclusive" -> {   // REVIVAL EXCLUSIVE: heals only if the player is dead, may revive player
				if (this.isDead) {
					theoreticalHeal = Math.min(heal, this.maxHP - this.hp);
				}
				yield theoreticalHeal;
			}
			case "absolute revival" -> {    // ABSOLUTE REVIVAL: heals exact amount and may revive player
				theoreticalHeal = Math.min(heal, this.maxHP - this.hp);
				yield theoreticalHeal;
			}
			case "absolute revival exclusive" -> {  // ABSOLUTE REVIVAL EXCLUSIVE: heals exact amount only if player is dead,
				if (this.isDead) {                  // may revive player
					theoreticalHeal = Math.min(heal, this.maxHP - this.hp);
				}
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
