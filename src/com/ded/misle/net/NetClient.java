package com.ded.misle.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetClient {

    /*
         NOTE:
        This module currently has no real authentication and only runs locally.
        Other players appear as ghosts only, and cannot interact with the host's world.
        This is intended, and may only change with the introduction of private parties,
        online connection features, and protective measures.

     */

    public static void sendPosition(String id, int x, int y, int roomID) {
        try {
            URL url = new URL("http://localhost:8080/update");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            String json = String.format("{\"id\":\"%s\", \"x\":%d, \"y\":%d, \"roomID\":%d}", id, x, y, roomID);
            try (OutputStream os = conn.getOutputStream()) {
                os.write(json.getBytes());
            }

            conn.getInputStream().close();
        } catch (IOException e) {
            System.err.println("Error when sending position: " + e.getMessage());
        }
    }

    public static List<Player> fetchOnlinePlayers(String playerName) {
        try {
            URL url = new URL("http://localhost:8080/players?id=" + playerName);
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
            if (map.containsKey("id") && map.containsKey("x") && map.containsKey("y") && map.containsKey("roomID")) {
                Player p = new Player();
                p.id = map.get("id");
                p.x = Integer.parseInt(map.get("x"));
                p.y = Integer.parseInt(map.get("y"));
                p.roomID = Integer.parseInt(map.get("roomID"));
                list.add(p);
            }
        }
        return list;
    }

    public static boolean isServerOnline() {
        try {
            URL url = new URL("http://localhost:8080/ping");
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
        public String id;
        public int x, y;
        public int roomID;
    }
}