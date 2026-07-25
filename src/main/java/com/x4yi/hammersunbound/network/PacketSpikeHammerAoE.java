package com.x4yi.hammersunbound.network;
import io.netty.buffer.ByteBuf;
import com.x4yi.hammersunbound.capability.IBloodPactCapability;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
public class PacketSpikeHammerAoE implements IMessage {
    private int targetEntityId;
    public PacketSpikeHammerAoE() {}
    public PacketSpikeHammerAoE(int targetEntityId) {
        this.targetEntityId = targetEntityId;
    }
    @Override
    public void fromBytes(ByteBuf buf) {
        targetEntityId = buf.readInt();
    }
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(targetEntityId);
    }
    public static class Handler implements IMessageHandler<PacketSpikeHammerAoE, IMessage> {
        @Override
        public IMessage onMessage(PacketSpikeHammerAoE message, MessageContext ctx) {
            if (ctx.side == net.minecraftforge.fml.relauncher.Side.SERVER) {
                net.minecraft.entity.player.EntityPlayerMP player = ctx.getServerHandler().player;
                player.getServerWorld().addScheduledTask(() -> {
                    net.minecraft.item.ItemStack held = player.getHeldItemMainhand();
                    if (!held.isEmpty() && held.getItem() instanceof com.x4yi.hammersunbound.item.spikehammer.SpikeHammerItem) {
                        ((com.x4yi.hammersunbound.item.spikehammer.SpikeHammerItem) held.getItem()).performAoE(player, message.targetEntityId);
                    }
                });
            }
            return null;
        }
    }
}