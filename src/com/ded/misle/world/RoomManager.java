package com.ded.misle.world;

public class RoomManager {
    public enum Room {
        VOID(0),
        CITY_TUANI(1),
        TUANI_HOUSE1(2),
        CLIFF(3),
        ;

        public final int id;

        Room(int id) {
            this.id = id;
        }
    }

    public enum TravelTransition {
        leaving_tuani_house1(1, 460, 483),
        entering_tuani_house1(2, 350, 110),
        tuani_to_cliff(3, 300, 440),
        cliff_to_tuani(1, 500, 31),

        ;

        public final int enteringRoomID;
        public final double x;
        public final double y;

        TravelTransition(int enteringRoomID, double x, double y) {
            this.enteringRoomID = enteringRoomID;
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
