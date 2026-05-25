package com.x4yi.hammersunbound.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketBleedingParticle implements IMessage {
    private int entityId;
    private int level;

    public PacketBleedingParticle() {}

    public PacketBleedingParticle(int entityId, int level) {
        this.entityId = entityId;
        this.level = level;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        level = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeInt(level);
    }

    public static class Handler implements IMessageHandler<PacketBleedingParticle, IMessage> {
        @Override
        public IMessage onMessage(PacketBleedingParticle message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                Minecraft.getMinecraft().addScheduledTask(() -> {
                    Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entityId);
                    if (entity != null) {
                        if (com.x4yi.hammersunbound.config.ClientConfig.bleedingParticleEnabled) {
                            for (int i = 0; i < message.level; i++) {
                                double x = entity.posX + (entity.world.rand.nextDouble() - 0.5) * entity.width;
                                double y = entity.posY + entity.world.rand.nextDouble() * entity.height * 0.8;
                                double z = entity.posZ + (entity.world.rand.nextDouble() - 0.5) * entity.width;
                                
                                double mx = (entity.world.rand.nextDouble() - 0.5) * 0.06D;
                                double my = entity.world.rand.nextDouble() * 0.06D;
                                double mz = (entity.world.rand.nextDouble() - 0.5) * 0.06D;
                                
                                com.x4yi.hammersunbound.client.particle.ParticleBlood particle = new com.x4yi.hammersunbound.client.particle.ParticleBlood(entity.world, x, y, z, mx, my, mz);
                                Minecraft.getMinecraft().effectRenderer.addEffect(particle);
                            }
                        }
                    }
                });
            }
            return null;
        }
    }
}
