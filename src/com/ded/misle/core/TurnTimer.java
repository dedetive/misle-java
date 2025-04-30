package com.ded.misle.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A timer system that executes {@link ActionListener}s based on a turn-based counter.
 * Each timer is scheduled to trigger after a set number of turns and can optionally repeat.
 * Timers are executed when {@link #executeAllDueTimers()} is called, typically once per turn.
 */
public class TurnTimer {
    /**
     * Global queue of all active timers scheduled for execution.
     * Timers are added when started and removed when executed or manually killed.
     */
    private static final List<TurnTimer> queue = Collections.synchronizedList(new ArrayList<>());

    /**
     * The current global turn count.
     * All timers calculate their execution turn based on this value.
     */
    private static int turnNum = 0;

    /**
     * Indicates whether this timer should be automatically removed
     * when the room changes.
     */
    private boolean roomScoped = false;

    /**
     * The specific turn number when this timer is scheduled to execute next.
     */
    private int executionTurn;

    /**
     * The number of turns to wait between executions.
     * Used both for initial scheduling and repeating logic.
     */
    private final int turns;

    /**
     * The action to be executed when the timer triggers.
     */
    private final ActionListener listener;

    /**
     * Whether this timer should repeat after triggering.
     */
    private boolean repeats;

    /**
     * How many times this timer has already been triggered.
     */
    private int timesTriggered = 0;

    /**
     * Maximum number of times this timer is allowed to trigger.
     * A value of 0 means no limit (infinite repetition).
     */
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
                timer.forceExecution(it);
            }
        }
    }

    /**
     * Forces this timer to execute immediately, regardless of its scheduled turn.
     * This does remove it from the queue if it is not a repeating timer.
     * If the timer is repeating, reschedules the next execution unless {@code stopsAt} has been hit.
     */
    public void forceExecution() {
        forceExecution(queue.iterator());
    }

    /**
     * Forces this timer to execute immediately, regardless of its scheduled turn.
     * This does remove it from the queue if it is not a repeating timer.
     * If the timer is repeating, reschedules the next execution unless {@code stopsAt} has been hit.
     * Should only ever be used in {@link #executeAllDueTimers()}
     *
     * @param it Iterator used to execute all due timers.
     */
    private void forceExecution(Iterator<TurnTimer> it) {
        ActionEvent e = new ActionEvent(TurnTimer.class, ActionEvent.ACTION_PERFORMED, listener.getClass().getName());
        listener.actionPerformed(e);

        if (!repeats) {
            it.remove();
        } else {
            executionTurn = turnNum + turns;
            timesTriggered++;
            if (stopsAt != 0 &&
                timesTriggered >= stopsAt) {
                it.remove();
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
    public TurnTimer setRepeats(boolean repeats) {
        this.repeats = repeats;
        return this;
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
     * Sets whether the timer is dependent on the room. If true, the timer will be cleared
     * when the room changes. Used to ensure that timers tied to specific room entities are
     * safely removed when switching rooms.
     *
     * @param roomScoped true if the timer is dependent on the room, false otherwise.
     */
    public TurnTimer setRoomScoped(boolean roomScoped) {
        this.roomScoped = roomScoped;
        return this;
    }

    /**
     * Clears all timers from the queue that are marked as room-dependent (roomScoped).
     * This method is called when the room changes to ensure that timers tied to the old
     * room (such as enemy movement timers) are removed.
     */
    public static void clearRoomScopedTimers() {
        queue.removeIf(timer -> timer.roomScoped);
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
    public TurnTimer setStopsAt(int stopsAt) {
        this.stopsAt = stopsAt;
        return this;
    }

    /**
     * Increases the turn counter. Should be called once at the end of a turn.
     */
    protected static void increaseTurn() {
        turnNum++;
    }
}
