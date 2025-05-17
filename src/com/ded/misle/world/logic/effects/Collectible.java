package com.ded.misle.world.logic.effects;

import com.ded.misle.renderer.PlayingRenderer;
import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.entities.player.Player;

import static com.ded.misle.audio.AudioPlayer.AudioFile.collect_item;
import static com.ded.misle.audio.AudioPlayer.playThis;
import static com.ded.misle.game.GamePanel.player;
import static com.ded.misle.items.Item.createItem;
import static com.ded.misle.world.boxes.BoxHandling.deleteBox;

public class Collectible extends Effect {
    public boolean collectible = true;
    int id;
    int count = 1;

    Collectible(int id) {
        this.id = id;
    }

    public Collectible(int id, int count, boolean collectible) {
        this.collectible = collectible;
        this.id = id;
        this.count = count;
    }

    @Override
    public void run(Box culprit, Box victim) {
        if (!(victim instanceof Player)) return;

        handleBoxItemCollectible(culprit);
    }

    private void handleBoxItemCollectible(Box culprit) {
        if (!collectible) return;
        if (culprit.isMoving) return; // Moving items should not be caught

        if (id == 0) {
            deleteBox(culprit);
        }

        if (player.inv.addItem(createItem(id, count))) {
            playThis(collect_item);
            PlayingRenderer.updateSelectedItemNamePosition();
            deleteBox(culprit);
        }
    }

    @Override
    public String toString() {
        return "Collectible{" +
            "collectible=" + collectible +
            ", id=" + id +
            ", count=" + count +
            '}';
    }
}
