package com.ded.misle.npcs;

import com.ded.misle.PhysicsEngine;
import com.ded.misle.boxes.Box;

import java.awt.*;
import java.util.ArrayList;

import static com.ded.misle.boxes.BoxHandling.addBoxToCache;
import static com.ded.misle.player.PlayerAttributes.KnockbackDirection.NONE;

public class NPC extends Box {
    private static final ArrayList<NPC> selectedNPCs = new ArrayList<>();
    private static final ArrayList<NPC> dialogNPCs = new ArrayList<>();
    private static final ArrayList<NPC> shopNPCs = new ArrayList<>();
    private static final ArrayList<NPC> interactableNPCs = new ArrayList<>();
    private int dialogID;
    public enum InteractionType {
        NONE,
        DIALOG,
        SHOP
    }

    public NPC(double x, double y, InteractionType interactionType) {
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

        if (interactionType != InteractionType.NONE) {
            interactableNPCs.add(this);
        }
        switch (interactionType) {
            case SHOP -> shopNPCs.add(this);
            case DIALOG -> dialogNPCs.add(this);
        }

        addBoxToCache(this);
    }

    public void setSelected(boolean selected) {
        if (selected) selectedNPCs.add(this);
        else selectedNPCs.remove(this);
    }

    public static ArrayList<NPC> getSelectedNPCs() {
        return selectedNPCs;
    }

    public static ArrayList<NPC> getInteractableNPCs() {
        return interactableNPCs;
    }

    public static ArrayList<NPC> getDialogNPCs() {
        return dialogNPCs;
    }

    public void setDialogID(int dialogID) {
        this.dialogID = dialogID;        // Dialog ID should never be 0
    }

    public int getDialogID() {
        return dialogID;
    }
}
