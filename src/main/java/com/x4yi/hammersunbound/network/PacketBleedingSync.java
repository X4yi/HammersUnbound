package com.x4yi.hammersunbound.network;
import io.netty.buffer.ByteBuf;
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
            if (ctx.side == net.minecraftforge.fml.relauncher.Side.CLIENT) {
                com.x4yi.hammersunbound.HammersUnbound.proxy.handleBleedingSync(message.entityId, message.bleedingLevel);
            }
            return null;
        }
    }
}