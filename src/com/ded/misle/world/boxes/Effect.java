package com.ded.misle.world.boxes;

import com.ded.misle.core.GamePanel;
import com.ded.misle.renderer.MainRenderer;
import com.ded.misle.renderer.PlayingRenderer;
import com.ded.misle.world.chests.DropTable;
import com.ded.misle.world.player.Player;

import javax.swing.*;
import java.awt.*;

import static com.ded.misle.audio.AudioPlayer.AudioFile.collect_item;
import static com.ded.misle.audio.AudioPlayer.playThis;
import static com.ded.misle.core.GamePanel.gameState;
import static com.ded.misle.core.GamePanel.player;
import static com.ded.misle.items.Item.createItem;
import static com.ded.misle.renderer.MainRenderer.*;
import static com.ded.misle.world.WorldLoader.loadBoxes;
import static com.ded.misle.world.WorldLoader.unloadBoxes;
import static com.ded.misle.world.boxes.BoxHandling.deleteBox;
import static com.ded.misle.world.boxes.BoxHandling.getCollisionBoxesInRange;
import static com.ded.misle.world.enemies.EnemyAI.clearBreadcrumbs;
import static java.lang.System.currentTimeMillis;

public abstract class Effect {
    public static class Damage extends Effect {
        public double damageRate;
        public double damage;
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
        public void run(Box culprit, Box victim) {
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
        public DropTable dropTable;

        public Chest(double openRate, DropTable dropTable) {
            this.openRate = openRate;
            this.lastTimeOpen = 0;
            this.dropTable = dropTable;
        }

        public void run(Box culprit, Box chest) {
            if (!(culprit instanceof Player)) return;

            long currentTime = currentTimeMillis();
            double cooldownDuration = openRate * 1000;

            if (currentTime - this.lastTimeOpen >= cooldownDuration) {
                handleBoxChest(chest, currentTime);
            }
        }

        private void handleBoxChest(Box chest, long currentTime) {
            lastTimeOpen = currentTime;
            int[] results = dropTable.getDropTableItemID();
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
        int roomID;
        Point coordinates; // TODO: Currently unused please fix

        Spawnpoint(int id, Point coordinates) {
            this.roomID = id;
            this.coordinates = coordinates;
        }

        @Override
        public void run(Box culprit, Box unused) {
            if (culprit instanceof Player) handleBoxSpawnpoint((Player) culprit);
        }

        private void handleBoxSpawnpoint(Player culprit) {
            if (roomID > 0 && culprit.pos.getSpawnpoint() != culprit.pos.getRoomID()) {
                culprit.pos.setSpawnpoint(culprit.pos.getRoomID());
                System.out.println("Saved spawnpoint as room " + culprit.pos.getRoomID());
            }
        }
    }
    public static class Travel extends Effect {
        int roomID;
        Point coordinates; // TODO: Currently unused please fix

        Travel(int roomID, Point coordinates) {
            this.roomID = roomID;
            this.coordinates = coordinates;
        }

        @Override
        public void run(Box culprit, Box victim) {
            if (!(culprit instanceof Player)) return;

            handleBoxTravel();
        }

        private void handleBoxTravel() {
            fadeIn();
            gameState = GamePanel.GameState.FROZEN_PLAYING;
            Timer fadingIn = new Timer(75, e -> {
                if (isFading == MainRenderer.FadingState.FADED) {

                    player.pos.setRoomID(roomID);

                    player.setX(coordinates.x);
                    player.setY(coordinates.y);
                    unloadBoxes();
                    loadBoxes();
                    clearBreadcrumbs();

                    Timer loadWait = new Timer(300, evt -> {
                        fadeOut();
                        gameState = GamePanel.GameState.PLAYING;
                    });
                    loadWait.setRepeats(false);
                    loadWait.start();

                    ((Timer) e.getSource()).stop();
                }
            });
            fadingIn.setRepeats(true);
            fadingIn.start();
        }
    }
    public static class Collectible extends Effect {
        public boolean collectible = true;
        int id;
        int count = 1;

        Collectible(int id) {
            this.id = id;
        }
        Collectible(int id, int count, boolean collectible) {
            this.collectible = collectible;
            this.id = id;
            this.count = count;
        }

        @Override
        public void run(Box culprit, Box victim) {
            if (!(culprit instanceof Player)) return;

            handleBoxItemCollectible(victim);
        }

        private void handleBoxItemCollectible(Box victim) {
            if (!collectible) return;

            if (id == 0) {
                deleteBox(victim);
            }

            if (player.inv.addItem(createItem(id, count))) {
                playThis(collect_item);
                PlayingRenderer.updateSelectedItemNamePosition();
                deleteBox(victim);
            }
        }
    }

    Effect() {}

    public abstract void run(Box culprit, Box victim);
}
