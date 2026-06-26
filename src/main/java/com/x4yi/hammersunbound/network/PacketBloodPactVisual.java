package com.x4yi.hammersunbound.network;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import java.util.ArrayList;
import java.util.List;
public class PacketBloodPactVisual implements IMessage {
    private int playerEntityId;
    private int[] targetEntityIds;
    private boolean active;
    private static List<BloodPactVisual> activeVisuals = new ArrayList<>();
    public PacketBloodPactVisual() {}
    public PacketBloodPactVisual(int playerEntityId, int targetEntityId, boolean active) {
        this.playerEntityId = playerEntityId;
        this.targetEntityIds = new int[]{targetEntityId};
        this.active = active;
    }
    public PacketBloodPactVisual(int playerEntityId, int[] targetEntityIds, boolean active) {
        this.playerEntityId = playerEntityId;
        this.targetEntityIds = targetEntityIds;
        this.active = active;
    }
    @Override
    public void fromBytes(ByteBuf buf) {
        playerEntityId = buf.readInt();
        int size = buf.readInt();
        targetEntityIds = new int[size];
        for (int i = 0; i < size; i++) {
            targetEntityIds[i] = buf.readInt();
        }
        active = buf.readBoolean();
    }
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(playerEntityId);
        if (targetEntityIds == null) {
            buf.writeInt(0);
        } else {
            buf.writeInt(targetEntityIds.length);
            for (int id : targetEntityIds) {
                buf.writeInt(id);
            }
        }
        buf.writeBoolean(active);
    }
    public static void addVisual(int playerEntityId, int[] targetEntityIds) {
        removeVisual(playerEntityId);
        activeVisuals.add(new BloodPactVisual(playerEntityId, targetEntityIds));
    }
    public static void removeVisual(int playerEntityId) {
        activeVisuals.removeIf(v -> v.playerEntityId == playerEntityId);
    }
    public static List<BloodPactVisual> getActiveVisuals(World world) {
        if (world != null) {
            activeVisuals.removeIf(v -> {
                Entity p = world.getEntityByID(v.playerEntityId);
                return p == null || p.isDead;
            });
        }
        return activeVisuals;
    }
    public static class BloodPactVisual {
        public final int playerEntityId;
        public final int[] targetEntityIds;
        public BloodPactVisual(int playerEntityId, int[] targetEntityIds) {
            this.playerEntityId = playerEntityId;
            this.targetEntityIds = targetEntityIds;
        }
        public Vec3d getPlayerPos(World world) {
            if (world == null) return null;
            Entity e = world.getEntityByID(playerEntityId);
            if (e == null) return null;
            return new Vec3d(e.posX, e.posY + e.height / 2.0, e.posZ);
        }
        public Vec3d getTargetPos(World world, int targetId) {
            if (world == null) return null;
            Entity e = world.getEntityByID(targetId);
            if (e == null) return null;
            return new Vec3d(e.posX, e.posY + e.height / 2.0, e.posZ);
        }
    }
    public static class Handler implements IMessageHandler<PacketBloodPactVisual, IMessage> {
        @Override
        public IMessage onMessage(PacketBloodPactVisual message, MessageContext ctx) {
            if (ctx.side == net.minecraftforge.fml.relauncher.Side.CLIENT) {
                com.x4yi.hammersunbound.HammersUnbound.proxy.handleBloodPactVisual(message.playerEntityId, message.targetEntityIds, message.active);
            }
            return null;
        }
    }
}