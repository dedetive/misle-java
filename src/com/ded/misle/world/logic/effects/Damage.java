package com.ded.misle.world.logic.effects;

import com.ded.misle.core.TurnTimer;
import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.entities.HPBox;

import java.time.Duration;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;

import static com.ded.misle.game.GamePanel.player;
import static com.ded.misle.world.entities.HPBox.DamageFlag.NORMAL;

public class Damage extends Effect {
    public double damage;
    public int damageRate;

    public EnumSet<HPBox.DamageFlag> flags;
    public Optional<Duration> lockDuration;

    private boolean canDamage = true;
    TurnTimer t;

    public Damage(double damage, int damageRate) {
        this(damage, damageRate, HPBox.DamageFlag.of(NORMAL), Optional.empty());
    }

    public Damage(double damage, int damageRate, EnumSet<HPBox.DamageFlag> flags) {
        this(damage, damageRate, flags, Optional.empty());
    }

    public Damage(double damage, int damageRate, EnumSet<HPBox.DamageFlag> flags, Optional<Duration> lockDuration) {
        this.damageRate = damageRate;
        this.damage = damage;
        this.flags = flags;
        this.lockDuration = lockDuration;
        t = new TurnTimer(damageRate, e -> canDamage = true).setRoomScoped(true);
    }

    @Override
    public void run(Box culprit, Box victim) {
        if (!(victim instanceof HPBox)) return;
        if (!culprit.getInteractsWithPlayer() && victim == player) return;

        handleBoxDamageCooldown((HPBox) victim);
    }

    private void handleBoxDamageCooldown(HPBox victim) {
        if (canDamage) {
            victim.takeDamage(damage, flags, lockDuration, victim.getKnockbackDirection());

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
            ", flags=" + Arrays.toString(flags.toArray()) +
            '}';
    }
}
