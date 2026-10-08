package com.umaracingle.remoteentity;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteEntityMod implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("remote-entity-renderer");
    private static RemoteEntityManager manager;

    @Override
    public void onInitializeClient() {
        RemoteEntityConfig config = RemoteEntityConfig.load();
        manager = new RemoteEntityManager(config);
        ClientTickEvents.END_CLIENT_TICK.register(client -> manager.tick(client));
        LOGGER.info("Remote entity rendering enabled with URL {} and poll interval {}s", config.url(), config.pollIntervalSeconds());
    }
}
