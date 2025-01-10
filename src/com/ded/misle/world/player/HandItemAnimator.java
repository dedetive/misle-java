package com.ded.misle.world.player;

import com.ded.misle.world.boxes.Box;
import com.ded.misle.input.MouseHandler;
import com.ded.misle.items.Item;

import javax.swing.*;

import java.sql.SQLOutput;

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

            launchAttack(mouseHandler, Double.parseDouble(selectedItem.getAttributes().get("damage").toString()), Double.parseDouble(selectedItem.getAttributes().get("range").toString()));

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
        mouseHandler.updateCurrentMouseRotation();

        double angleRadians = Math.toRadians(mouseHandler.getRelativeMouseRotation());
        double XComponent = Math.cos(angleRadians) * range;
        double YComponent = Math.sin(angleRadians) * range;

        PlayerAttributes.KnockbackDirection direction;

        if (mouseHandler.getRelativeMouseRotation() >= 0 && mouseHandler.getRelativeMouseRotation() < 60) direction = PlayerAttributes.KnockbackDirection.LEFT;
        else if (mouseHandler.getRelativeMouseRotation() >= 60 && mouseHandler.getRelativeMouseRotation() < 120) direction = PlayerAttributes.KnockbackDirection.UP;
        else if (mouseHandler.getRelativeMouseRotation() >= 120 && mouseHandler.getRelativeMouseRotation() < 240) direction = PlayerAttributes.KnockbackDirection.RIGHT;
        else if (mouseHandler.getRelativeMouseRotation() >= 240 && mouseHandler.getRelativeMouseRotation() < 330) direction = PlayerAttributes.KnockbackDirection.DOWN;
        else if (mouseHandler.getRelativeMouseRotation() >= 330 && mouseHandler.getRelativeMouseRotation() < 360) direction = PlayerAttributes.KnockbackDirection.RIGHT;
        else {
            direction = NONE;
        }
        double attackX = player.getX() / scale + XComponent;
        double attackY = player.getY() / scale + YComponent;

        Box attack = addBox(attackX, attackY);
//            editBox(attack, BoxHandling.EditBoxKeys.COLOR, "#DEDE40");
        editBox(attack, EditBoxKeys.TEXTURE, "invisible");
        editBox(attack, EditBoxKeys.HAS_COLLISION, "true");
        editBox(attack, EditBoxKeys.INTERACTS_WITH_PLAYER, "false");
        editBox(attack, EditBoxKeys.EFFECT, "{damage, " + damage + ", 1000, normal, 0}");
        isPixelOccupied(attack, range, 10, direction);
        System.out.println(direction);

        scheduleAnimation(60, () -> {
            deleteBox(attack);
        });
    }
}
