package com.ded.misle.world.data;

import com.ded.misle.items.DropTable;
import com.ded.misle.world.entities.ai.behaviors.ChainBehavior;
import com.ded.misle.world.entities.ai.behaviors.PursueBehavior;
import com.ded.misle.world.entities.ai.behaviors.WaitBehavior;
import com.ded.misle.world.entities.ai.behaviors.WanderBehavior;
import com.ded.misle.world.entities.enemies.EnemyConfigurator;
import com.ded.misle.world.entities.enemies.EnemyType;

import java.awt.*;

import static com.ded.misle.game.GamePanel.player;

/**
 * Centralized definitions of enemy setup configurations.
 * Each constant provides a lambda that customizes stats, visuals,
 * behavior, and loot for a particular type of enemy.
 * <p>
 * These are used by {@link EnemyType}.
 */
public enum EnemyConfigurations {
    RED_BLOCK(enemy -> {
        enemy.setMaxHP(50);
        enemy.fillHP();
        enemy.setDamage(5, 1);
        enemy.setTexture("solid");
        enemy.setColor(new Color(0xA02020));
        enemy.setDropTable(DropTable.POTION_CHEST);
        enemy.setXpDrop(50);
        enemy.setCoinDropRange(0, 100);
        enemy.setCollision(true);
    }),

    GOBLIN(enemy -> {
        enemy.setMaxHP(20);
        enemy.fillHP();
        enemy.setDamage(3);
        enemy.setTexture("../characters/enemy/goblin");
        enemy.setDropTable(DropTable.GOBLIN);
        enemy.setXpDrop(1);
        enemy.setCoinDrop(3);
        enemy.setCollision(true);
        enemy.setTurnsToRespawn(50);
        enemy.scheduleOnDamage(
            () -> enemy.setDisplayHP(true)
        );

        var wait = new WaitBehavior();
        var pursue = new PursueBehavior();
        var wander = new WanderBehavior(3);

        ChainBehavior chain;
        switch (player.getDifficulty()) {
            case EASY -> {
                chain = new ChainBehavior(
                    wait,
                    pursue
                );
            }
            case MEDIUM -> {
                chain = new ChainBehavior(
                    wait,
                    pursue,
                    wait,
                    wait,
                    pursue
                );
            }
            case HARD -> {
                chain = new ChainBehavior(
                    wait,
                    pursue,
                    pursue,
                    wait,
                    wait,
                    pursue
                );
            }
            case NIGHTMARE -> {
                chain = new ChainBehavior(
                    wait,
                    pursue,
                    pursue,
                    wait,
                    pursue
                );
            }
            default -> {
                chain = new ChainBehavior(
                    wait,
                    pursue,
                    pursue,
                    wait,
                    wait,
                    pursue
                );
            }
        }

        chain.addConditions(
            pursue.getConditions()
        );

        enemy.getController().setTarget(player);

        enemy.setBehaviors(
            chain,
            wander
        );
    });

    /**
     * The functional configurator for this enemy setup.
     */
    public final EnemyConfigurator c;

    EnemyConfigurations(EnemyConfigurator configurator) {
        this.c = configurator;
    }
}
