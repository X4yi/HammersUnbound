package com.x4yi.hammersunbound.network;

import com.x4yi.hammersunbound.capability.IBleedingCapability;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketBleedingSync implements IMessage {

    private int entityId;
    private int bleedingLevel;

    public PacketBleedingSync() {}

    public PacketBleedingSync(int entityId, int bleedingLevel) {
        this.entityId = entityId;
        this.bleedingLevel = bleedingLevel;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        bleedingLevel = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeInt(bleedingLevel);
    }

    public static class Handler implements IMessageHandler<PacketBleedingSync, IMessage> {
        @Override
        public IMessage onMessage(PacketBleedingSync message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                if (Minecraft.getMinecraft().world == null) return;
                Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entityId);
                if (entity != null && entity.hasCapability(IBleedingCapability.CAPABILITY, null)) {
                    IBleedingCapability cap = entity.getCapability(IBleedingCapability.CAPABILITY, null);
                    if (cap != null) {
                        cap.getBleedingEffect().setLevel(message.bleedingLevel);
                    }
                }
            });
            return null;
        }
    }
}
