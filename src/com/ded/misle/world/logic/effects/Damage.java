package com.ded.misle.world.logic.effects;

import com.ded.misle.world.data.Direction;
import com.ded.misle.world.logic.TurnTimer;
import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.entities.Entity;

import java.time.Duration;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Optional;

import static com.ded.misle.game.GamePanel.player;
import static com.ded.misle.world.entities.Entity.DamageFlag.NORMAL;

public class Damage extends Effect {
    public double damage;
    public int damageRate;

    public EnumSet<Entity.DamageFlag> flags;
    public int lockDuration;

    private boolean canDamage = true;
    TurnTimer t;

    public Damage(double damage, int damageRate) {
        this(damage, damageRate, Entity.DamageFlag.of(NORMAL), 0);
    }

    public Damage(double damage, int damageRate, EnumSet<Entity.DamageFlag> flags) {
        this(damage, damageRate, flags, 0);
    }

    public Damage(double damage, int damageRate, EnumSet<Entity.DamageFlag> flags, int lockDuration) {
        this.damageRate = damageRate;
        this.damage = damage;
        this.flags = flags;
        this.lockDuration = lockDuration;
        t = new TurnTimer(damageRate, e -> canDamage = true).setRoomScoped(true);
    }

    @Override
    public void run(Box culprit, Box victim) {
        if (!(victim instanceof Entity)) return;
        if (!culprit.getInteractsWithPlayer() && victim == player) return;

        handleBoxDamageCooldown(culprit, (Entity) victim);
    }

    private void handleBoxDamageCooldown(Box culprit, Entity victim) {

        Direction hitDirection = Direction.interpretDirection(
            culprit.getX() - victim.getX(),
            culprit.getY() - victim.getY()
        );

        if (canDamage) {
            victim.takeDamage(damage, flags, lockDuration, hitDirection);

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
