package com.ded.misle.world.entities.player;

import com.ded.misle.renderer.smoother.SmoothPosition;
import com.ded.misle.world.boxes.BoxManipulation;
import com.ded.misle.world.logic.TurnManager;
import com.ded.misle.world.logic.Path;
import com.ded.misle.world.logic.PhysicsEngine;

import java.awt.*;

import static com.ded.misle.game.GamePanel.*;
import static com.ded.misle.world.data.Direction.interpretDirection;

/**
 * Manages the movement planning mode for the player.
 * <p>
 * While in planning mode, the player can queue movement steps by navigating to positions on the world.
 * These steps are recorded without executing any movement in real-time, nor advancing turns.
 * Once planning is confirmed, the queued steps are executed sequentially, executing the turns as it would normally.
 * Only the final planned move is considered an attack.
 * <p>
 * If the player successfully attacks an enemy without taking any damage during the path execution,
 * a bonus damage multiplier is applied based on the number of tiles walked.
 */
public class Planner {
    /** Stores the sequence of planned points to move through. */
    private final Path path;

    /**
     * Indicates the last, unpassable point when a point in the plan touches a living object.
     */
    private Point enemyPoint;

    /**
     * Handles the interpolation for smooth visual transitions between path points.
     */
    private final SmoothPosition smoothPos;

    /** Indicates whether the player is currently in planning mode. */
    private boolean isPlanning;

    /**
     * Indicates the last time this Plan was executed and finished.
     */
    private long lastTimeExecuted = 0;

    /**
     * Indicates the last time this Plan has started execution.
     */
    private long lastTimeStarted = 0;

    /**
     * Constructs a new Planner with an empty path and planning mode disabled.
     */
    public Planner(Point playerPosition) {
        this.path = new Path().addPoint(playerPosition);
        this.isPlanning = false;
        this.smoothPos = new SmoothPosition(playerPosition.x, playerPosition.y);
    }

    /**
     * Attempts to add a new movement point to the plan.
     * Only adds the point if planning mode is active and the point is not already part of the plan.
     *
     * @param point the target point to add to the planned path
     */
    public void attemptToMove(Point point) {
        if (!isPlanning) return;
        if (enemyPoint == null) {
            if (!path.contains(point)) {
                path.addPoint(point);
                smoothPos.setTarget(point.x, point.y);
            } else if (getPoints()[getPoints().length - 2].equals(point)) {
                undo();
            }
        } else {
            undo();
        }
    }

    /**
     * Sets the last, unpassable point. Ideally when a point in the plan touches a living object.<p>
     * Note this enemy point is also a valid entry in this plan's Path.<p>
     * Also note only one point can be added. To add another, the last movement should be undone before.
     * @param point the target point to be marked as the enemy point
     */
    public void addEnemyPoint(Point point) {
        if (hasEnemyPoint()) return;
        path.addPoint(point);
        this.enemyPoint = point;
    }

    /**
     * Returns whether this plan has a point marked as an enemy point.
     * @return whether this has an enemy point
     */
    public boolean hasEnemyPoint() {
        return enemyPoint != null;
    }

    /**
     * Undoes last move.
     */
    public void undo() {
        path.undo();
        if (enemyPoint != null) enemyPoint = null;
        if (path.getPoints().length == 0) {
            isPlanning = false;
            return;
        }
        smoothPos.setTarget(path.getEnd().x, path.getEnd().y);
    }

    /**
     * Returns the sequence of points currently stored in the planner.
     *
     * @return an array of Points representing the planned movement path
     */
    public Point[] getPoints() {
        return this.path.getPoints();
    }

    /**
     * Checks if the planner is currently in planning mode.
     *
     * @return {@code true} if planning is active; {@code false} otherwise
     */
    public boolean isPlanning() {
        return isPlanning;
    }

    /**
     * Sets the planner's mode to planning or idle.
     *
     * @param planning {@code true} to enable planning mode, {@code false} to disable it
     * @return this Planner object for chaining
     */
    public Planner setPlanning(boolean planning) {
        isPlanning = planning;
        return this;
    }

    /**
     * Returns this planner's last point.
     * @return the last point in this planner's path array
     */
    public Point getEnd() {
        return this.path.getEnd();
    }

    /**
     * Returns the current interpolated screen position of the player along the planned path.
     * This is used for rendering smooth movement transitions while in planning mode.
     *
     * @return a Point representing the interpolated screen position
     */
    public Point getSmoothPos() {
        return new Point(this.smoothPos.getRenderX(), this.smoothPos.getRenderY());
    }

    /**
     * Updates the internal smooth position based on the interpolation speed and tile size.
     * This ensures visually smooth movement along the path during rendering.
     */
    public void updateSmoothPos() {
        this.smoothPos.update(50f);
    }

    /**
     * Default delay in milliseconds between each turn during this plan's execution. {@link #delayPerTurn} gets set to this value every time the plan is going to be executed.
     */
    private static final int DEFAULT_DELAY_PER_TURN = 500;

