package com.x4yi.hammersunbound.item.spikehammer;

import com.x4yi.hammersunbound.capability.IBleedingCapability;
import com.x4yi.hammersunbound.capability.IBloodPactCapability;
import com.x4yi.hammersunbound.config.BleedingConfig;
import com.x4yi.hammersunbound.config.BloodPactConfig;
import com.x4yi.hammersunbound.config.HammerMaterialData;
import com.x4yi.hammersunbound.config.ServerConfig;
import com.x4yi.hammersunbound.config.SpikeHammerConfig;
import com.x4yi.hammersunbound.init.ModCreativeTabs;
import com.x4yi.hammersunbound.item.base.ItemHammer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class SpikeHammerItem extends ItemHammer {

    public SpikeHammerItem(String materialName, HammerMaterialData data) {
        super(materialName, data);
        setCreativeTab(ModCreativeTabs.HAMMERS_UNBOUND);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (attacker instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) attacker;
            if (ServerConfig.spikehammerEnableBloodPact) {
                applyBloodPactDamage(target, player);
            }
        }

        stack.damageItem(1, attacker);
        return true;
    }

    @Override
    public void onCriticalHit(EntityLivingBase target, EntityLivingBase attacker, ItemStack stack) {
        if (ServerConfig.spikehammerEnableBleeding) {
            applyBleeding(target);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if (!ServerConfig.spikehammerEnableBloodPact) {
            return new ActionResult<>(EnumActionResult.PASS, player.getHeldItem(hand));
        }

        ItemStack stack = player.getHeldItem(hand);
        SpikeHammerConfig.SpikeHammerMaterialEntry entry = SpikeHammerConfig.getMaterial(materialName);
        if (entry == null) return new ActionResult<>(EnumActionResult.PASS, stack);

        BloodPactConfig config = entry.bloodPact;
        float adjustedRange = config.range * ServerConfig.spikehammerBloodPactRangeMultiplier;
        EntityLivingBase target = findTargetInFront(player, adjustedRange);

        if (target != null) {
            activateBloodPact(player, target);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }

        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    private EntityLivingBase findTargetInFront(EntityPlayer player, float range) {
        if (range <= 0) return null;

        Vec3d look = player.getLook(1.0F);
        Vec3d start = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3d end = start.addVector(look.x * range, look.y * range, look.z * range);

        AxisAlignedBB aabb = new AxisAlignedBB(
                Math.min(start.x, end.x) - 1.0, Math.min(start.y, end.y) - 1.0, Math.min(start.z, end.z) - 1.0,
                Math.max(start.x, end.x) + 1.0, Math.max(start.y, end.y) + 1.0, Math.max(start.z, end.z) + 1.0
        );

        List<EntityLivingBase> entities = player.world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
        EntityLivingBase closest = null;
        double closestDist = range;

        for (EntityLivingBase entity : entities) {
            if (entity == null || entity.isDead || entity == player) continue;
            double dist = player.getDistance(entity);
            if (dist < closestDist) {
                closestDist = dist;
                closest = entity;
            }
        }

        return closest;
    }

    public void applyBleeding(EntityLivingBase target) {
        if (target == null || target.isDead) return;

        if (target.hasCapability(IBleedingCapability.CAPABILITY, null)) {
            IBleedingCapability cap = target.getCapability(IBleedingCapability.CAPABILITY, null);
            if (cap != null) {
                SpikeHammerConfig.SpikeHammerMaterialEntry entry = SpikeHammerConfig.getMaterial(materialName);
                if (entry != null) {
                    BleedingConfig adjusted = new BleedingConfig(
                            entry.bleeding.maxLevels,
                            (int) (entry.bleeding.baseDuration * ServerConfig.spikehammerBleedingDurationMultiplier),
                            entry.bleeding.damagePerLevel * ServerConfig.spikehammerBleedingDamageMultiplier,
                            entry.bleeding.tickInterval,
                            entry.bleeding.decayTicks
                    );
                    cap.getBleedingEffect().apply(target, adjusted);
                }
            }
        }
    }

    private void activateBloodPact(EntityPlayer player, EntityLivingBase target) {
        if (target == null || target.isDead || player == null) return;

        if (player.hasCapability(IBloodPactCapability.CAPABILITY, null)) {
            IBloodPactCapability cap = player.getCapability(IBloodPactCapability.CAPABILITY, null);
            if (cap != null) {
                SpikeHammerConfig.SpikeHammerMaterialEntry entry = SpikeHammerConfig.getMaterial(materialName);
                if (entry != null) {
                    cap.getBloodPactEffect().activate(player, target, entry.bloodPact);
                }
            }
        }
    }

    private void applyBloodPactDamage(EntityLivingBase target, EntityPlayer player) {
        if (target == null || player == null) return;

        if (player.hasCapability(IBloodPactCapability.CAPABILITY, null)) {
            IBloodPactCapability cap = player.getCapability(IBloodPactCapability.CAPABILITY, null);
            if (cap != null && cap.getBloodPactEffect().isActive()) {
                cap.getBloodPactEffect().onHitTarget(attackDamage);
            }
        }
    }

    @Override
    public String getHammerType() {
        return "spikehammer";
    }
}
