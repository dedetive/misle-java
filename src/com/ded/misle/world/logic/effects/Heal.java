package com.ded.misle.world.logic.effects;

import com.ded.misle.world.logic.TurnTimer;
import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.entities.Entity;

import java.util.Arrays;
import java.util.EnumSet;

import static com.ded.misle.game.GamePanel.player;

public class Heal extends Effect {
    public double healValue;
    public int healRate;

    public EnumSet<Entity.HealFlag> flags;

    private boolean canHeal = true;
    TurnTimer t;

    public Heal(double healValue, int healRate) {
        this(healValue, healRate, Entity.HealFlag.of(Entity.HealFlag.NORMAL));
    }

    public Heal(double healValue, int healRate, EnumSet<Entity.HealFlag> flags) {
        this.healValue = healValue;
        this.healRate = healRate;
        this.flags = flags;
        t = new TurnTimer(healRate, e -> canHeal = true).setRoomScoped(true);
    }

    @Override
    public void run(Box culprit, Box victim) {
        if (!(victim instanceof Entity)) return;
        if (!culprit.getInteractsWithPlayer() && victim == player) return;

        handleBoxHealCooldown((Entity) victim);
    }

    private void handleBoxHealCooldown(Entity victim) {
        if (canHeal) {
            victim.receiveHeal(healValue, flags);

            canHeal = false;
            t.restart();

//			System.out.println(box.getEffectValue() + " " + box.getEffectReason() + " heal received! Now at " + player.attr.getHP() + " HP.");
        }
    }

    @Override
    public String toString() {
        return "Heal{" +
            "healValue=" + healValue +
            ", healRate=" + healRate +
            ", nextHealTick=" + (t.getRemainingTurnsUntilActivation()) +
            ", flags=" + Arrays.toString(flags.toArray()) +
            '}';
    }
}
