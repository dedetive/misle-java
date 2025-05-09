package com.ded.misle.world.effects;

import com.ded.misle.core.GamePanel;
import com.ded.misle.renderer.MainRenderer;
import com.ded.misle.world.player.Player;

import javax.swing.*;
import java.awt.*;

import com.ded.misle.world.boxes.Box;
import static com.ded.misle.core.GamePanel.gameState;
import static com.ded.misle.core.GamePanel.player;
import static com.ded.misle.renderer.MainRenderer.*;
import static com.ded.misle.world.WorldLoader.loadBoxes;
import static com.ded.misle.world.WorldLoader.unloadBoxes;
import static com.ded.misle.world.enemies.EnemyAI.clearBreadcrumbs;

public class Travel extends Effect {
    int roomID;
    Point coordinates;

    public Travel(int roomID, Point coordinates) {
        this.roomID = roomID;
        this.coordinates = coordinates;
    }

    @Override
    public void run(Box culprit, Box victim) {
        if (!(victim instanceof Player)) return;

        handleBoxTravel();
    }

    private void handleBoxTravel() {
        fadeIn();
        gameState = GamePanel.GameState.FROZEN_PLAYING;
        Timer fadingIn = new Timer(75, e -> {
            if (isFading == MainRenderer.FadingState.FADED) {

                player.pos.setRoomID(roomID);

                player.setX(coordinates.x);
                player.setY(coordinates.y);
                unloadBoxes();
                loadBoxes();
                clearBreadcrumbs();

                Timer loadWait = new Timer(300, evt -> {
                    fadeOut();
                    gameState = GamePanel.GameState.PLAYING;
                });
                loadWait.setRepeats(false);
                loadWait.start();

                ((Timer) e.getSource()).stop();
            }
        });
        fadingIn.setRepeats(true);
        fadingIn.start();
    }

    @Override
    public String toString() {
        return "Travel{" +
            "roomID=" + roomID +
            ", x=" + coordinates.x +
            ", y=" + coordinates.y +
            '}';
    }
}
