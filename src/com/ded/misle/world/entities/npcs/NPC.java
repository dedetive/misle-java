package com.ded.misle.world.entities.npcs;

import com.ded.misle.core.LanguageManager;
import com.ded.misle.world.logic.PhysicsEngine;
import com.ded.misle.world.entities.HPBox;

import java.awt.*;
import java.util.ArrayList;

import static com.ded.misle.renderer.DialogRenderer.resetLetterDisplay;
import static com.ded.misle.world.entities.npcs.NPCDialog.endDialog;

public class NPC extends HPBox {
    private static final ArrayList<NPC> selectedNPCs = new ArrayList<>();
    private static final ArrayList<NPC> dialogNPCs = new ArrayList<>();
    private static final ArrayList<NPC> shopNPCs = new ArrayList<>();
    private static final ArrayList<NPC> interactableNPCs = new ArrayList<>();
    private int dialogID;
    private int dialogIndex;
    private int dialogMaxIndex;
    public String name;
    public Color nameColor;
    public enum InteractionType {
        NONE,
        DIALOG,
        SHOP
    }

    public NPC(int x, int y, InteractionType interactionType) {
        super(x, y);
        this.setColor(new Color(0xFFFF00));
        this.setObjectType(PhysicsEngine.ObjectType.NPC);
        this.setCollision(true);
        this.name = "NPC";
        this.nameColor = new Color(0xFFFFFF);
        this.setMaxHP(20);

        if (interactionType != InteractionType.NONE) {
            interactableNPCs.add(this);
        }
        switch (interactionType) {
            case SHOP -> shopNPCs.add(this);
            case DIALOG -> dialogNPCs.add(this);
        }
    }

    public void setSelected(boolean selected) {
        if (selected) selectedNPCs.add(this);
        else selectedNPCs.remove(this);
    }

    // Getters

    public static ArrayList<NPC> getSelectedNPCs() {
        return selectedNPCs;
    }

    public static ArrayList<NPC> getInteractableNPCs() {
        return interactableNPCs;
    }

    public static ArrayList<NPC> getDialogNPCs() {
        return dialogNPCs;
    }

    public static void clearNPCs() {
        selectedNPCs.clear();
        dialogNPCs.clear();
        interactableNPCs.clear();
        shopNPCs.clear();
    }

    public void deleteNPC() {
        selectedNPCs.remove(this);
        dialogNPCs.remove(this);
        interactableNPCs.remove(this);
        shopNPCs.remove(this);
    }

    // Dialog related

    public void setDialogID(int dialogID) {
        this.dialogID = dialogID;
        try {
            this.dialogMaxIndex = Integer.parseInt(LanguageManager.getText("DIALOG_" + dialogID));
        } catch (NumberFormatException e) {
            this.dialogMaxIndex = 0;
        }
    }

    public int getDialogID() {
        return dialogID;
    }

    public void resetDialogIndex() {
        this.dialogIndex = 0;
    }

    public void setDialogIndex(int dialogIndex) {
        this.dialogIndex = dialogIndex;
    }

    public void incrementDialogIndex() {
        this.dialogIndex++;
        resetLetterDisplay();
        if (this.dialogIndex >= this.dialogMaxIndex + 1) {
            this.resetDialogIndex();
            endDialog();
        }
    }

    public int getDialogIndex() {
        return dialogIndex;
    }
}
