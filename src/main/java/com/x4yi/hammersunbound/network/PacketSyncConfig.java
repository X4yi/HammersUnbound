package com.x4yi.hammersunbound.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketSyncConfig implements IMessage {

    private String itemsJson;
    private String serverJson;

    public PacketSyncConfig() {}

    public PacketSyncConfig(String itemsJson, String serverJson) {
        this.itemsJson = itemsJson;
        this.serverJson = serverJson;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        itemsJson = ByteBufUtils.readUTF8String(buf);
        serverJson = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, itemsJson);
        ByteBufUtils.writeUTF8String(buf, serverJson);
    }

    public static class Handler implements IMessageHandler<PacketSyncConfig, IMessage> {
        @Override
        public IMessage onMessage(PacketSyncConfig message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                com.x4yi.hammersunbound.HammersUnbound.proxy.handleConfigSync(message.itemsJson, message.serverJson);
            }
            return null;
        }
    }
}
