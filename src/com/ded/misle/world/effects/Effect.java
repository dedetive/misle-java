package com.ded.misle.world.effects;

import com.ded.misle.world.boxes.Box;

public abstract class Effect {
    Effect() {}

    public abstract void run(Box culprit, Box victim);

    @Override
    public String toString() {
        return "Effect{" +
            "effectType=" + this.getClass().getSimpleName() +
            '}';
    }
}
