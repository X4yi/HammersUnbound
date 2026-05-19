package com.x4yi.hammersunbound.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;
import java.util.List;

public class PacketBloodPactVisual implements IMessage {

    private int playerEntityId;
    private int targetEntityId;
    private boolean active;

    private static List<BloodPactVisual> activeVisuals = new ArrayList<>();

    public PacketBloodPactVisual() {}

    public PacketBloodPactVisual(int playerEntityId, int targetEntityId, boolean active) {
        this.playerEntityId = playerEntityId;
        this.targetEntityId = targetEntityId;
        this.active = active;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        playerEntityId = buf.readInt();
        targetEntityId = buf.readInt();
        active = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(playerEntityId);
        buf.writeInt(targetEntityId);
        buf.writeBoolean(active);
    }

    public static void addVisual(int playerEntityId, int targetEntityId) {
        activeVisuals.add(new BloodPactVisual(playerEntityId, targetEntityId));
    }

    public static void removeVisual(int playerEntityId, int targetEntityId) {
        activeVisuals.removeIf(v -> v.playerEntityId == playerEntityId && v.targetEntityId == targetEntityId);
    }

    public static List<BloodPactVisual> getActiveVisuals() {
        return activeVisuals;
    }

    public static class BloodPactVisual {
        public final int playerEntityId;
        public final int targetEntityId;

        public BloodPactVisual(int playerEntityId, int targetEntityId) {
            this.playerEntityId = playerEntityId;
            this.targetEntityId = targetEntityId;
        }

        public Vec3d getPlayerPos() {
            if (Minecraft.getMinecraft().world == null) return null;
            Entity e = Minecraft.getMinecraft().world.getEntityByID(playerEntityId);
            if (e == null) return null;
            return new Vec3d(e.posX, e.posY + e.height / 2.0, e.posZ);
        }

        public Vec3d getTargetPos() {
            if (Minecraft.getMinecraft().world == null) return null;
            Entity e = Minecraft.getMinecraft().world.getEntityByID(targetEntityId);
            if (e == null) return null;
            return new Vec3d(e.posX, e.posY + e.height / 2.0, e.posZ);
        }
    }

    public static class Handler implements IMessageHandler<PacketBloodPactVisual, IMessage> {
        @Override
        public IMessage onMessage(PacketBloodPactVisual message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                if (message.active) {
                    addVisual(message.playerEntityId, message.targetEntityId);
                } else {
                    removeVisual(message.playerEntityId, message.targetEntityId);
                }
            });
            return null;
        }
    }
}
