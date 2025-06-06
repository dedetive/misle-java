package com.ded.misle.net;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.*;

public class NetClient {

    /*
         NOTE:
        This module currently has no real authentication and only runs locally.
        Other players appear as ghosts only, and cannot interact with the host's world.
        This is intended, and may only change with the introduction of private parties,
        online connection features, and protective measures.

     */

    public static void sendPosition(String uuid, String name, int x, int y, int roomID, BufferedImage icon, int heldItemID) {
        try {
            URL url = URI.create("http://localhost:8080/update").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            String base64Icon = "";
            if (icon != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(icon, "png", baos);
                base64Icon = Base64.getEncoder().encodeToString(baos.toByteArray());
            }

            String json = String.format(
                "{\"uuid\":\"%s\",\"name\":\"%s\",\"x\":%d,\"y\":%d,\"roomID\":%d,\"icon\":\"%s\",\"heldItemID\":%d}",
                uuid, name, x, y, roomID, base64Icon, heldItemID
            );

            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }

            conn.getInputStream().close();
        } catch (IOException e) {
            System.err.println("Error when sending position: " + e.getMessage());
        }
    }

    public static List<Player> fetchOnlinePlayers(String uuid) {
        try {
            URL url = URI.create("http://localhost:8080/players?uuid=" + uuid).toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String json = reader.readLine();
                return parsePlayers(json);
            }
        } catch (IOException e) {
            System.err.println("Error when attempting to fetch players: " + e.getMessage());
            return List.of();
        }
    }

    public static List<Player> parsePlayers(String json) {
        List<Player> list = new ArrayList<>();
        json = json.replace("[", "").replace("]", "");
        String[] objects = json.split("},\\s*\\{");
        for (String obj : objects) {
            obj = obj.replace("{", "").replace("}", "").replace("\"", "");
            String[] parts = obj.split(",");
            Map<String, String> map = new HashMap<>();
            for (String part : parts) {
                String[] kv = part.split(":");
                if (kv.length == 2) map.put(kv[0].trim(), kv[1].trim());
            }
            if (map.containsKey("uuid") && map.containsKey("x") &&
                map.containsKey("y") && map.containsKey("roomID") &&
                map.containsKey("icon") && map.containsKey("heldItemID")
            ) {
                Player p = new Player();
                p.uuid = map.get("uuid");
                p.name = map.get("name");
                p.x = Integer.parseInt(map.get("x"));
                p.y = Integer.parseInt(map.get("y"));
                p.roomID = Integer.parseInt(map.get("roomID"));
                p.heldItemID = Integer.parseInt(map.get("heldItemID"));
                try {
                    byte[] decoded = Base64.getDecoder().decode(map.get("icon"));
                    p.icon = ImageIO.read(new java.io.ByteArrayInputStream(decoded));
                } catch (Exception e) {
                    System.err.println("Failed to decode player icon for " + p.uuid);
                }
                list.add(p);
            }
        }
        return list;
    }

    public static boolean isServerOnline() {
        try {
            URL url = URI.create("http://localhost:8080/ping").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(300);
            conn.setReadTimeout(300);
            return conn.getResponseCode() == 200;
        } catch (IOException e) {
            return false;
        }
    }

    public static class Player {
        public String uuid, name;
        public BufferedImage icon;
        public int x, y;
        public int roomID;
        public int heldItemID;
    }
}