package com.ded.misle.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A timer system that executes {@link ActionListener}s based on a turn-based counter.
 * Each timer is scheduled to trigger after a set number of turns and can optionally repeat.
 * Timers are executed when {@link #executeAllDueTimers()} is called, typically once per turn.
 */
public class TurnTimer {
    private static final ArrayList<TurnTimer> queue = new ArrayList<>();
    private static int turnNum = 0;

    private int executionTurn;
    private final int turns;
    private final ActionListener listener;
    private boolean repeats;
    private int timesTriggered = 0;
    private int stopsAt = 0;

    /**
     * Creates a one-time timer that triggers after a given number of turns.
     *
     * @param turns    Number of turns to wait before triggering.
     * @param listener The action to execute when the timer triggers.
     */
    public TurnTimer(int turns, ActionListener listener) {
        this(turns, listener, false);
    }

    /**
     * Creates a timer that triggers after a given number of turns.
     * Can be configured to repeat.
     *
     * @param turns    Number of turns to wait before triggering.
     * @param listener The action to execute when the timer triggers.
     * @param repeats  Whether the timer should repeat after being triggered.
     */
    public TurnTimer(int turns, ActionListener listener, boolean repeats) {
        if (turns < 0) throw new IllegalArgumentException("Negative turns are not allowed!");

        this.turns = turns;
        this.repeats = repeats;
        this.listener = listener;
        this.executionTurn = turnNum + turns;
    }

    /**
     * Creates and starts a one-time timer.
     *
     * @param turns    Number of turns to wait before triggering.
     * @param listener The action to execute.
     * @return The created {@code TurnTimer} instance.
     */
    public static TurnTimer schedule(int turns, ActionListener listener) {
        var timer = new TurnTimer(turns, listener, false);
        timer.start();
        return timer;
    }

    /**
     * Creates and starts a timer that may repeat.
     *
     * @param turns    Number of turns to wait before triggering.
     * @param listener The action to execute.
     * @param repeats  Whether the timer should repeat after triggering.
     * @return The created {@code TurnTimer} instance.
     */
    public static TurnTimer schedule(int turns, ActionListener listener, boolean repeats) {
        var timer = new TurnTimer(turns, listener, repeats);
        timer.start();
        return timer;
    }

    /**
     * Starts the timer and adds it to the queue for execution.
     */
    public void start() {
        queue.add(this);
    }

    /**
     * Executes all timers that are due for the current turn.
     * Should be called once per turn after game logic.
     */
    public static void executeAllDueTimers() {
        Iterator<TurnTimer> it = queue.iterator();
        while (it.hasNext()) {
            TurnTimer timer = it.next();
            if (timer.isTimerDue()) {
                ActionEvent e = new ActionEvent(TurnTimer.class, 0, timer.listener.getClass().getName());
                timer.listener.actionPerformed(e);

                if (timer.repeats) {
                    timer.executionTurn = turnNum + timer.turns;
                    timer.timesTriggered++;
                    if (timer.stopsAt != 0 &&
                        timer.timesTriggered >= timer.stopsAt) {
                        it.remove();
                    }
                } else {
                    it.remove();
                }
            }
        }
    }

    /**
     * Checks if the timer is due to execute this turn.
     *
     * @return {@code true} if the timer should be triggered now.
     */
    public boolean isTimerDue() {
        return executionTurn == turnNum;
    }

    /**
     * Stops and removes the timer from the execution queue.
     */
    public void kill() {
        queue.remove(this);
    }

    /**
     * Enables or disables repetition of the timer after triggering.
     *
     * @param repeats {@code true} to make the timer repeat; {@code false} for one-time use.
     */
    public void setRepeats(boolean repeats) {
        this.repeats = repeats;
    }

    /**
     * Clears all timers and resets the turn counter to 0.
     * Typically used when restarting the game.
     */
    public static void reset() {
        queue.clear();
        turnNum = 0;
    }

    /**
     * Returns how many times this timer has triggered.
     *
     * @return Number of times the timer has executed.
     */
    public int getTimesTriggered() {
        return timesTriggered;
    }

    /**
     * Sets a limit on how many times the timer should trigger before stopping.
     * Only applies if the timer is repeating.
     *
     * @param stopsAt Number of times the timer is allowed to trigger. 0 means infinite.
     */
    public void setStopsAt(int stopsAt) {
        this.stopsAt = stopsAt;
    }

    /**
     * Increases the turn counter. Should be called once at the end of a turn.
     */
    protected static void increaseTurn() {
        turnNum++;
    }
}
