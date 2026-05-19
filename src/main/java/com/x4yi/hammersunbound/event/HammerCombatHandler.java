package com.x4yi.hammersunbound.event;

import com.x4yi.hammersunbound.capability.IBleedingCapability;
import com.x4yi.hammersunbound.capability.IBloodPactCapability;
import com.x4yi.hammersunbound.config.SpikeHammerConfig;
import com.x4yi.hammersunbound.network.ModNetworkHandler;
import com.x4yi.hammersunbound.network.PacketBleedingSync;
import com.x4yi.hammersunbound.network.PacketBloodPactVisual;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class HammerCombatHandler {

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entity = event.getEntityLiving();
        if (entity == null || entity.isDead || entity.world.isRemote) return;

        if (IBleedingCapability.CAPABILITY != null && entity.hasCapability(IBleedingCapability.CAPABILITY, null)) {
            IBleedingCapability bleedingCap = entity.getCapability(IBleedingCapability.CAPABILITY, null);
            if (bleedingCap != null && bleedingCap.getBleedingEffect() != null && bleedingCap.getBleedingEffect().isActive()) {
                bleedingCap.getBleedingEffect().tick(entity);
            }
        }

        if (IBloodPactCapability.CAPABILITY != null && entity.hasCapability(IBloodPactCapability.CAPABILITY, null)) {
            IBloodPactCapability pactCap = entity.getCapability(IBloodPactCapability.CAPABILITY, null);
            if (pactCap != null && pactCap.getBloodPactEffect() != null && pactCap.getBloodPactEffect().isActive()) {
                if (entity instanceof EntityPlayer) {
                    pactCap.getBloodPactEffect().tick((EntityPlayer) entity);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        EntityLivingBase target = event.getEntityLiving();
        if (target == null || target.isDead) return;

        if (IBleedingCapability.CAPABILITY != null && target.hasCapability(IBleedingCapability.CAPABILITY, null)) {
            IBleedingCapability cap = target.getCapability(IBleedingCapability.CAPABILITY, null);
            if (cap != null && cap.getBleedingEffect() != null && cap.getBleedingEffect().isActive()) {
                if (event.getSource().getTrueSource() instanceof EntityPlayerMP) {
                    EntityPlayerMP player = (EntityPlayerMP) event.getSource().getTrueSource();
                    ModNetworkHandler.INSTANCE.sendTo(
                            new PacketBleedingSync(target.getEntityId(), cap.getBleedingEffect().getLevel()),
                            player
                    );
                }
            }
        }

        if (IBloodPactCapability.CAPABILITY != null && target.hasCapability(IBloodPactCapability.CAPABILITY, null)) {
            IBloodPactCapability cap = target.getCapability(IBloodPactCapability.CAPABILITY, null);
            if (cap != null && cap.getBloodPactEffect() != null && cap.getBloodPactEffect().isActive()) {
                EntityPlayer player = cap.getBloodPactEffect().getPlayer();
                if (player != null && player instanceof EntityPlayerMP) {
                    ModNetworkHandler.INSTANCE.sendTo(
                            new PacketBloodPactVisual(player.getEntityId(), target.getEntityId(), true),
                            (EntityPlayerMP) player
                    );
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        EntityPlayer player = event.player;
        if (player == null) return;

        if (IBloodPactCapability.CAPABILITY != null && player.hasCapability(IBloodPactCapability.CAPABILITY, null)) {
            IBloodPactCapability cap = player.getCapability(IBloodPactCapability.CAPABILITY, null);
            if (cap != null && cap.getBloodPactEffect() != null && cap.getBloodPactEffect().isActive()) {
                cap.getBloodPactEffect().deactivate();
            }
        }
    }
}
