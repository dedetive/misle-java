package com.ded.misle.world.logic;

import com.ded.misle.core.SettingsManager;

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
        Path basePath = getPath(SettingsManager.GetPathTag.RESOURCES).resolve("rooms/");

        StringBuilder jsonContent = new StringBuilder();
        Path worldsJson = basePath.resolve("rooms.json");
        try (BufferedReader reader = Files.newBufferedReader(worldsJson)) {
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String jsonText = jsonContent.toString();
        jsonText = jsonText.substring(1, jsonText.length() - 1);
        String[] itemBlocks = jsonText.split("},\\s*\\{");

        for (String block : itemBlocks) {
            try {
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

                String background = colorPart.split(",background:")[1].split(",")[0].trim();
                colorPart = colorPart.replace(",background:", "").replace(background, "").trim();

                int[] spawnpointPos = new int[2];
                String[] colorPairs = colorPart.replace("}", "").split("#");
                for (String pair : colorPairs) {
                    if (pair.trim().isEmpty()) continue;
                    String[] kv = pair.split(": ", 2);
                    if (kv[1].trim().lastIndexOf(",") == kv[1].trim().length() - 1) {
                        kv[1] = kv[1].trim().substring(0, kv[1].length() - 1).trim();
                    }
                    if (kv.length == 2) colorCodeMap.put(Integer.parseInt(kv[0].trim(), 16), kv[1].trim());
                    if (kv[1].contains("SPAWNPOINT")) {
                        spawnpointPos[0] = Integer.parseInt(kv[1].trim().split(":")[1].split(",", kv[1].trim().split(":")[1].length())[0]);
                        spawnpointPos[1] = Integer.parseInt(kv[1].trim().split(":")[2].split(",", kv[1].trim().split(":")[1].length())[0]);
                    }
                }

                new Room(roomName, fileNames, id, colorCodeMap, spawnpointPos, background);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("Failed to parse room " + block);
            }
        }
    }

    public static class Room {
        public final String name;
        public final String[] fileNames;
        public final int id;
        public final Map<Integer, String> colorCodeMap;
        public final World.Background background;
        public final int[] spawnpointPos = new int[2];

        Room(String name, String[] fileNames, int id, Map<Integer, String> colorCodeMap, String background) {
            this.name = name;
            this.fileNames = fileNames;
            this.id = id;
            this.colorCodeMap = colorCodeMap;
            this.background = World.Background.contains(background) ? World.Background.valueOf(background) : World.Background.DEFAULT;
            rooms.add(this);
        }

        Room(String name, String[] fileNames, int id, Map<Integer, String> colorCodeMap, int[] spawnpointPos, String background) {
            this(name, fileNames, id, colorCodeMap, background);
            this.spawnpointPos[0] = spawnpointPos[0];
            this.spawnpointPos[1] = spawnpointPos[1];
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
