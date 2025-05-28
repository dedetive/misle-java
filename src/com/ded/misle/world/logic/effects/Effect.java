package com.ded.misle.world.logic.effects;

import com.ded.misle.world.boxes.Box;

public abstract class Effect {
    Effect() {}

    private boolean triggersOnContact = true;
    public abstract void run(Box culprit, Box victim);

    public boolean getTriggersOnContact() {
        return triggersOnContact;
    }
    public Effect setTriggersOnContact(boolean triggersOnContact) {
        this.triggersOnContact = triggersOnContact;
        return this;
    }

    @Override
    public String toString() {
        return "Effect{" +
            "effectType=" + this.getClass().getSimpleName() +
            '}';
    }
}
