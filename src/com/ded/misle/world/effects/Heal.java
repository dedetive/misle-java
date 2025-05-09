package com.ded.misle.world.effects;

import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.boxes.HPBox;

import static com.ded.misle.core.GamePanel.player;
import static java.lang.System.currentTimeMillis;

public class Heal extends Effect {
    public double healRate;
    public double healValue;
    public long lastHealTime;
    public String reason;

    public Heal(double healRate, double healValue) {
        this.healRate = healRate;
        this.healValue = healValue;
        lastHealTime = 0;
    }

    @Override
    public void run(Box culprit, Box victim) {
        if (!(victim instanceof HPBox)) return;
        if (!culprit.interactsWithPlayer && victim == player) return;

        handleBoxHealCooldown((HPBox) victim);
    }

    // TODO: Update heal time to new turns system instead of time-based
    private void handleBoxHealCooldown(HPBox victim) {
        long currentTime = currentTimeMillis();
        long cooldownDuration = (long) healRate;

        if (currentTime - lastHealTime >= cooldownDuration) {
            lastHealTime = currentTime;
            victim.receiveHeal(healValue, reason);
//			System.out.println(box.getEffectValue() + " " + box.getEffectReason() + " heal received! Now at " + player.attr.getHP() + " HP.");
        }
    }

    @Override
    public String toString() {
        return "Heal{" +
            "healValue=" + healValue +
            "healRate=" + healRate +
            "nextHealTick=" + lastHealTime + healRate +
            "reason=" + reason +
            '}';
    }
}
