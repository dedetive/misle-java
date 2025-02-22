package com.ded.misle.world.player;

import com.ded.misle.world.boxes.Box;
import com.ded.misle.input.MouseHandler;
import com.ded.misle.items.Item;
import com.ded.misle.world.boxes.BoxHandling;

import javax.swing.*;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

import static com.ded.misle.Launcher.heldItemFollowsMouse;
import static com.ded.misle.core.GamePanel.player;
import static com.ded.misle.Launcher.scale;
import static com.ded.misle.core.PhysicsEngine.isPixelOccupied;
import static com.ded.misle.world.boxes.BoxHandling.*;
import static com.ded.misle.world.player.PlayerAttributes.KnockbackDirection.NONE;
import static java.lang.Math.abs;
import static java.lang.Math.clamp;

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

        if (heldItemFollowsMouse) {
            mouseHandler.updateCurrentMouseRotation();

            double angleRadians = Math.toRadians(mouseHandler.getRelativeMouseRotation());

            XComponent = Math.cos(angleRadians) * range;
            YComponent = Math.sin(angleRadians) * range;

            if (mouseHandler.getRelativeMouseRotation() >= 0 && mouseHandler.getRelativeMouseRotation() < 60)
                direction = PlayerAttributes.KnockbackDirection.LEFT;
            else if (mouseHandler.getRelativeMouseRotation() >= 60 && mouseHandler.getRelativeMouseRotation() < 120)
                direction = PlayerAttributes.KnockbackDirection.UP;
            else if (mouseHandler.getRelativeMouseRotation() >= 120 && mouseHandler.getRelativeMouseRotation() < 240)
                direction = PlayerAttributes.KnockbackDirection.RIGHT;
            else if (mouseHandler.getRelativeMouseRotation() >= 240 && mouseHandler.getRelativeMouseRotation() < 330)
                direction = PlayerAttributes.KnockbackDirection.DOWN;
            else if (mouseHandler.getRelativeMouseRotation() >= 330 && mouseHandler.getRelativeMouseRotation() < 360)
                direction = PlayerAttributes.KnockbackDirection.RIGHT;
            else {
                direction = NONE;
            }
        } else {
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
        }

        double attackX = player.getX() / scale + XComponent;
        double attackY = player.getY() / scale + YComponent;

//        Box attack = addBox(attackX, attackY);
        int boxCount = lineAddBox(attackX, attackY, Math.max((int) (Math.ceil(range / 20) * clamp(abs(XComponent), 0, 1)), 1),
            Math.max((int) (Math.ceil(range / 20) * clamp(abs(YComponent), 0, 1)), 1), "", LineAddBoxModes.FILL);
//        editLastBox(EditBoxKeys.COLOR, "#DEDE40", boxCount);
        editLastBox(EditBoxKeys.TEXTURE, "invisible", boxCount);
        editLastBox(EditBoxKeys.HAS_COLLISION, "true", boxCount);
        editLastBox(EditBoxKeys.INTERACTS_WITH_PLAYER, "false", boxCount);
        editLastBox(EditBoxKeys.EFFECT, "{damage, " + Math.max(Math.ceil(Math.floor(Math.pow(damage, 1.1)) * (player.attr.getStrength() * 10/100 + 1)), 1) + ", 1000, normal, 0}", boxCount);

        List<Box> boxes = getAllBoxes();
        List<Box> attack = new ArrayList<>(List.of());

        for (int i = 0; i < boxCount; i++) {
            attack.add(boxes.get(boxes.size() - 1 - i));
            isPixelOccupied(boxes.get(boxes.size() - 1 - i), range, 15, direction);
        }

        scheduleAnimation(60, () -> {
            for (int i = 0; i < boxCount; i++) {
                deleteBox(attack.get(i));
            }
        });
    }
}
