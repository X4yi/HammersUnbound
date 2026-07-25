package com.x4yi.hammersunbound.network;
import com.x4yi.hammersunbound.config.ServerConfig;
import com.x4yi.hammersunbound.config.SpikeHammerConfig;
import com.x4yi.hammersunbound.config.WarHammerConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
public class PacketRequestSyncConfig implements IMessage {
    public PacketRequestSyncConfig() {}
    @Override
    public void fromBytes(ByteBuf buf) {}
    @Override
    public void toBytes(ByteBuf buf) {}
    public static class Handler implements IMessageHandler<PacketRequestSyncConfig, IMessage> {
        @Override
        public IMessage onMessage(PacketRequestSyncConfig message, MessageContext ctx) {
            if (ctx.side != Side.SERVER) return null;
            EntityPlayerMP player = ctx.getServerHandler().player;
            if (player == null) return null;
            player.getServerWorld().addScheduledTask(() -> {
                JsonObject itemsJson = new JsonObject();
                JsonObject whMats = new JsonObject();
                for (java.util.Map.Entry<String, WarHammerConfig.WarHammerMaterialEntry> entry : WarHammerConfig.getAllMaterials().entrySet()) {
                    whMats.add(entry.getKey(), entry.getValue().toJson());
                }
                JsonObject warhammerJson = new JsonObject();
                warhammerJson.add("materials", whMats);
                itemsJson.add("warhammer", warhammerJson);
                JsonObject shMats = new JsonObject();
                for (java.util.Map.Entry<String, SpikeHammerConfig.SpikeHammerMaterialEntry> entry : SpikeHammerConfig.getAllMaterials().entrySet()) {
                    shMats.add(entry.getKey(), entry.getValue().toJson());
                }
                JsonObject spikehammerJson = new JsonObject();
                spikehammerJson.add("materials", shMats);
                itemsJson.add("spikehammer", spikehammerJson);
                JsonObject serverJson = new JsonObject();
                serverJson.addProperty("configVersion", com.x4yi.hammersunbound.HammersUnbound.VERSION);
                JsonObject whServer = new JsonObject();
                whServer.addProperty("stunDurationMultiplier", ServerConfig.warhammerStunDurationMultiplier);
                whServer.addProperty("enableAOE", ServerConfig.warhammerEnableAOE);
                whServer.addProperty("enableStun", ServerConfig.warhammerEnableStun);
                serverJson.add("warhammer", whServer);
                JsonObject shServer = new JsonObject();
                shServer.addProperty("bleedingDamageMultiplier", ServerConfig.spikehammerBleedingDamageMultiplier);
                shServer.addProperty("bleedingDurationMultiplier", ServerConfig.spikehammerBleedingDurationMultiplier);
                shServer.addProperty("bloodPactRangeMultiplier", ServerConfig.spikehammerBloodPactRangeMultiplier);
                shServer.addProperty("bloodPactDrainMultiplier", ServerConfig.spikehammerBloodPactDrainMultiplier);
                shServer.addProperty("enableBleeding", ServerConfig.spikehammerEnableBleeding);
                shServer.addProperty("enableBloodPact", ServerConfig.spikehammerEnableBloodPact);
                serverJson.add("spikehammer", shServer);
                Gson gson = new Gson();
                String itemsStr = gson.toJson(itemsJson);
                String serverStr = gson.toJson(serverJson);
                ModNetworkHandler.INSTANCE.sendTo(new PacketSyncConfig(itemsStr, serverStr), player);
            });
            return null;
        }
    }
}