package com.ded.misle.player;

import static com.ded.misle.GamePanel.tileSize;
import static com.ded.misle.Launcher.scale;

public class PlayerAttributes {

	private double playerSpeed;
	private double playerSpeedModifier;
	private double width;
	private double height;
	private double HP;
	private double maxHP;


	public PlayerAttributes() {
			this.setPlayerSpeedModifier(1);
			this.setPlayerWidth(tileSize);
			this.setPlayerHeight(tileSize);
			this.setPlayerMaxHP(100);
			this.setPlayerHP(getPlayerMaxHP());
	}


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

	public double getPlayerHP() {
		return HP;
	}

	public void setPlayerHP(double HP) {
		this.HP = HP;
	}

	public double getPlayerMaxHP() {
		return maxHP;
	}

	public void setPlayerMaxHP(double maxHP) {
		this.maxHP = maxHP;
	}

	public double takeDamage(double damage, String reason) {
		setPlayerHP(Math.min(Math.max(getPlayerHP() - damage, 0), getPlayerMaxHP()));
		return damage;
	}

	public double calculateDamage(double damage, String reason) {
		return 0.0;
	}
}
