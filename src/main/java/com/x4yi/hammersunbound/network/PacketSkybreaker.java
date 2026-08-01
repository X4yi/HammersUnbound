package com.x4yi.hammersunbound.network;
import io.netty.buffer.ByteBuf;
import com.x4yi.hammersunbound.item.warhammer.WarHammerItem;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
public class PacketSkybreaker implements IMessage {
    public PacketSkybreaker() {}
    @Override
    public void fromBytes(ByteBuf buf) {}
    @Override
    public void toBytes(ByteBuf buf) {}
    public static class Handler implements IMessageHandler<PacketSkybreaker, IMessage> {
        @Override
        public IMessage onMessage(PacketSkybreaker message, MessageContext ctx) {
            if (ctx.side == net.minecraftforge.fml.relauncher.Side.SERVER) {
                net.minecraft.entity.player.EntityPlayerMP player = ctx.getServerHandler().player;
                player.getServerWorld().addScheduledTask(() -> {
                    net.minecraft.item.ItemStack held = player.getHeldItemMainhand();
                    if (!held.isEmpty() && held.getItem() instanceof WarHammerItem) {
                        ((WarHammerItem) held.getItem()).performSkybreaker(player, held);
                    }
                });
            }
            return null;
        }
    }
}