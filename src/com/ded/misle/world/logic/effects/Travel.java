package com.ded.misle.world.logic.effects;

import com.ded.misle.game.GamePanel;
import com.ded.misle.renderer.Fader;
import com.ded.misle.renderer.MainRenderer;
import com.ded.misle.world.entities.player.Player;

import javax.swing.*;
import java.awt.*;

import com.ded.misle.world.boxes.Box;

import static com.ded.misle.game.GamePanel.*;
import static com.ded.misle.renderer.MainRenderer.*;
import static com.ded.misle.world.data.WorldLoader.loadBoxes;
import static com.ded.misle.world.data.WorldLoader.unloadBoxes;
import static com.ded.misle.world.entities.enemies.EnemyAI.clearBreadcrumbs;

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
        pixelate(2000L, 110);
        gameState = GamePanel.GameState.FROZEN_PLAYING;
        Timer fadingIn = new Timer(1750, e -> {

                player.pos.setRoomID(roomID);

                unloadBoxes();
                loadBoxes();
                player.setX(coordinates.x);
                player.setY(coordinates.y);
                clearBreadcrumbs();

                Timer loadWait = new Timer(300, evt -> {
                    unpixelate(2000);
                    gameState = GamePanel.GameState.PLAYING;
                });
                loadWait.setRepeats(false);
                loadWait.start();

                ((Timer) e.getSource()).stop();
        });
        fadingIn.setRepeats(false);
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
