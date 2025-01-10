package com.ded.misle.world.player;

import com.ded.misle.world.boxes.Box;
import com.ded.misle.world.boxes.BoxHandling;
import com.ded.misle.world.boxes.HPBox;
import com.ded.misle.input.MouseHandler;
import com.ded.misle.items.Item;

import javax.swing.*;

import static com.ded.misle.core.GamePanel.player;
import static com.ded.misle.Launcher.scale;
import static com.ded.misle.core.PhysicsEngine.isPixelOccupied;
import static com.ded.misle.world.boxes.BoxHandling.*;
import static com.ded.misle.world.player.PlayerAttributes.KnockbackDirection.NONE;
import static com.ded.misle.renderer.PlayingRenderer.isFacingRight;
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

            mouseHandler.updateCurrentMouseRotation();

            double attackRange = Double.parseDouble(selectedItem.getAttributes().get("range").toString());
            double attackDirection = clamp(isFacingRight, -0.5, 0.5) * 2;
            double attackAngleRadians = Math.toRadians(mouseHandler.getRelativeMouseRotation());
            double attackXComponent = Math.cos(attackAngleRadians) * attackRange;
            double attackYComponent = Math.sin(attackAngleRadians) * attackRange;

            PlayerAttributes.KnockbackDirection direction;

            if (mouseHandler.getRelativeMouseRotation() >= 0 && mouseHandler.getRelativeMouseRotation() < 60) direction = PlayerAttributes.KnockbackDirection.LEFT;
            else if (mouseHandler.getRelativeMouseRotation() >= 60 && mouseHandler.getRelativeMouseRotation() < 120) direction = PlayerAttributes.KnockbackDirection.UP;
            else if (mouseHandler.getRelativeMouseRotation() >= 120 && mouseHandler.getRelativeMouseRotation() < 240) direction = PlayerAttributes.KnockbackDirection.RIGHT;
            else if (mouseHandler.getRelativeMouseRotation() >= 240 && mouseHandler.getRelativeMouseRotation() < 330) direction = PlayerAttributes.KnockbackDirection.DOWN;
            else if (mouseHandler.getRelativeMouseRotation() >= 330 && mouseHandler.getRelativeMouseRotation() < 360) direction = PlayerAttributes.KnockbackDirection.RIGHT;
            else {
                direction = NONE;
            }
            double attackX = player.getX() / scale + attackXComponent;
            double attackY = player.getY() / scale + attackYComponent;

            Box attack = addBox(attackX, attackY);
//            editBox(attack, BoxHandling.EditBoxKeys.COLOR, "#DEDE40");
            editBox(attack, EditBoxKeys.TEXTURE, "invisible");
            editBox(attack, EditBoxKeys.HAS_COLLISION, "true");
            editBox(attack, EditBoxKeys.INTERACTS_WITH_PLAYER, "false");
            editBox(attack, EditBoxKeys.EFFECT, "{damage, " + selectedItem.getAttributes().get("damage") + ", 1000, normal, 0}");
            isPixelOccupied(attack, attackRange, 10, direction);

            selectedItem.delayedSetAnimationRotation(150, 60);
            selectedItem.delayedChangeAnimationBulk(-0.175, 120);

            // RETURN TO ORIGINAL POSITION
            scheduleAnimation(60, () -> {
                deleteBox(attack);

                if (player.inv.getSelectedItem() != selectedItem) {
                    return;
                }

                selectedItem.delayedSetAnimationRotation(-75, 70);
                selectedItem.delayedMoveAnimationY(30, 30);
                selectedItem.delayedMoveAnimationX(-15, 30);
            });
        });
    }
}
