package com.ded.misle.world.logic;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A timer system that executes {@link ActionListener}s based on a turn-based counter.
 * Each timer is scheduled to trigger after a set number of turns and can optionally repeat.
 * Timers are executed when {@link #executeAllDueTimers()} is called, typically once per turn.
 *
 *  <p>Example usage:
 *  <pre>
 *  TurnTimer.schedule(3, e -> {
 *      System.out.println("Triggered after 3 turns");
 *  });
 *  </pre>
 */
public class TurnTimer {
    /**
     * Global queue of all active timers scheduled for execution.
     * Timers are added when started and removed when executed or manually killed.
     */
    private static final List<TurnTimer> queue = Collections.synchronizedList(new ArrayList<>() {
        @Override public boolean add(TurnTimer e) {
            return e != null && e.listener != null && super.add(e);
        }
    });

    /**
     * The current global turn count.
     * This is incremented with {@link #increaseTurn()} and used to determine when timers trigger.
     */
    private static int turnNum = 0;

    /**
     * Indicates whether the timer has been already started.
     * Should be false when it's not inside {@link #queue}, and true when it is.
     */
    private boolean started = false;

    /**
     * Indicates whether this timer should be automatically removed
     * when the room changes. This is useful for timers tied to
     * specific rooms or entities (like NPC movement timers).
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
     * The action to be executed when the timer triggers.
     */
    private final ActionListener listener;

    /**
     * An action that is triggered exactly once when the {@code TurnTimer} finishes naturally.
     * <p>
     * This listener is called only when the timer:
     * <ul>
     *   <li>runs with no repeats and reaches the end, or</li>
     *   <li>runs with repeats and {@code stopsAt} is reached.</li>
     * </ul>
     * <p>
     * It is <b>not</b> called if the timer is forcibly terminated using {@code kill()}.
     */
    private ActionListener onFinish;

    /**
     * The action to be executed immediately after the main listener triggers,
     * but before the timer is potentially rescheduled or removed.
     * <p>
     * Unlike {@link #onFinish}, this listener is called after every execution,
     * including repeated executions and forced executions.
     */
    private ActionListener afterExecution;

    /**
     * Creates a one-time timer that triggers after a given number of turns.
     *
     * @param turns    Number of turns to wait before triggering.
     * @param listener The action to execute when the timer triggers.
     */
    public TurnTimer(int turns, ActionListener listener) {
        this(turns, false, listener);
    }

    /**
     * Creates a timer that triggers after a given number of turns.
     * Can be configured to repeat.
     *
     * @param turns    Number of turns to wait before triggering.
     * @param repeats  Whether the timer should repeat after being triggered.
     * @param listener The action to execute when the timer triggers.
     */
    public TurnTimer(int turns, boolean repeats, ActionListener listener) {
        if (turns <= 0) throw new IllegalArgumentException("Non-positive turns are not allowed!");

        this.turns = turns;
        this.repeats = repeats;
        this.listener = listener;
    }

    /**
     * Creates and starts a one-time timer.
     * It is exactly equal to creating a TurnTimer and running {@link #start()} on it.
     *
     * @param turns    Number of turns to wait before triggering.
     * @param listener The action to execute.
     * @return The created {@code TurnTimer} instance.
     */
    public static TurnTimer schedule(int turns, ActionListener listener) {
        var timer = new TurnTimer(turns, false, listener);
        timer.start();
        return timer;
    }

    /**
     * Creates and starts a timer that may repeat.
     *
     * @param turns    Number of turns to wait before triggering.
     * @param repeats  Whether the timer should repeat after triggering.
     * @param listener The action to execute.
     * @return The created {@code TurnTimer} instance.
     */
    public static TurnTimer schedule(int turns, boolean repeats, ActionListener listener) {
        var timer = new TurnTimer(turns, repeats, listener);
        timer.start();
        return timer;
    }

    /**
     * Starts the timer and adds it to the queue for execution.
     * If the timer was already started, this method does nothing.
     * @apiNote Calling this method on an already-started timer has no effect.
     * To reinitialize a timer, use {@link #restart()} instead.
     *
     * @return This {@code TurnTimer} instance for chaining.
     */
    public TurnTimer start() {
        if (started) return this;
        started = true;
        queue.add(this);
        updateExecutionTurn();
        return this;
    }

    /**
     * Resets the timer to its constructor state, and then starts it.
     * @see #reset()
     * @see #start()
     */
    public TurnTimer restart() {
        this.reset().start();

        return this;
    }

    /**
     * Resets the timer to its constructor state.
     * This removes it from the queue and resets the trigger count.
     * It does not reset the number of turns or the assigned listener.
     *
     * @return This {@code TurnTimer} instance for chaining.
     */
    public TurnTimer reset() {
        started = false;
        timesTriggered = 0;
        queue.remove(this);

        return this;
    }

    /**
     * Stops and removes the timer from the execution queue.
     * <p>
     * This method prevents the timer from executing or completing naturally,
     * and as such, the {@code onFinish} listener (if set) will not be called.
     */
    public void kill() {
        queue.remove(this);
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
     * Forces this timer to execute immediately, regardless of its scheduled turn.
     * This does remove it from the queue if it is not a repeating timer.
     * If the timer is repeating, reschedules the next execution unless {@code stopsAt} has been hit.
     * <p>
     * This method also triggers {@link #afterExecution} after the main listener.
     */
    public void forceExecution() {
        forceExecution(queue.iterator(), true);
    }
    /**
     * Forces this timer to execute immediately, regardless of its scheduled turn.
     * <p>
     * Executes the main listener action and, if present, the {@link #afterExecution} listener
     * after it. If the timer is non-repeating, or has hit its {@code stopsAt} limit, it is removed.
     *
     * @param it        The iterator used during timer processing.
     * @param removeNow Whether to remove immediately via {@link #kill()}, or via {@link Iterator#remove()}.
     */
    private void forceExecution(Iterator<TurnTimer> it, boolean removeNow) {
        ActionEvent e = new ActionEvent(TurnTimer.class, ActionEvent.ACTION_PERFORMED, listener.getClass().getName());
        listener.actionPerformed(e);
        if (afterExecution != null) afterExecution.actionPerformed(e);

        if (!repeats) {
            cleanupAfterFinalExecution(it, removeNow, e);
        } else {
            this.updateExecutionTurn();
            timesTriggered++;
            if (stopsAt != 0 &&
                timesTriggered >= stopsAt) {
                cleanupAfterFinalExecution(it, removeNow, e);
            }
        }
    }

    /**
     * Updates the timer's next turn to be executed.
     */
    private void updateExecutionTurn() {
        this.executionTurn = turnNum + turns;
    }

    /**
     * Finalizes the execution of this timer when it is no longer meant to continue.
     * <p>
     * Removes the timer from the queue and calls {@link #onFinish} if set.
     * <p>
     * Note: {@link #afterExecution} is <b>not</b> triggered here, as it should
     * only run immediately after a normal execution.
     *
     * @param it        The iterator used during queue processing.
     * @param removeNow Whether to remove via {@link #kill()} (if true) or {@link Iterator#remove()} (if false).
     * @param e         The {@link ActionEvent} passed to {@code onFinish}.
     */
    private void cleanupAfterFinalExecution(Iterator<TurnTimer> it, boolean removeNow, ActionEvent e) {
        if (removeNow) kill();
        else it.remove();
        if (onFinish != null) onFinish.actionPerformed(e);
    }

    /**
     * Executes all timers that are due for the current turn.
     * Should be called once per turn after game logic.
     */
    private static void executeAllDueTimers() {
        List<TurnTimer> dueTimers;

        synchronized (queue) {
            dueTimers = queue.stream()
                .filter(TurnTimer::isTimerDue)
                .toList();
        }

        for (TurnTimer timer : dueTimers) {
            timer.forceExecution();
        }
    }

    /**
     * Clears all timers and resets the turn counter to 0.
     * Typically used when restarting the game.
     */
    public static void resetQueue() {
        queue.clear();
        turnNum = 0;
    }

    /**
     * Enables or disables repetition of the timer after triggering.
     * <p>
     * Note: {@link #afterExecution} is called after <i>every</i> trigger, including repeats.
     *
     * @param repeats {@code true} to make the timer repeat; {@code false} for one-time use.
     * @return this timer instance, for chaining
     */
    public TurnTimer setRepeats(boolean repeats) {
        this.repeats = repeats;
        return this;
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
     * Sets a listener to be executed once when this timer finishes naturally.
     * <p>
     * The {@code onFinish} action is called:
     * <ul>
     *   <li>when a non-repeating timer reaches its scheduled execution turn, or</li>
     *   <li>when a repeating timer completes and reaches its {@code stopsAt} limit.</li>
     * </ul>
     * It will <b>not</b> be called if the timer is terminated using {@link #kill()}.
     *
     * @param listener the {@code ActionListener} to execute on natural completion
     * @return this timer instance, for chaining
     */
    public TurnTimer setOnFinish(ActionListener listener) {
        this.onFinish = listener;
        return this;
    }

    /**
     * Sets a listener to execute immediately after the main timer action.
     * <p>
     * This listener is called after every execution of the timer, regardless of whether
     * it will repeat or stop afterward. It differs from {@link #onFinish}, which only
     * executes once when the timer finishes naturally.
     *
     * @param afterExecution the {@code ActionListener} to run after each execution
     * @return this timer instance, for chaining
     */
    public TurnTimer setAfterExecution(ActionListener afterExecution) {
        this.afterExecution = afterExecution;
        return this;
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
     * Returns the remaining number of turns until this timer's next activation.
     * <p>
     * If the timer is not active in the queue, returns 0.
     *
     * @return the number of turns left until this timer triggers, or 0 if inactive
     */
    public int getRemainingTurnsUntilActivation() {
        return queue.contains(this) ? this.executionTurn - turnNum : 0;
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
     * Increases the turn counter. Should be called once at the end of a turn.
     */
    public static void increaseTurn() {
        turnNum++;
        executeAllDueTimers();
    }

    @Override
    public String toString() {
        return "TurnTimer{" +
            "turns=" + turns +
            ", repeats=" + repeats +
            ", timesTriggered=" + timesTriggered +
            ", stopsAt=" + stopsAt +
            ", executionTurn=" + executionTurn +
            ", started=" + started +
            ", roomScoped=" + roomScoped +
            ", listener=" + (listener != null ? listener.getClass().getSimpleName() : "null") +
            ", onFinish=" + (onFinish != null ? onFinish.getClass().getSimpleName() : "null") +
            '}';
    }
}
