package com.ded.misle.world;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

import static com.ded.misle.core.SettingsManager.getPath;

public class RoomManager {
    public static ArrayList<Room> rooms = new ArrayList<>();
    static {
        Path basePath = getPath().resolve("resources/worlds/");

        // TEMPORARY ARRANGEMENT
        // TODO: Change this to a text file with explicit order to ID mapping
        // As it is, IDs are not fixed in place

        for (File file : Objects.requireNonNull(basePath.toFile().listFiles())) {
            String normalizedName = file.getName();
            normalizedName = normalizedName.substring(0, normalizedName.lastIndexOf("."));
            normalizedName = normalizedName.toUpperCase();
            rooms.add(new Room(normalizedName, rooms.size()));
            System.out.println(rooms.getLast().name);
        }
    }

    public static class Room {
        public final String name;
        public final int id;

        Room(String name, int id) {
            this.name = name;
            this.id = id;
        }
    }

    public enum TravelTransition {
//        LEAVING_TUANI_HOUSE_1(TUANI_CITY, 460, 483),
//        ENTERING_TUANI_HOUSE_1(TUANI_HOUSE_1, 350, 110),
//        TUANI_CITY_TO_1(TUANI_1, 300, 440),
//        TUANI_1_TO_CITY(TUANI_CITY, 500, 31),
//        TUANI_1_TO_2(TUANI_2, 700, 1000),
//        TUANI_2_TO_1(TUANI_1, 1110, 31),

        ;

        public final int enteringRoomID;
        public final int x;
        public final int y;

        TravelTransition(Room enteringRoom, int x, int y) {
            this.enteringRoomID = enteringRoom.id;
            this.x = x;
            this.y = y;
        }
    }

    public static Room roomIDToName(int roomID) {
        try {
            return rooms.get(roomID);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    public static Room findRoom(String roomName) {
        for (Room room : rooms) {
            if (room.name.equals(roomName)) {
                return room;
            }
        }
        return null;
    }
}
