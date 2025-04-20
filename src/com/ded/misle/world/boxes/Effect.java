package com.ded.misle.world.boxes;

import com.ded.misle.world.player.Player;

import java.util.Arrays;

import static com.ded.misle.core.GamePanel.player;
import static java.lang.System.currentTimeMillis;

abstract class Effect {
    public static class DamageEffect extends Effect {
        public double damageRate;
        public double damageValue;
        public long lastDamageTime;

        public DamageEffect(long damageRate) {
            this.damageRate = damageRate;
        }

        public void handleEffect(Box culprit, HPBox victim) {
            if (!culprit.interactsWithPlayer && victim == player) return;

            handleBoxDamageCooldown(victim);
        }

        private void handleBoxDamageCooldown(HPBox victim) {
            long currentTime = currentTimeMillis();
            long cooldownDuration = (long) this.damageRate;

            // Check if enough time has passed since the last damage was dealt
            if (currentTime - this.lastDamageTime >= cooldownDuration) {
                this.lastDamageTime = currentTime;
                victim.takeDamage(this.damageValue, this.getEffectReason(), this.getEffectArgs(), victim.getKnockbackDirection());
//			System.out.println(box.getEffectValue() + " " + box.getEffectReason() + " damage dealt! Now at " + player.attr.getHP() + " HP.");
            }
        }
    }
    public static class HealEffect extends Effect {
        public double effectRate;

        public HealEffect(long effectRate) {
            this.effectRate = effectRate;
        }
    }
    public static class ChestEffect extends Effect {
        public double effectRate;

        public ChestEffect(long effectRate) {
            this.effectRate = effectRate;
        }
    }
    public static class SpawnpointEffect extends Effect {

    }
    public static class TravelEffect extends Effect {

    }
    public static class ItemEffect extends Effect {

    }

    public Effect effectType;

    Effect() {
        effectType = Effect.this;
    }

    public void handleEffect(HPBox box) {
        if (!this.interactsWithPlayer && box == player) return;

        switch (this.effect[0]) {
            case "damage" -> this.handleBoxDamageCooldown(box);
            case "heal" -> this.handleBoxHealCooldown(box);
            case "velocity" -> this.handleBoxVelocity();
            case "spawnpoint" -> { if (box instanceof Player) this.handleBoxSpawnpoint(); }
            case "chest" -> { if (box instanceof Player) this.handleBoxChest(); }
            case "item" -> { if (box instanceof Player) this.handleBoxItemCollectible(); }
            case "travel" -> { if (box instanceof Player) this.handleBoxTravel(); }
        }
    }

    public void setEffectValue(double effectValue) {
        this.effect[1] = String.valueOf(effectValue);
    }

    public void setEffectRate(double effectRate) {
        this.effect[2] = String.valueOf(effectRate);
    }

    public String getEffectReason() {
        return switch (getEffect()) {
            case "damage" -> this.effect[3];
            case "heal" -> this.effect[3];
            case "item" -> this.effect[3];
            default -> "";
        };
    }

    public void setEffectReason(String reason) {
        switch (reason) {
            case "damage" -> this.effect[3] = reason;
            case "heal" -> this.effect[3] = reason;
            default -> this.effect[3] = reason;
        }
    }

    public String[] getEffectArgs() {
        return switch (getEffect()) {
            case "damage" -> new String[]{this.effect[4]};
            default -> new String[]{""};
        };
    }

    public void setEffectArgs(String[] args) {
        switch (getEffect()) {
            case "damage" -> this.effect[4] = Arrays.toString(args);
        }
    }

    public long getLastEffectTime() {
        return lastDamageTime;
    }

    public void setLastEffectTime(long lastDamageTime) {
        this.lastDamageTime = lastDamageTime;
    }
}
