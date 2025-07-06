package com.ded.misle.world.logic.effects;

import com.ded.misle.items.DropTable;
import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.entities.player.Player;

import static com.ded.misle.game.GamePanel.player;
import static com.ded.misle.world.logic.PhysicsEngine.isSpaceOccupied;

public class Chest extends Effect {
    public int openRate;
    public DropTable dropTable;

    public Chest(int openRate, DropTable dropTable) {
        this.openRate = openRate;
        this.dropTable = dropTable;
    }

    public void run(Box chest, Box culprit) {
        if (!(culprit instanceof Player)) return;

        String chestId = chest.getId();
        int storedTurns = player.loadTimerFromUUID(chestId);

        if (storedTurns <= 0) {
            handleBoxChest(chest);
            player.storeTimerInUUID(chestId, openRate);
        }
    }

    private void handleBoxChest(Box chest) {
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
            '}';
    }
}