    /**
     * Delay in milliseconds between each turn during this plan's execution. This value is reduced by {@link #DELAY_REDUCTION_PER_TURN} every turn.
     */
    private static int delayPerTurn = DEFAULT_DELAY_PER_TURN;

    /**
     * Minimum delay in milliseconds between each turn during this plan's execution.
     */
    private static final int MINIMUM_DELAY_PER_TURN = 120;

    /**
     * Value in milliseconds that reduces current delay per turn, until it reaches {@link #MINIMUM_DELAY_PER_TURN}.
     */
    private static final int DELAY_REDUCTION_PER_TURN = 7;

    /**
     * Value in milliseconds that delayPerTurn is set to if {@link #quickExecution} is set to true.
     */
    private static final int QUICK_DELAY_PER_TURN = 50;

    /**
     * Whether execution should use quick execution or not. If it uses, {@link #delayPerTurn} is always equal to {@link #QUICK_DELAY_PER_TURN}.
     */
    private static boolean quickExecution = false;

    /**
     * Flag indicating whether the plan is currently being executed.
     */
    private boolean isExecuting = false;

    /**
     * Lock object used for synchronizing turn delay and skipStep interruptions.
     */
    private final Object lock = new Object();

    /**
     * Lock object used for synchronizing pause interruptions.
     */
    private static final Object pauseLock = new Object();

    /**
     * Returns whether the plan is currently being executed.
     *
     * @return true if the plan is being executed, false otherwise
     */
    public boolean isExecuting() {
        return isExecuting;
    }

    /**
     * Starts executing the current plan on a separate thread.
     * Each step in the plan moves the player and waits for a fixed delay,
     * unless interrupted by {@link #skipStep()}.
     */
    public void executePlan() {
        isExecuting = true;
        player.stepCounter.reset();
        Thread executor = new Thread(() -> {
            Point previousPoint = new Point(-1, -1);
            delayPerTurn = DEFAULT_DELAY_PER_TURN;
            lastTimeStarted = System.currentTimeMillis();

            for (Point point : this.path.getPoints()) {
                if (!isExecuting) return;
                synchronized (pauseLock) {
                    while (gameState == GameState.PAUSE_MENU) {
                        try {
                            pauseLock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                }

                // Ignore player initial position
                if (path.getPoints()[0].equals(point)) {
                    previousPoint = point;
                    continue;
                }

                player.stepCounter.updateStep(player.stepCounter.getCurrentStep() + 1);

                delayPerTurn = quickExecution
                    ? QUICK_DELAY_PER_TURN
                    : Math.max(delayPerTurn - DELAY_REDUCTION_PER_TURN, MINIMUM_DELAY_PER_TURN);

                path.removePoint(previousPoint);
                Point unitaryPoint = new Point(point.x - player.getX(), point.y - player.getY());
                if (!PhysicsEngine.isSpaceOccupied(point.x, point.y, player))
                    BoxManipulation.movePlayer(unitaryPoint.x, unitaryPoint.y);
                else {
                    // Kills execution sooner, so no damage multi
                    isPlanning = false;
                    isExecuting = false;
                    player.inv.useItem();
                }
                player.pos.updateLastDirection(interpretDirection(unitaryPoint.x, unitaryPoint.y));
                TurnManager.requestNewTurn();
                previousPoint = point;

                if (path.getLength() <= 1) {
                    isPlanning = false;
                    isExecuting = false;
                    player.inv.useItem();
                } else {
                    synchronized (lock) {
                        try {
                            lock.wait(delayPerTurn);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }

            lastTimeExecuted = System.currentTimeMillis();
            quickExecution = false;
            isExecuting = false;
        });

        executor.start();
    }

    /**
     * Interrupts the current turn delay in {@link #executePlan()}, causing the execution to
     * immediately proceed to the next step without waiting for the full delay.
     */
    public void skipStep() {
        if (!this.isExecuting) return;

        synchronized (lock) {
            lock.notify();
        }
    }

    /**
     * Toggles {@link #quickExecution} tag. This property, when true, sets {@link #delayPerTurn} to {@link #QUICK_DELAY_PER_TURN} during execution.
     * Note that quick execution is always set to false after finishing the execution.
     */
    public void toggleQuickExecution() {
        if (!this.isExecuting) return;

        quickExecution = !quickExecution;
    }

    /**
     * Immediately interrupts this plan's execution. If it was not executing, this does nothing.
     */
    public void killExecution() {
        isExecuting = false;
    }

    /**
     * @return last time this Planner's plan was finished.
     */
    public long getLastTimeExecuted() {
        return lastTimeExecuted;
    }

    /**
     * @return last time this Planner's plan was started.
     */
    public long getLastTimeStarted() {
        return lastTimeStarted;
    }

    /**
     * Wakes up any execution that might have been paused.
     */
    public static void resumeExecution() {
        synchronized (pauseLock) {
            pauseLock.notifyAll();
        }
    }
}