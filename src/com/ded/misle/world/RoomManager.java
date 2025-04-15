package com.ded.misle.world;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.function.Function;

import static com.ded.misle.core.SettingsManager.getPath;

public class RoomManager {
    public static ArrayList<Room> rooms = new ArrayList<>();
    static {
        Path basePath = getPath().resolve("resources/worlds/");

        // TODO: Put all this junk in some separate more generic method and maybe reuse in ItemLoader
        StringBuilder jsonContent = new StringBuilder();
        Path worldsJson = basePath.resolve("worlds.json");
        try (BufferedReader reader = Files.newBufferedReader(worldsJson)) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line.trim());
            }
        } catch (IOException e) { e.printStackTrace(); }

        String jsonText = jsonContent.toString();
        jsonText = jsonText.substring(1, jsonText.length() - 1); // Remove "[" and "]"
        String[] itemBlocks = jsonText.split("},\\s*\\{");
        for (String block : itemBlocks) {
            block = block.replace("{", "").replace("}", "").replace("\"", "");
            System.out.println(block);

            String[] parts = block.split(",id:");

            String roomAndFiles = parts[0].trim(); // ROOM_NAME: [ROOM_PNG1,ROOM_PNG2...]
            int id = Integer.parseInt(parts[1].trim());
            System.out.println(id);

            String[] nameAndFiles = roomAndFiles.split(":\\s*\\[");
            String roomName = nameAndFiles[0].trim();
            String filesPart = nameAndFiles[1].replace("]", "").trim();

            String[] fileNames = filesPart.isEmpty() ? new String[]{} : filesPart.split(",");
            for (String name: fileNames) System.out.println(name);

            new Room(
                roomName,
                fileNames,
                id
            );
        }
    }

    public static class Room {
        public final String name;
        public final String[] fileNames;
        public final int id;

        Room(String name, String[] fileNames, int id) {
            this.name = name;
            this.fileNames = fileNames;
            this.id = id;
            rooms.add(this);
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
