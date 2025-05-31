package com.ded.misle.world.entities.player;

import com.ded.misle.world.data.Direction;
import com.ded.misle.world.logic.RoomManager;
import com.ded.misle.world.logic.World;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.ded.misle.game.GamePanel.*;
import static com.ded.misle.world.data.Direction.RIGHT;
import static com.ded.misle.world.data.Direction.UP;

public class PlayerPosition {

    private double cameraOffsetX;
    private double cameraOffsetY;
	private int spawnpointRoom = 1;
	private Region region = Region.VOID;
	private int roomID;
	private double rotation;
	public World world;

	public Direction walkingDirection;
	public Direction horizontalDirection;
	public Direction verticalDirection;
	private long lastDirectionUpdate;

    public enum Region {
		VOID,
		CHAIN_OF_LIES
	}

	public PlayerPosition() {
		setCameraOffsetX(0);
		setCameraOffsetY(0);
		setRegion(Region.CHAIN_OF_LIES);
		this.walkingDirection = RIGHT;
		this.horizontalDirection = RIGHT;
		this.verticalDirection = UP;
	}

	public double getCameraOffsetX() {
		return cameraOffsetX;
	}

	public void setCameraOffsetX(double cameraOffsetX) {
		this.cameraOffsetX = cameraOffsetX;
	}

	public double getCameraOffsetY() {
		return cameraOffsetY;
	}

	public void setCameraOffsetY(double cameraOffsetY) {
		this.cameraOffsetY = cameraOffsetY;
	}

	public double calculateCameraOffsetX() {
		Planner planner = player.getPlanner();
		int targetX = planner.isPlanning() && !planner.isExecuting()
			? planner.getEnd().x
			: player.getX();

		return Math.clamp(targetX * originalTileSize - (double) originalScreenWidth / 2,
			- originalTileSize,
			Math.max(originalWorldWidth - originalScreenWidth + originalTileSize, 0));
	}
	public double calculateCameraOffsetY() {
		Planner planner = player.getPlanner();
		int targetY = planner.isPlanning() && !planner.isExecuting()
			? planner.getEnd().y
			: player.getY();

		return Math.clamp(targetY * originalTileSize - (double) originalScreenHeight / 2,
			(double) - originalTileSize / 2,
			originalWorldHeight - originalScreenHeight + (double) originalTileSize / 2);
	}

	public int getSpawnpoint() {
		return spawnpointRoom;
	}

	public void setSpawnpoint(int roomID) {
		this.spawnpointRoom = roomID;
	}

	public void reloadSpawnpoint() {
		setRoomID(spawnpointRoom);
		RoomManager.Room room = RoomManager.roomIDToName(spawnpointRoom);
		if (room == null) room = RoomManager.roomIDToName(1);
        assert room != null;

		int[] spawnpointCoordinates = room.spawnpointPos;
		player.setX(spawnpointCoordinates[0]);
		player.setY(spawnpointCoordinates[1]);
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public int getRoomID() {
		return roomID;
	}

	public void setRoomID(int roomID) {
		this.roomID = roomID;
	}

	public double getRotation() {
		return rotation;
	}

	public void setRotation(double rotation) {
		this.rotation = rotation;
	}

	public void delayedRotate(double angle, double delay) {
		int frames = (int)(delay / 1000 * 60);
		double dangle = angle / frames;
		Timer timer = new Timer(1000 / 60, new ActionListener() {
			int count = 0;
			public void actionPerformed(ActionEvent evt) {
				if (count < frames) {
					player.pos.setRotation(player.pos.getRotation() + dangle);
					count++;
				}
			}
		});
		timer.start();
	}

	public Direction getWalkingDirection() {
		return player.pos.walkingDirection;
	}

	public Direction getHorizontalDirection() {
		return player.pos.horizontalDirection;
	}

	public Direction getVerticalDirection() {
		return player.pos.verticalDirection;
	}


	public void updateLastDirection(Direction direction) {
		walkingDirection = direction;
		switch (direction) {
			case LEFT, RIGHT -> horizontalDirection = direction;
			case UP, DOWN -> verticalDirection = direction;
		}
		lastDirectionUpdate = System.currentTimeMillis();
	}

	public Direction getRecentDirection(long precision) {
		return getDirectionIfPrecision(walkingDirection, precision);
	}

	public Direction getRecentHorizontalDirection(long precision) {
		return getDirectionIfPrecision(horizontalDirection, precision);
	}

	public Direction getRecentVerticalDirection(long precision) {
		return getDirectionIfPrecision(verticalDirection, precision);
	}

	private Direction getDirectionIfPrecision(Direction direction, long precision) {
		return lastDirectionUpdate + precision > System.currentTimeMillis()
			? direction
			: Direction.NONE;
	}
}
