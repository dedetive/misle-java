package com.ded.misle.world.effects;

import com.ded.misle.core.TurnTimer;
import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.boxes.HPBox;

import static com.ded.misle.core.GamePanel.player;

public class Damage extends Effect {
    public double damage;
    public int damageRate;

    public String reason;
    public String[] args;

    private boolean canDamage = true;
    TurnTimer t;

    public Damage(double damage, int damageRate) {
        this(damage, damageRate, "normal", new String[]{});
    }

    public Damage(double damage, int damageRate, String reason, String[] args) {
        this.damageRate = damageRate;
        this.damage = damage;
        this.reason = reason;
        this.args = args;
        t = new TurnTimer(damageRate, e -> canDamage = true).setRoomScoped(true);
    }

    @Override
    public void run(Box culprit, Box victim) {
        if (!(victim instanceof HPBox)) return;
        if (!culprit.interactsWithPlayer && victim == player) return;

        handleBoxDamageCooldown((HPBox) victim);
    }

    private void handleBoxDamageCooldown(HPBox victim) {
        if (canDamage) {
            victim.takeDamage(damage, reason, args, victim.getKnockbackDirection());

            canDamage = false;
            t.restart();

//			System.out.println(box.getEffectValue() + " " + box.getEffectReason() + " damage dealt! Now at " + player.attr.getHP() + " HP.");
        }
    }

    @Override
    public String toString() {
        return "Damage{" +
            "damage=" + damage +
            ", damageRate=" + damageRate +
            ", nextDamageTick=" + (t.getRemainingTurnsUntilActivation()) +
            ", reason=" + reason +
            '}';
    }
}
