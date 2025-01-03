package com.ded.misle.player;

import com.ded.misle.boxes.Box;
import com.ded.misle.boxes.BoxHandling;
import com.ded.misle.boxes.HPBox;
import com.ded.misle.items.Item;

import javax.swing.*;

import static com.ded.misle.GamePanel.player;
import static com.ded.misle.Launcher.scale;
import static com.ded.misle.PhysicsEngine.isPixelOccupied;
import static com.ded.misle.boxes.BoxHandling.*;
import static com.ded.misle.renderer.PlayingRenderer.isFacingRight;
import static java.lang.Math.abs;
import static java.lang.Math.clamp;

public class HandItemAnimator {
    private static void scheduleAnimation(int delay, Runnable action) {
        Timer timer = new Timer(delay, evt -> action.run());
        timer.setRepeats(false);
        timer.start();
    }

    public static void animateClaw() {
        // PREPARATION (move claw up and swing back)
        Item selectedItem = player.inv.getSelectedItem();

        selectedItem.delayedSetAnimationRotation(-75, 180);
        selectedItem.delayedChangeAnimationBulk(0.175, 180);
        selectedItem.delayedMoveAnimationY(-30, 70);
        selectedItem.delayedMoveAnimationX(15, 70);

        // ATTACK (swing forward)
        scheduleAnimation(215, () -> {
            if (player.inv.getSelectedItem() != selectedItem) return;

            double attackRange = Double.parseDouble(selectedItem.getAttributes().get("range").toString());
            double attackDirection = clamp(isFacingRight, -0.5, 0.5);
            PlayerAttributes.KnockbackDirection direction;
            if (attackDirection >= 0) direction = PlayerAttributes.KnockbackDirection.LEFT;
            else direction = PlayerAttributes.KnockbackDirection.RIGHT;
            double attackX = player.getX() / scale + attackRange * attackDirection;
            double attackY = player.getY() / scale;

            Box attack = addBox(attackX, attackY);
//            editBox(attack, BoxHandling.EditBoxKeys.COLOR, "#DEDE40");
            editBox(attack, EditBoxKeys.TEXTURE, "invisible");
            editBox(attack, EditBoxKeys.HAS_COLLISION, "true");
            editBox(attack, EditBoxKeys.INTERACTS_WITH_PLAYER, "false");
            for (HPBox box : getHPBoxesInRange(attackX, attackY, 12.5 * abs(attackRange * attackDirection))) {
                if (box instanceof Player) continue;
                boolean result = isPixelOccupied(box, 12.5 * abs(attackRange * attackDirection), 10, direction);
                if (result) {
                    box.takeDamage(Double.parseDouble(String.valueOf(selectedItem.getAttributes().get("damage"))), "normal", new String[]{}, direction);
                }
            }

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
