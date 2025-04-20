package com.ded.misle.world.boxes;

import com.ded.misle.world.player.Player;

import java.awt.*;

import static com.ded.misle.core.GamePanel.player;
import static com.ded.misle.world.boxes.BoxHandling.getCollisionBoxesInRange;
import static com.ded.misle.world.chests.DropTable.getDropTableItemID;
import static java.lang.System.currentTimeMillis;

public abstract class Effect {
    public static class Damage extends Effect {
        public double damageRate;
        public double damageValue;
        public long lastDamageTime;
        public String reason;
        public String[] args;

        public Damage(double damageRate, double damageValue) {
            this.damageRate = damageRate;
            this.damageValue = damageValue;
            lastDamageTime = 0;
        }

        @Override
        public void handle(Box culprit, Box victim) {
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
                victim.takeDamage(damageValue, reason, args, victim.getKnockbackDirection());
//			System.out.println(box.getEffectValue() + " " + box.getEffectReason() + " damage dealt! Now at " + player.attr.getHP() + " HP.");
            }
        }
    }
    public static class Heal extends Effect {
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
        public void handle(Box culprit, Box victim) {
            if (!(victim instanceof HPBox)) return;
            if (!culprit.interactsWithPlayer && victim == player) return;

            handleBoxHealCooldown((HPBox) victim);
        }

        private void handleBoxHealCooldown(HPBox victim) {
            long currentTime = currentTimeMillis();
            long cooldownDuration = (long) healRate;

            if (currentTime - lastHealTime >= cooldownDuration) {
                lastHealTime = currentTime;
                victim.receiveHeal(healValue, reason);
//			System.out.println(box.getEffectValue() + " " + box.getEffectReason() + " heal received! Now at " + player.attr.getHP() + " HP.");
            }
        }
    }
    public static class Chest extends Effect {
        public double openRate;
        public long lastTimeOpen;
        public String dropTable;

        public Chest(long openRate) {
            this.openRate = openRate;
            this.lastTimeOpen = 0;
        }

        public void handle(Box culprit, Box chest) {
            if (!(culprit instanceof Player)) return;

            long currentTime = currentTimeMillis();
            double cooldownDuration = openRate * 1000;

            if (currentTime - this.lastTimeOpen >= cooldownDuration) {
                handleBoxChest(chest, currentTime);
            }
        }

        private void handleBoxChest(Box chest, long currentTime) {
            lastTimeOpen = currentTime;
            int[] results = getDropTableItemID(dropTable);
            int id = results[0];
            int count = results[1];
            boolean canGoMinus = false;
            boolean canGoPlus = false;
            if (getCollisionBoxesInRange(chest.getX() - 1, chest.getY(), 10, 6).isEmpty()) {
                canGoMinus = true;
            }
            if (getCollisionBoxesInRange(chest.getX() + 1, chest.getY(), 10, 6).isEmpty()) {
                canGoPlus = true;
            }

            chest.spawnItem(canGoMinus, canGoPlus, id, count);
        }
    }
    public static class Spawnpoint extends Effect {
        int id;
        Point coordinates; // TODO: Currently unused please fix

        Spawnpoint(int id, Point coordinates) {
            this.id = id;
            this.coordinates = coordinates;
        }

        @Override
        public void handle(Box culprit, Box unused) {
            if (culprit instanceof Player) handleBoxSpawnpoint((Player) culprit);
        }

        private void handleBoxSpawnpoint(Player culprit) {
            if (id > 0 && culprit.pos.getSpawnpoint() != culprit.pos.getRoomID()) {
                culprit.pos.setSpawnpoint(culprit.pos.getRoomID());
                System.out.println("Saved spawnpoint as room " + culprit.pos.getRoomID());
            }
        }
    }
    public static class Travel extends Effect {

    }
    public static class Item extends Effect {

    }

    public Effect type;

    Effect() {
        type = Effect.this;
    }

    public abstract void handle(Box culprit, Box victim);

    public void handleEffect(HPBox box) {
        if (!this.interactsWithPlayer && box == player) return;

        switch (this.effect[0]) {
            case "spawnpoint" -> { if (box instanceof Player) this.handleBoxSpawnpoint(); }
            case "item" -> { if (box instanceof Player) this.handleBoxItemCollectible(); }
            case "travel" -> { if (box instanceof Player) this.handleBoxTravel(); }
        }
    }
}
