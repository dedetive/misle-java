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
    private static final ArrayList<NPC> talkableNPCList = new ArrayList<>();
    private boolean talkable;
    private static final ArrayList<NPC> selectedNPCList = new ArrayList<>();

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
        this.talkable = false;

        addBoxToCache(this);
    }

    public void setTalkable(boolean talkable) {
        if (talkable && !this.talkable) talkableNPCList.add(this);
        else if (!talkable && this.talkable) talkableNPCList.remove(this);

        this.talkable = talkable;
    }

    public static ArrayList<NPC> getTalkableNPCList() {
        return talkableNPCList;
    }

    public void setSelected(boolean selected) {
        if (selected) selectedNPCList.add(this);
        else selectedNPCList.remove(this);
    }

    public static ArrayList<NPC> getSelectedNPCList() {
        return selectedNPCList;
    }
}
