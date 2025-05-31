package com.ded.misle.world.entities.enemies;

import com.ded.misle.items.DropTable;
import com.ded.misle.world.logic.effects.Damage;

import java.awt.*;

public enum EnemyType {
    RED_BLOCK(enemy -> {
        double mag = enemy.getMagnification();

        enemy.setMaxHP(50);
        enemy.setHP(50);
        enemy.effect = new Damage(5 * mag, 1).setTriggersOnContact(false);
        enemy.setTexture("solid");
        enemy.setColor(new Color(0xA02020));
        enemy.setDropTable(DropTable.POTION_CHEST);
        enemy.setXpDrop(50);
        enemy.setCoinDropRange(0, 100);
        enemy.setCollision(true);
    }),

    GOBLIN(enemy -> {
        double mag = enemy.getMagnification();

        enemy.setMaxHP(20 * mag);
        enemy.setHP(20 * mag);
        enemy.effect = new Damage(3 * mag, 2).setTriggersOnContact(false);
        enemy.setTexture("../characters/enemy/goblin");
        enemy.setDropTable(DropTable.GOBLIN);
        enemy.setXpDrop(1);
        enemy.setCoinDrop(3);
        enemy.setCollision(true);
    });

    private final EnemyConfigurator configurator;

    EnemyType(EnemyConfigurator configurator) {
        this.configurator = configurator;
    }

    public void applyTo(Enemy enemy) {
        configurator.configure(enemy);
    }
}
