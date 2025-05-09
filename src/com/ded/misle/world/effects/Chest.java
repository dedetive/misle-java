package com.ded.misle.world.effects;

import com.ded.misle.items.DropTable;
import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.player.Player;

import static com.ded.misle.core.PhysicsEngine.isSpaceOccupied;
import static java.lang.System.currentTimeMillis;

public class Chest extends Effect {
    public double openRate;
    public long lastTimeOpen;
    public DropTable dropTable;

    public Chest(double openRate, DropTable dropTable) {
        this.openRate = openRate;
        this.lastTimeOpen = 0;
        this.dropTable = dropTable;
    }

    public void run(Box chest, Box culprit) {
        if (!(culprit instanceof Player)) return;

        long currentTime = currentTimeMillis();
        double cooldownDuration = openRate * 1000;

        if (currentTime - this.lastTimeOpen >= cooldownDuration) {
            handleBoxChest(chest, currentTime);
        }
    }

    private void handleBoxChest(Box chest, long currentTime) {
        lastTimeOpen = currentTime;
        int[] results = dropTable.getRandomItemID();
        int id = results[0];
        int count = results[1];
        boolean canGoMinus = false;
        boolean canGoPlus = false;
        if (!isSpaceOccupied(chest.getX() - 1, chest.getY(), new Box())) {
            canGoMinus = true;
        }
        if (!isSpaceOccupied(chest.getX() + 1, chest.getY(), new Box())) {
            canGoPlus = true;
        }

        chest.spawnItem(canGoMinus, canGoPlus, id, count);
    }

    @Override
    public String toString() {
        return "Chest{" +
            "dropTable=" + dropTable +
            ", openRate=" + openRate +
            ", nextOpenTick=" + (lastTimeOpen + openRate) +
            '}';
    }
}
