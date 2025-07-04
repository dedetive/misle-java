package com.ded.misle.world.data;

public enum Difficulty {

    // This is just WIP, I'll most likely change their names, and maybe add or remove some

    EASY(0.75f),
    MEDIUM(1f),
    HARD(1.25f),
    NIGHTMARE(1.75f)

    ;

    public final float enemyStatMultiplier;

    Difficulty(float enemyStatMultiplier) {
        this.enemyStatMultiplier = enemyStatMultiplier;
    }
}