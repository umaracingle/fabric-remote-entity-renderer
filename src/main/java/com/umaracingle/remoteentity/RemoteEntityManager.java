package com.umaracingle.remoteentity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RemoteEntityManager {
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final RemoteEntityConfig config;
    private final Map<UUID, RemoteEntity> liveEntities = new HashMap<>();
    private long lastPollMs = 0L;

    public RemoteEntityManager(RemoteEntityConfig config) {
        this.config = config;
    }

    public void tick(MinecraftClient client) {
        if (client.world == null) {
            return;
        }

        long now = System.currentTimeMillis();
        if (now - lastPollMs < config.pollIntervalSeconds() * 1000L) {
            return;
        }

        lastPollMs = now;
        CompletableFuture.supplyAsync(this::fetchRemoteEntities)
                .thenAccept(data -> applyRemoteData(client, data));
    }

    private List<RemoteEntityData> fetchRemoteEntities() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.url()))
                    .GET()
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                RemoteEntityMod.LOGGER.warn("Remote entity endpoint returned status {}", response.statusCode());
                return List.of();
            }

            return RemoteEntityData.parse(response.body());
        } catch (Exception e) {
            RemoteEntityMod.LOGGER.warn("Failed to fetch remote entity data from {}", config.url(), e);
            return List.of();
        }
    }

    private void applyRemoteData(MinecraftClient client, List<RemoteEntityData> remoteData) {
        if (client.world == null) {
            return;
        }

        Map<UUID, RemoteEntityData> incoming = new HashMap<>();
        for (RemoteEntityData data : remoteData) {
            incoming.put(data.uuid(), data);
        }

        for (Map.Entry<UUID, RemoteEntityData> entry : incoming.entrySet()) {
            UUID uuid = entry.getKey();
            RemoteEntityData data = entry.getValue();
            RemoteEntity entity = liveEntities.get(uuid);

            if (entity == null) {
                entity = new RemoteEntity(client.world, uuid, data);
                client.world.addEntity(entity);
                liveEntities.put(uuid, entity);
            } else {
                entity.updateFromRemote(resolvePreferredPosition(client.world, data));
            }
        }

        List<UUID> stale = new ArrayList<>();
        for (UUID existing : liveEntities.keySet()) {
            if (!incoming.containsKey(existing)) {
                stale.add(existing);
            }
        }

        for (UUID uuid : stale) {
            RemoteEntity entity = liveEntities.remove(uuid);
            if (entity != null) {
                entity.discard();
            }
        }
    }

    private RemoteEntityData resolvePreferredPosition(ClientWorld world, RemoteEntityData data) {
        Box range = Box.of(new Vec3d(data.x(), data.y(), data.z()), 512.0, 512.0, 512.0);
        for (Entity candidate : world.getEntitiesByClass(Entity.class, range, entity -> entity.getUuid().equals(data.uuid()))) {
            return new RemoteEntityData(
                    data.uuid(),
                    data.name(),
                    data.world(),
                    candidate.getX(),
                    candidate.getY(),
                    candidate.getZ(),
                    data.yaw(),
                    data.health(),
                    data.armor()
            );
        }

        return data;
    }
}
