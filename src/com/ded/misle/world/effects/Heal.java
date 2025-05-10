package com.ded.misle.world.effects;

import com.ded.misle.core.TurnTimer;
import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.boxes.HPBox;

import static com.ded.misle.core.GamePanel.player;
import static java.lang.System.currentTimeMillis;

public class Heal extends Effect {
    public double healValue;
    public int healRate;

    public String reason;

    private boolean canHeal = true;
    TurnTimer t;

    public Heal(double healValue, int healRate) {
        this(healValue, healRate, "normal");
    }

    public Heal(double healValue, int healRate, String reason) {
        this.healValue = healValue;
        this.healRate = healRate;
        this.reason = reason;
        t = new TurnTimer(healRate, e -> canHeal = true).setRoomScoped(true);
    }

    @Override
    public void run(Box culprit, Box victim) {
        if (!(victim instanceof HPBox)) return;
        if (!culprit.interactsWithPlayer && victim == player) return;

        handleBoxHealCooldown((HPBox) victim);
    }

    private void handleBoxHealCooldown(HPBox victim) {
        if (canHeal) {
            victim.receiveHeal(healValue, reason);

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
            ", reason=" + reason +
            '}';
    }
}
