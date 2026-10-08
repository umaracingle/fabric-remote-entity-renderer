package com.umaracingle.remoteentity;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record RemoteEntityData(
        UUID uuid,
        String name,
        String world,
        double x,
        double y,
        double z,
        float yaw,
        int health,
        int armor
) {
    public static List<RemoteEntityData> parse(String json) {
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonArray players = root.getAsJsonArray("players");
            if (players == null) {
                return List.of();
            }

            List<RemoteEntityData> result = new ArrayList<>();
            for (var element : players) {
                if (!element.isJsonObject()) {
                    continue;
                }

                JsonObject player = element.getAsJsonObject();
                String uuidString = player.has("uuid") ? player.get("uuid").getAsString() : "00000000-0000-0000-0000-000000000000";
                String name = player.has("name") ? player.get("name").getAsString() : "unknown";
                String world = player.has("world") ? player.get("world").getAsString() : "minecraft_overworld";
                double x = player.has("x") ? player.get("x").getAsDouble() : 0.0;
                double y = player.has("y") ? player.get("y").getAsDouble() : 0.0;
                double z = player.has("z") ? player.get("z").getAsDouble() : 0.0;
                float yaw = player.has("yaw") ? player.get("yaw").getAsFloat() : 0.0F;
                int health = player.has("health") ? player.get("health").getAsInt() : 20;
                int armor = player.has("armor") ? player.get("armor").getAsInt() : 0;

                result.add(new RemoteEntityData(
                        UUID.fromString(uuidString),
                        name,
                        world,
                        x,
                        y,
                        z,
                        yaw,
                        health,
                        armor
                ));
            }

            return result;
        } catch (Exception e) {
            RemoteEntityMod.LOGGER.warn("Failed to parse remote entity JSON", e);
            return List.of();
        }
    }
}
