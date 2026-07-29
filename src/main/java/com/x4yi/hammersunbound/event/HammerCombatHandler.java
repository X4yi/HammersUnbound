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
        if (entity == null || entity.isDead) return;
        boolean hasStun = com.x4yi.hammersunbound.init.ModPotions.STUN != null && entity.isPotionActive(com.x4yi.hammersunbound.init.ModPotions.STUN);
        if (!hasStun && !entity.hasCapability(IBleedingCapability.CAPABILITY, null) && !entity.hasCapability(IBloodPactCapability.CAPABILITY, null)) return;
        if (hasStun) {
            if (!entity.world.isRemote) {
                net.minecraft.nbt.NBTTagCompound data = entity.getEntityData();
                if (!data.hasKey("StunnedRotationYaw")) {
                    data.setFloat("StunnedRotationYaw", entity.rotationYaw);
                    data.setFloat("StunnedRotationPitch", entity.rotationPitch);
                    data.setFloat("StunnedRotationYawHead", entity.rotationYawHead);
                    data.setFloat("StunnedRenderYawOffset", entity.renderYawOffset);
                }
                float yaw = data.getFloat("StunnedRotationYaw");
                float pitch = data.getFloat("StunnedRotationPitch");
                float yawHead = data.getFloat("StunnedRotationYawHead");
                float renderOffset = data.getFloat("StunnedRenderYawOffset");
                entity.rotationYaw = yaw;
                entity.prevRotationYaw = yaw;
                entity.rotationPitch = pitch;
                entity.prevRotationPitch = pitch;
                entity.rotationYawHead = yawHead;
                entity.prevRotationYawHead = yawHead;
                entity.renderYawOffset = renderOffset;
                entity.prevRenderYawOffset = renderOffset;
                entity.motionX = 0;
                entity.motionZ = 0;
                if (entity.motionY > 0.0) {
                    entity.motionY = 0.0;
                }
                if (!(entity instanceof EntityPlayer)) {
                    if (entity instanceof net.minecraft.entity.EntityLiving) {
                        net.minecraft.entity.EntityLiving living = (net.minecraft.entity.EntityLiving) entity;
                        living.getNavigator().clearPath();
                        if (living.getAttackTarget() != null) {
                            living.setAttackTarget(null);
                        }
                    }
                    entity.moveForward = 0.0F;
                    entity.moveVertical = 0.0F;
                    entity.moveStrafing = 0.0F;
                    entity.limbSwing = 0;
                    entity.limbSwingAmount = 0;
                    entity.prevLimbSwingAmount = 0;
                }
            }
        } else {
            if (!entity.world.isRemote) {
                net.minecraft.nbt.NBTTagCompound data = entity.getEntityData();
                if (data.hasKey("StunnedRotationYaw")) {
                    data.removeTag("StunnedRotationYaw");
                    data.removeTag("StunnedRotationPitch");
                    data.removeTag("StunnedRotationYawHead");
                    data.removeTag("StunnedRenderYawOffset");
                }
            }
        }
        if (entity.world.isRemote) return;
        if (IBleedingCapability.CAPABILITY != null) {
            IBleedingCapability bleedingCap = entity.getCapability(IBleedingCapability.CAPABILITY, null);
            if (bleedingCap != null && bleedingCap.getBleedingEffect() != null && bleedingCap.getBleedingEffect().isActive()) {
                bleedingCap.getBleedingEffect().tick(entity);
            }
        }
        if (IBloodPactCapability.CAPABILITY != null) {
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
        if (IBleedingCapability.CAPABILITY != null) {
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
        if (IBloodPactCapability.CAPABILITY != null) {
            net.minecraft.entity.Entity attacker = event.getSource().getTrueSource();
            if (attacker instanceof EntityPlayer) {
                EntityPlayer playerAttacking = (EntityPlayer) attacker;
                IBloodPactCapability playerCap = playerAttacking.getCapability(IBloodPactCapability.CAPABILITY, null);
                if (playerCap != null && playerCap.getBloodPactEffect() != null && playerCap.getBloodPactEffect().isActive()) {
                    if (playerCap.getBloodPactEffect().getTargetEntityIds().contains(target.getEntityId())) {
                        int currMadness = playerCap.getBloodPactEffect().getMadness();
                        playerCap.getBloodPactEffect().setMadness(Math.min(100, currMadness + 10));
                        playerCap.getBloodPactEffect().addAccumulatedDamage(event.getAmount());
                        int currentTimer = playerCap.getBloodPactEffect().getBurstTimer();
                        playerCap.getBloodPactEffect().setBurstTimer(Math.max(0, currentTimer - 10));
                        playerCap.getBloodPactEffect().addDurationBonus();
                        if (playerCap.getBloodPactEffect().getPingPongTargetId() == target.getEntityId() &&
                            playerCap.getBloodPactEffect().getPingPongPhase() == 2) {
                            event.setAmount(event.getAmount() * 1.5F);
                            playerCap.getBloodPactEffect().startPingPong(target);
                        }
                    }
                }
            }
            if (target instanceof EntityPlayer) {
                EntityPlayer playerHurt = (EntityPlayer) target;
                IBloodPactCapability playerCap = playerHurt.getCapability(IBloodPactCapability.CAPABILITY, null);
                if (playerCap != null && playerCap.getBloodPactEffect() != null && playerCap.getBloodPactEffect().isActive()) {
                    if (attacker instanceof EntityLivingBase && playerCap.getBloodPactEffect().getTargetEntityIds().contains(attacker.getEntityId())) {
                        playerCap.getBloodPactEffect().subtractDurationPenalty();
                        if (playerCap.getBloodPactEffect().getPingPongTargetId() == attacker.getEntityId() &&
                            playerCap.getBloodPactEffect().getPingPongPhase() > 0) {
                            playerCap.getBloodPactEffect().cancelPingPong();
                        }
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        EntityPlayer player = event.player;
        if (player == null) return;
        if (IBloodPactCapability.CAPABILITY != null) {
            IBloodPactCapability cap = player.getCapability(IBloodPactCapability.CAPABILITY, null);
            if (cap != null && cap.getBloodPactEffect() != null && cap.getBloodPactEffect().isActive()) {
                cap.getBloodPactEffect().deactivate();
            }
        }
    }
    @SubscribeEvent
    public void onPlayerClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        EntityPlayer oldPlayer = event.getOriginal();
        if (oldPlayer == null) return;
        if (IBloodPactCapability.CAPABILITY != null) {
            IBloodPactCapability cap = oldPlayer.getCapability(IBloodPactCapability.CAPABILITY, null);
            if (cap != null && cap.getBloodPactEffect() != null && cap.getBloodPactEffect().isActive()) {
                cap.getBloodPactEffect().deactivate();
            }
        }
    }
    @SubscribeEvent
    public void onCriticalHit(net.minecraftforge.event.entity.player.CriticalHitEvent event) {
        EntityPlayer player = event.getEntityPlayer();
        net.minecraft.entity.Entity target = event.getTarget();
        if (player == null || target == null || !(target instanceof EntityLivingBase)) return;
        net.minecraft.item.ItemStack stack = player.getHeldItemMainhand();
        if (stack.isEmpty() || !(stack.getItem() instanceof com.x4yi.hammersunbound.item.base.ItemHammer)) return;
        com.x4yi.hammersunbound.item.base.ItemHammer hammer = (com.x4yi.hammersunbound.item.base.ItemHammer) stack.getItem();
        boolean isCrit = event.isVanillaCritical() || event.getResult() == net.minecraftforge.fml.common.eventhandler.Event.Result.ALLOW;
        boolean isFullyCharged = player.getCooledAttackStrength(0.5F) > 0.9F;
        boolean isSprint = player.isSprinting() && isFullyCharged;
        if (isCrit) {
            if (hammer instanceof com.x4yi.hammersunbound.item.warhammer.WarHammerItem) {
                ((com.x4yi.hammersunbound.item.warhammer.WarHammerItem) hammer).onCriticalHit((EntityLivingBase) target, player, stack);
            } else if (hammer instanceof com.x4yi.hammersunbound.item.spikehammer.SpikeHammerItem) {
                if (com.x4yi.hammersunbound.config.ServerConfig.spikehammerEnableBleeding) {
                    ((com.x4yi.hammersunbound.item.spikehammer.SpikeHammerItem) hammer).onCriticalHit((EntityLivingBase) target, player, stack);
                }
            }
        } else if (isSprint) {
            if (hammer instanceof com.x4yi.hammersunbound.item.spikehammer.SpikeHammerItem) {
                if (com.x4yi.hammersunbound.config.ServerConfig.spikehammerEnableBleeding) {
                    ((com.x4yi.hammersunbound.item.spikehammer.SpikeHammerItem) hammer).applyBleeding((EntityLivingBase) target);
                }
            }
        }
    }
}