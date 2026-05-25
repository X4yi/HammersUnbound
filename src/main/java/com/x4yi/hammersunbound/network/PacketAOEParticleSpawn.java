package com.x4yi.hammersunbound.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketAOEParticleSpawn implements IMessage {

    private double posX;
    private double posY;
    private double posZ;
    private float radius;
    private int particleCount;

    public PacketAOEParticleSpawn() {}

    public PacketAOEParticleSpawn(double posX, double posY, double posZ, float radius, int particleCount) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.radius = radius;
        this.particleCount = particleCount;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        posX = buf.readDouble();
        posY = buf.readDouble();
        posZ = buf.readDouble();
        radius = buf.readFloat();
        particleCount = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(posX);
        buf.writeDouble(posY);
        buf.writeDouble(posZ);
        buf.writeFloat(radius);
        buf.writeInt(particleCount);
    }

    public static class Handler implements IMessageHandler<PacketAOEParticleSpawn, IMessage> {
        @Override
        public IMessage onMessage(final PacketAOEParticleSpawn message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                com.x4yi.hammersunbound.HammersUnbound.proxy.handleAOEParticleSpawn(message.posX, message.posY, message.posZ, message.radius, message.particleCount);
            }
            return null;
        }
    }
}
