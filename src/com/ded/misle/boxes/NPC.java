package com.ded.misle.boxes;

import com.ded.misle.PhysicsEngine;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

import static com.ded.misle.GamePanel.tileSize;
import static com.ded.misle.boxes.BoxHandling.addBoxToCache;
import static com.ded.misle.boxes.BoxHandling.removeBoxFromCache;
import static com.ded.misle.player.PlayerAttributes.KnockbackDirection.NONE;

public class NPC extends Box {
    private static final HashMap<Integer, NPC> NPCList = new HashMap<>();

    public NPC(double x, double y) {
        this.setTexture("solid");
        this.setColor(new Color(0xFFFF00));
        this.setObjectType(PhysicsEngine.ObjectType.NPC);
        this.setHasCollision(true);
        this.setBoxScaleHorizontal(1);
        this.setBoxScaleVertical(1);
        this.setKnockbackDirection(NONE);
        this.setEffect(new String[]{""});
        this.setX(x);
        this.setY(y);

        NPCList.put(NPCList.size(), this);  // ID starts at 0
        addBoxToCache(this);
    }

    public static void clearNPCList() {
        for (NPC npc : NPCList.values()) {
            removeBoxFromCache(npc);
        }
        NPCList.clear();
    }
}
