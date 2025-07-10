package com.ded.misle.world.data.entity.configurations;

import com.ded.misle.items.DropTable;
import com.ded.misle.world.data.entity.EnemyConfigurator;
import com.ded.misle.world.data.entity.GenericConfigurations;
import com.ded.misle.world.entities.ai.behaviors.ChainBehavior;
import com.ded.misle.world.entities.ai.behaviors.PursueBehavior;
import com.ded.misle.world.entities.ai.behaviors.WaitBehavior;
import com.ded.misle.world.entities.ai.behaviors.WanderBehavior;

import static com.ded.misle.game.GamePanel.player;

/**
 * Centralized definitions of enemy setup configurations.
 * Each constant provides a lambda that customizes stats, visuals,
 * behavior, and loot for a particular type of enemy.
 * <p>
 * These are used by {@link EnemyType}.
 */
public enum EnemyConfigurations implements GenericConfigurations {
    GOBLIN(goblin -> {
        goblin.setMaxHP(20);
        goblin.fillHP();
        goblin.setDamage(3);
        goblin.setTexture("goblin");
        goblin.setDropTable(DropTable.GOBLIN);
        goblin.setXpDrop(1);
        goblin.setCoinDrop(3);
        goblin.setTurnsToRespawn(50);
        goblin.scheduleOnDamage(
            () -> goblin.setDisplayHP(true)
        );
        goblin.setMaxSight(6);

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

        goblin.getController().setTarget(player);

        goblin.setBehaviors(
            chain,
            wander
        );
    })

    ;

    /**
     * The functional configurator for this enemy setup.
     */
    public final EnemyConfigurator c;

    EnemyConfigurations(EnemyConfigurator configurator) {
        this.c = configurator;
    }
}
