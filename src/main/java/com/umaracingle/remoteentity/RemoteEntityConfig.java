package com.umaracingle.remoteentity;

import com.google.gson.Gson;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public record RemoteEntityConfig(String url, int pollIntervalSeconds) {
    private static final Gson GSON = new Gson();

    public static RemoteEntityConfig load() {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        Path configPath = configDir.resolve("remote-entity-renderer.json");

        try {
            Files.createDirectories(configDir);

            if (Files.notExists(configPath)) {
                RemoteEntityConfig defaultConfig = new RemoteEntityConfig(
                    "https://example.com/api/players",
                    5
                );
                Files.writeString(configPath, GSON.toJson(defaultConfig));
                return defaultConfig;
            }

            String json = Files.readString(configPath);
            if (json == null || json.isBlank()) {
                return new RemoteEntityConfig("https://example.com/api/players", 5);
            }

            return GSON.fromJson(json, RemoteEntityConfig.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load remote entity config", e);
        }
    }
}
