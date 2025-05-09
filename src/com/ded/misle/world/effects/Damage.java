package com.ded.misle.world.effects;

import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.boxes.HPBox;

import static com.ded.misle.core.GamePanel.player;
import static java.lang.System.currentTimeMillis;

public class Damage extends Effect {
    public double damage;
    public double damageRate;

    public long lastDamageTime;
    public String reason = "normal";
    public String[] args = new String[]{};

    public Damage(double damage, double damageRate) {
        this.damageRate = damageRate;
        this.damage = damage;
        lastDamageTime = 0;
    }

    public Damage(double damage, double damageRate, String reason, String[] args) {
        this.damageRate = damageRate;
        this.damage = damage;
        this.reason = reason;
        this.args = args;
        lastDamageTime = 0;
    }

    @Override
    public void run(Box culprit, Box victim) {
        if (!(victim instanceof HPBox)) return;
        if (!culprit.interactsWithPlayer && victim == player) return;

        handleBoxDamageCooldown((HPBox) victim);
    }

    private void handleBoxDamageCooldown(HPBox victim) {
        long currentTime = currentTimeMillis();
        long cooldownDuration = (long) damageRate;

        // Check if enough time has passed since the last damage was dealt
        if (currentTime - lastDamageTime >= cooldownDuration) {
            lastDamageTime = currentTime;
            victim.takeDamage(damage, reason, args, victim.getKnockbackDirection());
//			System.out.println(box.getEffectValue() + " " + box.getEffectReason() + " damage dealt! Now at " + player.attr.getHP() + " HP.");
        }
    }

    @Override
    public String toString() {
        return "Damage{" +
            "damage=" + damage +
            "damageRate=" + damageRate +
            "nextDamageTick=" + lastDamageTime + damageRate +
            "reason=" + reason +
            '}';
    }
}
