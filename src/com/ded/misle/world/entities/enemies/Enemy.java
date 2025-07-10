package com.ded.misle.world.entities.enemies;

import com.ded.misle.world.data.entity.configurations.EnemyType;
import com.ded.misle.world.entities.Entity;
import com.ded.misle.world.logic.effects.Damage;

import java.awt.*;

import static com.ded.misle.game.GamePanel.player;

/**
 * Represents an enemy entity in the game world.
 * Enemies have behaviors, experience and coin drops, and belong to a specific {@link EnemyType}.
 */
public class Enemy extends Entity<Enemy> {

    /**
     * Constructs a new {@code Enemy} at the given position with a specific type and magnification.
     *
     * @param pos           the position to spawn the enemy
     * @param type          the type of enemy
     * @param magnification a multiplier for stats like HP and damage
     */
    public Enemy(Point pos, EnemyType type, double magnification) {
        super(pos.x, pos.y, type,magnification + player.getDifficulty().enemyStatMultiplier);
        EnemyRegistry.register(this);
    }

    // GETTERS & SETTERS

    /**
     * Sets damage.
     * <p>Is multiplied by this Enemy's {@link #magnification}.</p>
     * @param damage Base damage dealt per hit
     * @param damageRate How long it takes per hit in turns
     */
    public void setDamage(double damage, int damageRate) {
        this.effect = new Damage(damage * getMagnification(), damageRate).setTriggersOnContact(false);
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

    /**
     * Kills the enemy and removes it from the registry.
     */
    public void kill() {
        EnemyRegistry.unregister(this);
    }
}