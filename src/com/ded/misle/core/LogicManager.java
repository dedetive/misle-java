package com.ded.misle.core;

import com.ded.misle.world.boxes.HPBox;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import static com.ded.misle.core.GamePanel.player;
import static com.ded.misle.world.boxes.HPBox.getHPBoxes;
import static com.ded.misle.world.enemies.EnemyAI.updateEnemyAI;

public abstract class LogicManager {

    public static class TurnTimer {
        private static final ArrayList<TurnTimer> queue = new ArrayList<>();
        private static int turnNum;

        private int executionTurn;
        private final int turns;
        private final ActionListener listener;
        private boolean repeats;

        TurnTimer(int turns, ActionListener listener) { this(turns, listener, false); }
        TurnTimer(int turns, ActionListener listener, boolean repeats) {
            if (turns < 0) throw new NumberFormatException("Negative turns not allowed!");

            this.turns = turns;
            this.repeats = repeats;
            this.listener = listener;
            this.executionTurn = turnNum + turns;
        }

        public void start() {
            queue.add(this);
        }
        public static void executeAllDueTimers() {
            Iterator<TurnTimer> it = queue.iterator();
            while (it.hasNext()) {
                TurnTimer timer = it.next();
                if (timer.isTimerDue()) {
                    ActionEvent e = new ActionEvent(TurnTimer.class, 0, timer.listener.getClass().getName());
                    timer.listener.actionPerformed(e);

                    if (timer.repeats) {
                        timer.executionTurn = turnNum + timer.turns;
                    } else {
                        it.remove();
                    }
                }
            }
        }
        public boolean isTimerDue() {
            return executionTurn == turnNum;
        }
        public void kill() {
            queue.remove(this);
        }
        public void setRepeats(boolean repeats) {
            this.repeats = repeats;
        }
        public static void reset() {
            queue.clear();
        }
        protected static void increaseTurn() {
            turnNum++;
        }
    }

    private static boolean pendingTurn = false;

    public static void requestNewTurn() {
        pendingTurn = true;
    }

    public static void updateIfNeeded() {
        if (pendingTurn) {
            updateTurn();
            pendingTurn = false;

            TurnTimer.executeAllDueTimers();
            TurnTimer.increaseTurn();
        }
    }

    private static void updateTurn() {
        long currentTime = System.currentTimeMillis();

        player.attr.checkIfLevelUp();

        updateEnemyAI();

        for (HPBox box : getHPBoxes()) {
            box.updateRegenerationHP(currentTime);
        }
    }
}
