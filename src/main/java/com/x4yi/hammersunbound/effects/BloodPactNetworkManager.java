package com.x4yi.hammersunbound.effects;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

public class BloodPactNetworkManager {

    public static void syncToTrackingAndSelf(BloodPactEffect effect, EntityPlayer player) {
        if (player != null && !player.world.isRemote) {
            int[] targetsArr = effect.getTargetEntityIdsArray();
            com.x4yi.hammersunbound.network.PacketBloodPactVisual packetVisual = 
                new com.x4yi.hammersunbound.network.PacketBloodPactVisual(player.getEntityId(), targetsArr, effect.isActive());
            
            com.x4yi.hammersunbound.network.ModNetworkHandler.INSTANCE.sendToAllTracking(packetVisual, player);
            
            if (player instanceof EntityPlayerMP) {
                com.x4yi.hammersunbound.network.ModNetworkHandler.INSTANCE.sendTo(packetVisual, (EntityPlayerMP) player);
                com.x4yi.hammersunbound.network.PacketBloodPactSync packetSync = 
                    new com.x4yi.hammersunbound.network.PacketBloodPactSync(
                        effect.isActive(), targetsArr, effect.getRemainingTicks(), 
                        effect.getMadness(), effect.getBurstTimer(), effect.getAccumulatedDamage(), 
                        effect.getPingPongPhase(), effect.getPingPongTargetId(), effect.getBurstImpactTimer()
                    );
                com.x4yi.hammersunbound.network.ModNetworkHandler.INSTANCE.sendTo(packetSync, (EntityPlayerMP) player);
            }
        }
    }

    public static void sendDeactivation(EntityPlayer player) {
        if (player != null && !player.world.isRemote) {
            com.x4yi.hammersunbound.network.PacketBloodPactVisual packet = 
                new com.x4yi.hammersunbound.network.PacketBloodPactVisual(player.getEntityId(), new int[0], false);
            com.x4yi.hammersunbound.network.ModNetworkHandler.INSTANCE.sendToAllTracking(packet, player);
            
            if (player instanceof EntityPlayerMP) {
                com.x4yi.hammersunbound.network.ModNetworkHandler.INSTANCE.sendTo(packet, (EntityPlayerMP) player);
                com.x4yi.hammersunbound.network.PacketBloodPactSync packetSync = 
                    new com.x4yi.hammersunbound.network.PacketBloodPactSync(
                        false, new int[0], 0, 0, 200, 0.0f, 0, -1, 0
                    );
                com.x4yi.hammersunbound.network.ModNetworkHandler.INSTANCE.sendTo(packetSync, (EntityPlayerMP) player);
            }
        }
    }
}
