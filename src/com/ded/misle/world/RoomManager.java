package com.ded.misle.world;

import java.util.HashMap;
import java.util.Map;

public class RoomManager {
    static HashMap<String, Integer> room = new HashMap<>();
    static {
        room.put("void", 0);
        room.put("city_tuani", 1);
        room.put("tuani_house1", 2);
        room.put("cliff", 3);
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

    public static int roomNameToID(String roomName) {
        return room.get(roomName);
    }

    public static String roomIDToName(int roomID) {
        for (Map.Entry<String, Integer> entry : room.entrySet()) {
            if (entry.getValue() == roomID) {
                return entry.getKey();
            }
        }
        return null;
    }
}
