package com.ded.misle.player;

import com.ded.misle.items.Item;

import javax.swing.*;

import static com.ded.misle.GamePanel.player;

public class HandItemAnimator {
    private static void scheduleAnimation(int delay, Runnable action) {
        Timer timer = new Timer(delay, evt -> action.run());
        timer.setRepeats(false);
        timer.start();
    }

    public static void animateClaw() {

        // PREPARATION (move claw up and swing back)
        Item selectedItem = player.inv.getSelectedItem();

        player.inv.getSelectedItem().delayedSetAnimationRotation(-75, 180);
        player.inv.getSelectedItem().delayedChangeAnimationBulk(0.175, 180);
        player.inv.getSelectedItem().delayedMoveAnimationY(-30, 70);
        player.inv.getSelectedItem().delayedMoveAnimationX(15, 70);

        // ATTACK (swing forward)
        scheduleAnimation(215, () -> {
            if (player.inv.getSelectedItem() != selectedItem) return;

            player.inv.getSelectedItem().delayedSetAnimationRotation(150, 60);
            player.inv.getSelectedItem().delayedChangeAnimationBulk(-0.175, 120);

            // RETURN TO ORIGINAL POSITION
            scheduleAnimation(60, () -> {
                if (player.inv.getSelectedItem() != selectedItem) {
                    return;
                }

                player.inv.getSelectedItem().delayedSetAnimationRotation(-75, 70);
                player.inv.getSelectedItem().delayedMoveAnimationY(30, 30);
                player.inv.getSelectedItem().delayedMoveAnimationX(-15, 30);
            });
        });
    }
}
