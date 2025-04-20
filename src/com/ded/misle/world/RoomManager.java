package com.ded.misle.world;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.ded.misle.core.SettingsManager.getPath;

public class RoomManager {
    public static ArrayList<Room> rooms = new ArrayList<>();

    static {
        Path basePath = getPath().resolve("resources/worlds/");

        StringBuilder jsonContent = new StringBuilder();
        Path worldsJson = basePath.resolve("worlds.json");
        try (BufferedReader reader = Files.newBufferedReader(worldsJson)) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line.trim());
            }
        } catch (IOException e) { e.printStackTrace(); }

        String jsonText = jsonContent.toString();
        jsonText = jsonText.substring(1, jsonText.length() - 1);
        String[] itemBlocks = jsonText.split("},\\s*\\{");

        for (String block : itemBlocks) {
            block = block.replace("{", "").replace("}", "").replace("\"", "");

            Map<Integer, String> colorCodeMap = new HashMap<>();

            String[] colorPartSplit = block.split("custom_color_code:");
            String colorPart = null;
            if (colorPartSplit.length > 1) {
                block = colorPartSplit[0] + "id:" + colorPartSplit[1].split("id:")[1];
                colorPart = colorPartSplit[1].split("id:")[0];
            }

            String[] parts = block.split(",id:");
            String roomAndFiles = parts[0].split("custom_color_code:")[0].trim();
            int id = Integer.parseInt(parts[1].trim());

            String[] nameAndFiles = roomAndFiles.split(":\\s*\\[");
            String roomName = nameAndFiles[0].trim();
            String filesPart = nameAndFiles[1].replace("]", "").trim();
            String[] fileNames = filesPart.isEmpty() ? new String[]{} : filesPart.split(",");

            if (colorPart != null) {
                String[] colorPairs = colorPart.replace("}", "").split(",");
                for (String pair : colorPairs) {
                    String[] kv = pair.split(":");
                    if (kv.length == 2) colorCodeMap.put(Integer.parseInt(kv[0].trim(), 16), kv[1].trim());
                }
            }

            new Room(roomName, fileNames, id, colorCodeMap);
        }
    }

    public static class Room {
        public final String name;
        public final String[] fileNames;
        public final int id;
        public final Map<Integer, String> colorCodeMap;

        Room(String name, String[] fileNames, int id, Map<Integer, String> colorCodeMap) {
            this.name = name;
            this.fileNames = fileNames;
            this.id = id;
            this.colorCodeMap = colorCodeMap;
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
