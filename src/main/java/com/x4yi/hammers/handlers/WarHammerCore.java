package com.x4yi.hammers.handlers;

import com.x4yi.hammers.config.HammerConfig;
import com.x4yi.hammers.items.ItemWarHammer;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class WarHammerCore {

    private static final String STUN_CD = "WHStunCD";

    @SubscribeEvent
    public static void onCrit(CriticalHitEvent e) {
        if (!e.isVanillaCritical()) return;
        if (!(e.getTarget() instanceof EntityLivingBase)) return;

        EntityPlayer player = e.getEntityPlayer();
        ItemStack stack = player.getHeldItemMainhand();

        if (!(stack.getItem() instanceof ItemWarHammer)) return;

        ItemWarHammer hammer = (ItemWarHammer) stack.getItem();
        HammerConfig.WarHammer cfg = hammer.getCfg();

        if (onCooldown(stack, player)) return;

        setCooldown(stack, player, cfg.stunCooldown);
        applyAOE(player, (EntityLivingBase) e.getTarget(), cfg);
    }

    private static void applyAOE(EntityPlayer player, EntityLivingBase hit, HammerConfig.WarHammer cfg) {
        AxisAlignedBB box = new AxisAlignedBB(
                hit.posX - cfg.aoeRadius,
                hit.posY - 1.0,
                hit.posZ - cfg.aoeRadius,
                hit.posX + cfg.aoeRadius,
                hit.posY + 2.0,
                hit.posZ + cfg.aoeRadius
        );

        List<EntityLivingBase> entities =
                player.world.getEntitiesWithinAABB(EntityLivingBase.class, box);

        for (EntityLivingBase e : entities) {
            if (e == player) continue;

            e.attackEntityFrom(DamageSource.causePlayerDamage(player), cfg.damage);
            e.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, cfg.stunDuration, 20, false, false));
            e.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, cfg.stunDuration, 255, false, false));
        }

        if (HammerConfig.client.AOEparticlesEnabled && player.world.isRemote) {
            spawnParticles(hit, cfg.aoeRadius);
        }
    }

    private static void spawnParticles(EntityLivingBase center, float radius) {
        if (!center.world.isRemote) return;

        int count = (int) (radius * radius * HammerConfig.client.AOEparticlesDensity);

        for (int i = 0; i < count; i++) {
            double rx = (center.world.rand.nextDouble() * 2 - 1) * radius;
            double rz = (center.world.rand.nextDouble() * 2 - 1) * radius;

            if (rx * rx + rz * rz > radius * radius) continue;

            int y = MathHelper.floor(center.posY);
            BlockPos pos = new BlockPos(center.posX + rx, y, center.posZ + rz);

            IBlockState state = center.world.getBlockState(pos.down());

            if (state.getBlock() == Blocks.AIR) {
                for (int dy = 1; dy <= 3; dy++) {
                    state = center.world.getBlockState(pos.up(dy));
                    if (state.getBlock() != Blocks.AIR) {
                        pos = pos.up(dy);
                        break;
                    }
                }
                if (state.getBlock() == Blocks.AIR) continue;
            }

            center.world.spawnParticle(
                    EnumParticleTypes.BLOCK_CRACK,
                    center.posX + rx,
                    pos.getY() + 0.1,
                    center.posZ + rz,
                    0.0, 0.0, 0.0,
                    Block.getStateId(state)
            );
        }
    }

    private static boolean onCooldown(ItemStack stack, EntityPlayer player) {
        return stack.getOrCreateSubCompound("WH")
                .getLong(STUN_CD) > player.world.getTotalWorldTime();
    }

    private static void setCooldown(ItemStack stack, EntityPlayer player, int ticks) {
        long end = player.world.getTotalWorldTime() + ticks;

        stack.getOrCreateSubCompound("WH")
                .setLong(STUN_CD, end);

        player.getCooldownTracker()
                .setCooldown(stack.getItem(), ticks);
    }
}
