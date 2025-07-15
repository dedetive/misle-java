package com.ded.misle.world.logic.effects;

import com.ded.misle.world.data.items.DropTable;
import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.entities.player.Player;
import com.ded.misle.world.logic.PhysicsEngine;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.ded.misle.game.GamePanel.player;
import static com.ded.misle.world.data.items.Item.createDroppedItem;

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

        chest.setTexture(chest.textureName + "_open");

        Point c = chest.getPos();
        List<Point> points = new ArrayList<>(List.of(
            new Point(c.x - 1, c.y),
            new Point(c.x + 1, c.y),
            new Point(c.x, c.y - 1),
            new Point(c.x, c.y + 1)
        ));

        points.removeIf(
            point -> PhysicsEngine.isSpaceOccupied(point.x, point.y)
        );
        if (points.isEmpty()) points.add(player.getPos());

        Random random = new Random();
        Point chosenPos = points.get(random.nextInt(points.size()));

        createDroppedItem(chosenPos.x, chosenPos.y, id, count);

        Timer timer = new Timer(825, e ->
            chest.setTexture(chest.textureName.replace("_open", "")));
        timer.setRepeats(false);
        timer.start();
    }

    @Override
    public String toString() {
        return "Chest{" +
            "dropTable=" + dropTable +
            ", openRate=" + openRate +
            '}';
    }
}
