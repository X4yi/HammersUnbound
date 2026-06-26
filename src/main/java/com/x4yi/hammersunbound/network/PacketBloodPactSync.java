package com.x4yi.hammersunbound.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import com.x4yi.hammersunbound.capability.IBloodPactCapability;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketBloodPactSync implements IMessage {
    private boolean active;
    private int[] targetEntityIds;
    private int remainingTicks;
    private int madness;
    private int burstTimer;
    private float accumulatedDamage;
    private int pingPongPhase;
    private int pingPongTargetId;

    public PacketBloodPactSync() {}

    public PacketBloodPactSync(boolean active, int[] targetEntityIds, int remainingTicks, int madness, int burstTimer, float accumulatedDamage) {
        this(active, targetEntityIds, remainingTicks, madness, burstTimer, accumulatedDamage, 0, -1);
    }

    public PacketBloodPactSync(boolean active, int[] targetEntityIds, int remainingTicks, int madness, int burstTimer, float accumulatedDamage, int pingPongPhase, int pingPongTargetId) {
        this.active = active;
        this.targetEntityIds = targetEntityIds;
        this.remainingTicks = remainingTicks;
        this.madness = madness;
        this.burstTimer = burstTimer;
        this.accumulatedDamage = accumulatedDamage;
        this.pingPongPhase = pingPongPhase;
        this.pingPongTargetId = pingPongTargetId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        active = buf.readBoolean();
        int size = buf.readInt();
        targetEntityIds = new int[size];
        for (int i = 0; i < size; i++) {
            targetEntityIds[i] = buf.readInt();
        }
        remainingTicks = buf.readInt();
        madness = buf.readInt();
        burstTimer = buf.readInt();
        accumulatedDamage = buf.readFloat();
        pingPongPhase = buf.readInt();
        pingPongTargetId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(active);
        if (targetEntityIds == null) {
            buf.writeInt(0);
        } else {
            buf.writeInt(targetEntityIds.length);
            for (int id : targetEntityIds) {
                buf.writeInt(id);
            }
        }
        buf.writeInt(remainingTicks);
        buf.writeInt(madness);
        buf.writeInt(burstTimer);
        buf.writeFloat(accumulatedDamage);
        buf.writeInt(pingPongPhase);
        buf.writeInt(pingPongTargetId);
    }

    public static class Handler implements IMessageHandler<PacketBloodPactSync, IMessage> {
        @Override
        public IMessage onMessage(PacketBloodPactSync message, MessageContext ctx) {
            if (ctx.side == net.minecraftforge.fml.relauncher.Side.CLIENT) {
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    EntityPlayer player = Minecraft.getMinecraft().player;
                    if (player != null && player.hasCapability(IBloodPactCapability.CAPABILITY, null)) {
                        IBloodPactCapability cap = player.getCapability(IBloodPactCapability.CAPABILITY, null);
                        if (cap != null && cap.getBloodPactEffect() != null) {
                            cap.getBloodPactEffect().syncClient(
                                    message.active,
                                    message.targetEntityIds,
                                    message.remainingTicks,
                                    message.madness,
                                    message.burstTimer,
                                    message.accumulatedDamage,
                                    message.pingPongPhase,
                                    message.pingPongTargetId
                            );
                        }
                    }
                });
            }
            return null;
        }
    }
}
