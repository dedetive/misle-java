package com.ded.misle.world.effects;

import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.player.Player;

public class Spawnpoint extends Effect {
    int roomID;

    public Spawnpoint(int id) {
        this.roomID = id;
    }

    @Override
    public void run(Box culprit, Box victim) {
        if (victim instanceof Player) handleBoxSpawnpoint((Player) victim);
    }

    private void handleBoxSpawnpoint(Player player) {
        if (roomID >= 0 && player.pos.getSpawnpoint() != player.pos.getRoomID()) {
            player.pos.setSpawnpoint(player.pos.getRoomID());
        }
    }

    @Override
    public String toString() {
        return "Spawnpoint{" +
            "roomID=" + roomID +
            ", activated=" + (com.ded.misle.core.GamePanel.player.pos.getSpawnpoint() == roomID) +
            '}';
    }
}
