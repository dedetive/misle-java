package com.ded.misle.world.entities.enemies;

import com.ded.misle.world.data.CoinDropRange;
import com.ded.misle.world.entities.Entity;
import com.ded.misle.world.entities.ai.AIBehavior;
import com.ded.misle.world.entities.ai.BehaviorController;
import com.ded.misle.world.logic.TurnTimer;
import com.ded.misle.world.logic.effects.Damage;

import java.awt.*;

import static com.ded.misle.game.GamePanel.player;

/**
 * Represents an enemy entity in the game world.
 * Enemies have behaviors, experience and coin drops, and belong to a specific {@link EnemyType}.
 */
public class Enemy extends Entity {

    /** The type of this enemy, which defines its default configuration. */
    private final EnemyType type;

    /** A scaling factor that magnifies the enemy's attributes (e.g. HP, damage). */
    private final double magnification;

    /** The amount of XP awarded to the player when this enemy is defeated. */
    private double xpDrop;

    /** The range of coins that may drop from this enemy. */
    private CoinDropRange coinDrop;

    /** Controls the AI behaviors of this enemy. */
    private final BehaviorController controller = new BehaviorController(this);

    private int turnsToRespawn = 0;

    private TurnTimer respawnTimer;

    /**
     * Constructs a new {@code Enemy} at the given position with a specific type and magnification.
     *
     * @param pos           the position to spawn the enemy
     * @param type          the type of enemy
     * @param magnification a multiplier for stats like HP and damage
     */
    public Enemy(Point pos, EnemyType type, double magnification) {
        super(pos.x, pos.y);
        this.magnification = magnification + player.getDifficulty().enemyStatMultiplier;
        this.type = type;
        this.load();
        EnemyRegistry.register(this);
    }

    /**
     * Applies the configuration from this enemy's {@link EnemyType}.
     */
    private void load() {
        type.applyTo(this);
    }

    /**
     * Kills the enemy and removes it from the registry.
     */
    public void kill() {
        EnemyRegistry.unregister(this);
    }

    // GETTERS & SETTERS

    /**
     * Returns the magnification factor used to scale the enemy's stats.
     *
     * @return the magnification multiplier
     */
    public double getMagnification() {
        return this.magnification;
    }

    /**
     * Returns the {@link EnemyType} of this enemy.
     *
     * @return the type of this enemy
     */
    public EnemyType getEnemyType() {
        return type;
    }

    /**
     * Returns the amount of XP this enemy drops when defeated.
     *
     * @return the XP drop value
     */
    public double getXPDrop() {
        return xpDrop;
    }

    /**
     * Sets the amount of XP this enemy will drop.
     *
     * @param xp the XP amount
     */
    public void setXpDrop(int xp) {
        this.xpDrop = xp;
    }

    /**
     * Sets the coin drop range using minimum and maximum values.
     *
     * @param min the minimum number of coins
     * @param max the maximum number of coins
     */
    public void setCoinDropRange(int min, int max) {
        this.coinDrop = new CoinDropRange(min, max);
    }

    /**
     * Sets the coin drop range using a {@link CoinDropRange} object.
     *
     * @param coinDropRange the coin drop range
     */
    public void setCoinDropRange(CoinDropRange coinDropRange) {
        this.coinDrop = coinDropRange;
    }

    /**
     * Sets a fixed coin drop amount (min = max = amount).
     *
     * @param coinDrop the fixed number of coins
     */
    public void setCoinDrop(int coinDrop) {
        this.coinDrop = new CoinDropRange(coinDrop);
    }

    /**
     * Returns a randomly rolled coin drop based on the drop range.
     *
     * @return the coin drop amount
     */
    public int getCoinDrop() {
        return coinDrop.roll();
    }

    /**
     * Sets the AI behaviors for this enemy.
     *
     * @param behaviors the list of behaviors
     */
    public void setBehaviors(AIBehavior... behaviors) {
        controller.setBehaviors(behaviors);
    }

    /**
     * Returns the list of AI behaviors assigned to this enemy.
     *
     * @return the array of behaviors
     */
    public AIBehavior[] getBehaviors() {
        return controller.getBehaviors();
    }

    /**
     * Returns the {@link BehaviorController} managing this enemy's behavior.
     *
     * @return the behavior controller
     */
    public BehaviorController getController() {
        return controller;
    }

    /**
     * Sets the maximum HP.
     * <p>Is multiplied by this Enemy's {@link #magnification}.</p>
     * @param maxHP New maximum HP.
     */
    @Override
    public void setMaxHP(double maxHP) {
        super.setMaxHP(maxHP * magnification);
    }

    /**
     * Sets damage.
     * <p>Is multiplied by this Enemy's {@link #magnification}.</p>
     * @param damage Base damage dealt per hit
     * @param damageRate How long it takes per hit in turns
     */
    public void setDamage(double damage, int damageRate) {
        this.effect = new Damage(damage * magnification, damageRate).setTriggersOnContact(false);
    }

    /**
     * Sets damage, with damage rate set to 1.
     * <p>Is multiplied by this Enemy's {@link #magnification}.
     *
     * @param damage Base damage dealt per hit
     */
    public void setDamage(double damage) {
        setDamage(damage, 1);
    }

    public void setTurnsToRespawn(int turnsToRespawn) {
        this.turnsToRespawn = turnsToRespawn;
        respawnTimer = new TurnTimer(turnsToRespawn + 1, e -> respawnIfPossible());
        respawnTimer.setRoomScoped(true);
    }

    public boolean canRespawn() {
        return player.loadTimerFromUUID(this.getId()) <= 0;
    }

    public void respawnIfPossible() {
        if (canRespawn()) {
            respawn();
            respawnTimer.reset();
        }
    }

    @Override
    public boolean checkIfDead() {
        boolean result = super.checkIfDead();

        if (result && turnsToRespawn > 0) {
            player.storeTimerInUUID(this.getId(), turnsToRespawn);
            respawnTimer.start();
        } else if (turnsToRespawn <= 0) {
            respawn();
        }

        return result;
    }

    private void respawn() {
        new Enemy(this.getOrigin(), type, this.magnification);
    }

    public void scheduleRespawn() {
        int turns = player.loadTimerFromUUID(this.getId());
        respawnTimer = new TurnTimer(turns, e -> respawnIfPossible());
        respawnTimer.setRoomScoped(true);
        respawnTimer.start();
    }
}