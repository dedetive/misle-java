package com.ded.misle.world;

import static com.ded.misle.world.RoomManager.Room.*;

public class RoomManager {
    public enum Room {
        VOID(0),
        TUANI_CITY(1),
        TUANI_HOUSE_1(2),
        TUANI_1(3),
        TUANI_2(4),
        ;

        public final int id;

        Room(int id) {
            this.id = id;
        }
    }

    public enum TravelTransition {
        LEAVING_TUANI_HOUSE_1(TUANI_CITY, 460, 483),
        ENTERING_TUANI_HOUSE_1(TUANI_HOUSE_1, 350, 110),
        TUANI_CITY_TO_1(TUANI_1, 300, 440),
        TUANI_1_TO_CITY(TUANI_CITY, 500, 31),
        TUANI_1_TO_2(TUANI_2, 700, 1000),
        TUANI_2_TO_1(TUANI_1, 1110, 31),

        ;

        public final int enteringRoomID;
        public final double x;
        public final double y;

        TravelTransition(Room enteringRoom, double x, double y) {
            this.enteringRoomID = enteringRoom.id;
            this.x = x;
            this.y = y;
        }
    }

    public static Room roomIDToName(int roomID) {
        for (Room room : RoomManager.Room.values()) {
            if (room.id == roomID) {
                return room;
            }
        }
        return null;
    }
}
