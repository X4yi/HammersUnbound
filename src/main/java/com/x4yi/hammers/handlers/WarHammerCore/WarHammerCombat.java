package com.x4yi.hammers.handlers.WarHammerCore;

import com.x4yi.hammers.config.HammersUnboundItems;
import com.x4yi.hammers.config.HammersUnboundServer;
import com.x4yi.hammers.items.ItemWarHammer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public final class WarHammerCombat {
    private static final String CRIT_MARK = "WHCrit";
    private static final DamageSource AOE =
            new DamageSource("wh_aoe").setDamageBypassesArmor();

    @SubscribeEvent
    public static void onCriticalMark(CriticalHitEvent e) {
        if (e.isVanillaCritical() && e.getTarget() instanceof EntityLivingBase) {
            e.getEntityPlayer().getEntityData().setBoolean(CRIT_MARK, true);
        }
    }
    @SubscribeEvent
    public static void onCritical(LivingHurtEvent e) {

        if (e.getSource() == AOE) return;
        if (!(e.getSource().getTrueSource() instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) e.getSource().getTrueSource();
        if (player.world.isRemote) return;

        if (!player.getEntityData().getBoolean(CRIT_MARK)) return;
        player.getEntityData().removeTag(CRIT_MARK);

        ItemStack stack = player.getHeldItemMainhand();
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemWarHammer)) return;
        if (player.isSneaking()) return;
        if (player.getCooldownTracker().hasCooldown(stack.getItem())) return;

        ItemWarHammer hammer = (ItemWarHammer) stack.getItem();
        HammersUnboundItems.WarHammer cfg = hammer.getCfg();

        float fall = Math.max(player.fallDistance, 0.1F);

        double scale = 1 + Math.sqrt(fall) *
                HammersUnboundServer.server.fallSmashDamageMultiplier * 1D;
        scale = Math.min(scale,
                HammersUnboundServer.server.fallScaleCap * 0.75D);
        float directDamage = (float) (cfg.damage * scale);
        float radius = (float) (cfg.aoeRadius *
                (1D + (scale - 1D) *
                        HammersUnboundServer.server.fallRadiusMultiplier * 0.4D));
        e.setAmount(directDamage);

        EntityLivingBase target = e.getEntityLiving();

        WarHammerStun.apply(target, cfg.stunDuration);
        applyAOE(player, target, radius, directDamage, cfg.stunDuration);
        WarHammerParticles.send(
                target,
                radius,
                (float) scale,
                WarHammerParticles.ImpactType.CRIT
        );
        player.getCooldownTracker().setCooldown(stack.getItem(), cfg.stunCooldown);
        player.fallDistance = 0F;
    }

    @SubscribeEvent
    public static void onFall(LivingFallEvent e) {

        if (!(e.getEntity() instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) e.getEntity();
        if (player.world.isRemote) return;
        if (!player.isSneaking()) return;

        ItemStack stack = player.getHeldItemMainhand();
        if (stack.isEmpty() || !(stack.getItem() instanceof ItemWarHammer)) return;
        if (player.getCooldownTracker().hasCooldown(stack.getItem())) return;

        float fall = player.fallDistance;

        if (fall < HammersUnboundServer.server.minFallDistanceSmash) return;

        ItemWarHammer hammer = (ItemWarHammer) stack.getItem();
        HammersUnboundItems.WarHammer cfg = hammer.getCfg();

        boolean betterSmash =
                fall >= HammersUnboundServer.server.minFallDistanceBetterSmash;

        double scale;

        if(betterSmash){
            scale = 1 + Math.pow(fall,0.75D) *
                    HammersUnboundServer.server.fallBetterSmashDamageMultiplier;
        }else{
            scale = 1 + Math.sqrt(fall) *
                    HammersUnboundServer.server.fallSmashDamageMultiplier;
        }

        scale=Math.min(scale,HammersUnboundServer.server.fallScaleCap);

        float radius = (float) (cfg.aoeRadius *
                (1D + (scale - 1D) * HammersUnboundServer.server.fallRadiusMultiplier));

        float damage = (float) (cfg.damage * scale);
        int stun = betterSmash ? cfg.stunDuration * 2 : cfg.stunDuration;

        e.setCanceled(true);

        applyAOE(player, player, radius, damage, stun);
        WarHammerParticles.send(
                player,
                radius,
                (float) scale,
                betterSmash
                        ? WarHammerParticles.ImpactType.BETTER_SMASH
                        : WarHammerParticles.ImpactType.SMASH
        );

        player.getCooldownTracker().setCooldown(stack.getItem(), cfg.stunCooldown);
        player.fallDistance = 0F;
    }
    private static void applyAOE(EntityPlayer player,
                                 EntityLivingBase center,
                                 float radius,
                                 float damage,
                                 int stun){

        AxisAlignedBB box=center.getEntityBoundingBox().grow(radius);

        for(EntityLivingBase entity:player.world.getEntitiesWithinAABB(
                EntityLivingBase.class,
                box,
                e->e!=player&&e.isEntityAlive())){

            double dist=entity.getDistance(center);

            if(dist>radius)continue;

            double factor=1-(dist/radius);
            float finalDamage=damage*(float)factor;

            if(finalDamage<=0)continue;

            entity.attackEntityFrom(AOE,finalDamage);
            WarHammerStun.apply(entity,stun);

            entity.motionY+=0.15D*factor;
            entity.velocityChanged=true;
        }
    }

}
