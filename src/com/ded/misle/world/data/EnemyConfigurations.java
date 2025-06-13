package com.ded.misle.world.data;

import com.ded.misle.items.DropTable;
import com.ded.misle.world.entities.ai.AIBehavior;
import com.ded.misle.world.entities.ai.BehaviorContext;
import com.ded.misle.world.entities.ai.behaviors.PatrolBehavior;
import com.ded.misle.world.entities.ai.behaviors.WanderBehavior;
import com.ded.misle.world.entities.enemies.EnemyConfigurator;
import com.ded.misle.world.entities.enemies.EnemyType;
import com.ded.misle.world.logic.Path;

import java.awt.*;
import java.util.function.Function;

/**
 * Centralized definitions of enemy setup configurations.
 * Each constant provides a lambda that customizes stats, visuals,
 * behavior, and loot for a particular type of enemy.
 *
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
        enemy.setDamage(3, 2);
        enemy.setTexture("../characters/enemy/goblin");
        enemy.setDropTable(DropTable.GOBLIN);
        enemy.setXpDrop(1);
        enemy.setCoinDrop(3);
        enemy.setCollision(true);

        Point[] upwardsPoints = new Point[] {
            new Point(0, 0),
            new Point(0, -1),
        };

        Function<BehaviorContext, Boolean> isMaxHP =
            context ->
                context.self().getHP() == context.self().getMaxHP();

        Function<BehaviorContext, Boolean> hasOverThirdHP =
            context ->
                context.self().getHP() >= context.self().getMaxHP() / 3;

        Function<BehaviorContext, Boolean> hasUnderThirdHP =
            context ->
                context.self().getHP() < context.self().getMaxHP() / 3;

        AIBehavior freelyWalkingAround = new WanderBehavior();
        freelyWalkingAround.setCondition(isMaxHP);

        AIBehavior limitedWalkingAround = new WanderBehavior(1);
        limitedWalkingAround.setCondition(hasOverThirdHP);

        AIBehavior goingUp = new WanderBehavior(new Path(upwardsPoints));
        goingUp.setCondition(hasUnderThirdHP);

        // this is all temporary don't worry
        enemy.setBehaviors(
            freelyWalkingAround,
            limitedWalkingAround,
            goingUp
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
