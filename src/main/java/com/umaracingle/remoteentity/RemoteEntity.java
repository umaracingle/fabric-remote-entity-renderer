package com.umaracingle.remoteentity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.text.Text;
import net.minecraft.world.World;

import java.util.UUID;

public class RemoteEntity extends ArmorStandEntity {
    private final UUID remoteUuid;

    public RemoteEntity(World world, UUID remoteUuid, RemoteEntityData data) {
        super(EntityType.ARMOR_STAND, world);
        this.remoteUuid = remoteUuid;

        setSilent(true);
        setNoGravity(true);
        setInvulnerable(true);
        setInvisible(false);
        setCustomNameVisible(true);
        setCustomName(Text.literal(data.name()));
        setPos(data.x(), data.y(), data.z());
        setYaw(data.yaw());
        setHealth(data.health());
        setMarker(true);
        setSmall(true);
        setShowArms(false);
    }

    public UUID getRemoteUuid() {
        return remoteUuid;
    }

    public void updateFromRemote(RemoteEntityData data) {
        setPos(data.x(), data.y(), data.z());
        setYaw(data.yaw());
        setCustomName(Text.literal(data.name()));
        setHealth(data.health());
    }

    @Override
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        return true;
    }

    @Override
    public boolean shouldRenderAtDistance(double distance) {
        return true;
    }

    @Override
    public boolean canHit() {
        return false;
    }

    @Override
    public boolean collidesWith(Entity other) {
        return false;
    }
}
