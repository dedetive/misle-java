package com.ded.misle.world.entities.player;

import com.ded.misle.world.data.items.Item;
import com.ded.misle.world.data.Direction;
import com.ded.misle.world.data.TilePattern;
import com.ded.misle.world.entities.player.attributes.Strength;
import com.ded.misle.world.logic.attacks.Range;
import com.ded.misle.world.logic.attacks.Attacker;

import javax.swing.*;

import java.awt.*;
import java.util.Objects;
import java.util.Optional;

import static com.ded.misle.game.GamePanel.player;

public class HandItemAnimator {
    private final Attacker attacker = new Attacker();

    public void update() {
        update(1f);
    }

    public void update(float damageMultiplier) {
        Item selectedItem = player.inv.getSelectedItem();

        double damage = 1;
        Range range = Range.getDefaultRange();

        if (selectedItem != null && Objects.equals(selectedItem.getType(), "weapon")) {
            String strRange = selectedItem.getAttributes().containsKey("range")
                ? (selectedItem.getAttributes().get("range").toString())
                : "";
            if (strRange.isEmpty()) {
                attacker.invalidate();
                return;
            }
            range = Range.toRange(strRange);

            damage = Double.parseDouble(selectedItem.getAttributes().get("damage").toString());
        }

        attacker.setRange(range);
        attacker.setDamage(damage);

        Optional<Strength> strength = player.attributeController.get(Strength.class);
        strength.ifPresent(value -> value.apply(attacker));

        attacker.setDamage(attacker.getDamage() * damageMultiplier);
    }

    public Range getRange(Direction direction) {
        update();

        attacker.setRange((Range)
            switch (direction) {
                case UP -> attacker.getRange().rotate(TilePattern.Rotation.DEG_90);
                case DOWN -> attacker.getRange().rotate(TilePattern.Rotation.DEG_270);
                case LEFT -> attacker.getRange().rotate(TilePattern.Rotation.DEG_180);
                default -> attacker.getRange();
            }
        );

        return attacker.getRange();
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

            attacker.attack(player.getPos(), player.getWalkingDirection());

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

    public void animateHand() {
        player.setWaiting(true);
        attacker.attack(player.getPos(), player.getWalkingDirection());
        scheduleAnimation(60, () -> player.setWaiting(false));
    }
}
