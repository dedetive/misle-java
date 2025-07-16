package com.ded.misle.input;

import static java.awt.event.KeyEvent.*;

public enum KeyDep {
    PAUSE(VK_ESCAPE),
    UP(VK_UP),
    DOWN(VK_DOWN),
    LEFT(VK_LEFT),
    RIGHT(VK_RIGHT),
    LEFT_MENU(VK_A),
    RIGHT_MENU(VK_D),
    DEBUG1(VK_OPEN_BRACKET),
    DEBUG2(VK_CLOSE_BRACKET),
    SCREENSHOT(VK_F2),
    INVENTORY(VK_E),
    DROP(VK_Q),
    CTRL(VK_CONTROL),
    SHIFT(VK_SHIFT),
    DODGE(VK_C),
    USE(VK_Z),
    EQUAL(VK_EQUALS),
    MINUS(VK_MINUS),
    GRID(VK_G),
    BACKSPACE(VK_BACK_SPACE),
    ENTER(VK_ENTER),
    NUM_0(VK_0),
    NUM_1(VK_1),
    NUM_2(VK_2),
    NUM_3(VK_3),
    NUM_4(VK_4),
    NUM_5(VK_5),
    NUM_6(VK_6),
    NUM_7(VK_7),
    PLANNING_TOGGLE(VK_SPACE),
    PLANNING_CONFIRM(VK_ENTER), /*
            TODO: unused while this key handler system is trash. please refactor this whole thing
                this absolute buffoon cannot even handle two keys with the same key code
         */;

    public final int keyCode;

    KeyDep(int keyEvent) {
        KeyHandlerDep.keyCodes.put(this, keyEvent);
        keyCode = keyEvent;
    }
}
