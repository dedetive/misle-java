package com.ded.misle.world.entities.player;

import com.ded.misle.items.Item;
import com.ded.misle.world.logic.attacks.Range;
import com.ded.misle.world.logic.attacks.WeaponAttacker;

import javax.swing.*;

import java.awt.*;

import static com.ded.misle.game.GamePanel.player;

public class HandItemAnimator {
    private final WeaponAttacker attacker = new WeaponAttacker();

    public void update() {


    }

    private void scheduleAnimation(int delay, Runnable action) {
        Timer timer = new Timer(delay, evt -> action.run());
        timer.setRepeats(false);
        timer.start();
    }

    public void animateClaw() {
        // PREPARATION (move claw up and swing back)
        Item selectedItem = player.inv.getSelectedItem();
        player.setWaiting(true);

        selectedItem.delayedSetAnimationRotation(-75, 180);
        selectedItem.delayedChangeAnimationBulk(0.175, 180);
        selectedItem.delayedMoveAnimationY(-30, 70);
        selectedItem.delayedMoveAnimationX(15, 70);

        // ATTACK (swing forward)
        scheduleAnimation(215, () -> {
            if (player.inv.getSelectedItem() != selectedItem) return;

            double damage = Double.parseDouble(selectedItem.getAttributes().get("damage").toString());
            Range range = Range.toRange(selectedItem.getAttributes().get("range").toString());
            Point origin = new Point(player.getX(), player.getY());

            attacker.setDamage(damage);
            attacker.setRange(range);
            attacker.attack(origin, player.stats.getWalkingDirection());

            selectedItem.delayedSetAnimationRotation(150, 60);
            selectedItem.delayedChangeAnimationBulk(-0.175, 120);

            // RETURN TO ORIGINAL POSITION
            scheduleAnimation(60, () -> {
                player.setWaiting(false);

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
