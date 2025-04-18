package com.ded.misle.world.player;

import com.ded.misle.world.boxes.Box;
import com.ded.misle.input.MouseHandler;
import com.ded.misle.items.Item;

import javax.swing.*;

import java.util.ArrayList;
import java.util.List;

import static com.ded.misle.core.GamePanel.player;
import static com.ded.misle.Launcher.scale;
import static com.ded.misle.core.PhysicsEngine.isSpaceOccupied;
import static com.ded.misle.world.boxes.BoxHandling.*;
import static com.ded.misle.world.player.PlayerAttributes.KnockbackDirection.NONE;

public class HandItemAnimator {
    private static void scheduleAnimation(int delay, Runnable action) {
        Timer timer = new Timer(delay, evt -> action.run());
        timer.setRepeats(false);
        timer.start();
    }

    public static void animateClaw(MouseHandler mouseHandler) {
        // PREPARATION (move claw up and swing back)
        Item selectedItem = player.inv.getSelectedItem();

        selectedItem.delayedSetAnimationRotation(-75, 180);
        selectedItem.delayedChangeAnimationBulk(0.175, 180);
        selectedItem.delayedMoveAnimationY(-30, 70);
        selectedItem.delayedMoveAnimationX(15, 70);

        // ATTACK (swing forward)
        scheduleAnimation(215, () -> {
            if (player.inv.getSelectedItem() != selectedItem) return;

            double damage = Double.parseDouble(selectedItem.getAttributes().get("damage").toString());
            double range = Double.parseDouble(selectedItem.getAttributes().get("range").toString());

            launchAttack(mouseHandler, damage, range);

            selectedItem.delayedSetAnimationRotation(150, 60);
            selectedItem.delayedChangeAnimationBulk(-0.175, 120);

            // RETURN TO ORIGINAL POSITION
            scheduleAnimation(60, () -> {

                if (player.inv.getSelectedItem() != selectedItem) {
                    return;
                }

                selectedItem.delayedSetAnimationRotation(-75, 70);
                selectedItem.delayedMoveAnimationY(30, 30);
                selectedItem.delayedMoveAnimationX(-15, 30);
            });
        });
    }

    private static void launchAttack(MouseHandler mouseHandler, double damage, double range) {
        double XComponent;
        double YComponent;

        PlayerAttributes.KnockbackDirection direction;

        direction = PlayerAttributes.KnockbackDirection.valueOf(player.stats.getWalkingDirection().toString()).getOppositeDirection();

        switch (direction) {
            case LEFT -> {
                XComponent = range / 2;
                YComponent = 0;
            }
            case RIGHT -> {
                XComponent = -range;
                YComponent = 0;
            }
            case UP -> {
                XComponent = 0;
                YComponent = range / 2;
            }
            case DOWN -> {
                XComponent = 0;
                YComponent = -range;
            }
            default -> {
                XComponent = range / 2;
                YComponent = 0;
            }
        }

        int attackX = (int) (player.getX() / scale + XComponent);
        int attackY = (int) (player.getY() / scale + YComponent);

//        Box attack = addBox(attackX, attackY);
        int boxCount = 1;
//        int boxCount = lineAddBox(attackX, attackY, Math.max((int) (Math.ceil(range / 20) * clamp(abs(XComponent), 0, 1)), 1),
//            Math.max((int) (Math.ceil(range / 20) * clamp(abs(YComponent), 0, 1)), 1), "", LineAddBoxModes.FILL);
//        editLastBox(EditBoxKeys.COLOR, "#DEDE40", boxCount);
        editLastBox(EditBoxKeys.TEXTURE, "invisible", boxCount);
        editLastBox(EditBoxKeys.HAS_COLLISION, "true", boxCount);
        editLastBox(EditBoxKeys.INTERACTS_WITH_PLAYER, "false", boxCount);
        editLastBox(EditBoxKeys.EFFECT, "{damage, " + Math.max(Math.ceil(Math.floor(Math.pow(damage, 1.1)) * (player.attr.getStrength() * 10/100 + 1)), 1) + ", 1000, normal, 0}", boxCount);

        List<Box> boxes = getAllBoxes();
        List<Box> attack = new ArrayList<>(List.of());

        for (int i = 0; i < boxCount; i++) {
            Box currentBox = boxes.get(boxes.size() - 1 - i);

            attack.add(currentBox); // Add box to list to be deleted
            isSpaceOccupied(attackX, attackY, currentBox); // Handle damage detection
        }

        scheduleAnimation(60, () -> {
            for (int i = 0; i < boxCount; i++) {
                deleteBox(attack.get(i));
            }
        });
    }
}
