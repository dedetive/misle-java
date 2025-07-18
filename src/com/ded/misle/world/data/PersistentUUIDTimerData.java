package com.ded.misle.world.data;

import com.ded.misle.core.Path;
import com.ded.misle.world.logic.TurnTimer;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.ded.misle.core.Path.getPath;

public class PersistentUUIDTimerData implements Serializable {

    public final UUID uuid;
    @Serial
    private static final long serialVersionUID = 1L;

    private final Map<String, Integer> timers = new HashMap<>();

    public PersistentUUIDTimerData(UUID uuid) {
        this.uuid = uuid;
        new TurnTimer(1, true,
            e -> {
            timers.entrySet().removeIf(entry -> {
                int v = entry.getValue();
                if (v < 1) {
                    return true;
                }
                entry.setValue(v - 1);
                return false;
            });

            this.save();
        }).start();
    }

    public void setTurns(String targetId, int turns) {
        timers.put(targetId, turns);
    }

    public int getTurns(String targetId) {
        return timers.getOrDefault(targetId, -1);
    }

    public void remove(String targetId) {
        timers.remove(targetId);
    }

    public Map<String, Integer> asMap() {
        return new HashMap<>(timers);
    }

    public void merge(PersistentUUIDTimerData other) {
        if (!this.uuid.equals(other.uuid)) {
            System.err.println("UUIDs do not match");
            return;
        }
        this.timers.putAll(other.timers);
    }

    public void save() {
        File file = new File(getPath(Path.PathTag.RESOURCES) + File.separator + "timers_" + uuid + ".dat");

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void load() {
        File file = new File(getPath(Path.PathTag.RESOURCES) + File.separator + "timers_" + uuid + ".dat");

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            PersistentUUIDTimerData loadedData = (PersistentUUIDTimerData) ois.readObject();
            this.merge(loadedData);
        } catch (Exception ignored) {}
    }

    @Override
    public String toString() {
        return "PersistentUUIDData{" +
            "uuid=" + uuid +
            ", timers=" + timers +
            '}';
    }
}