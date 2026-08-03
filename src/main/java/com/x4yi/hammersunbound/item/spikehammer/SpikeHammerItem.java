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
        if (attacker.world.isRemote) return;
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
        if (player.hasCapability(IBloodPactCapability.CAPABILITY, null)) {
            IBloodPactCapability cap = player.getCapability(IBloodPactCapability.CAPABILITY, null);
            if (cap != null && cap.getBloodPactEffect() != null && cap.getBloodPactEffect().isActive()) {
                EntityLivingBase target = findTargetInFront(player, adjustedRange);
                if (target != null && cap.getBloodPactEffect().getTargetEntityIds().contains(target.getEntityId())) {
                    cap.getBloodPactEffect().startPingPong(target);
                    return new ActionResult<>(EnumActionResult.SUCCESS, stack);
                } else {
                    player.getCooldownTracker().setCooldown(this, 200);
                    return new ActionResult<>(EnumActionResult.FAIL, stack);
                }
            }
        }
        EntityLivingBase target = findTargetInFront(player, adjustedRange);
        if (target != null) {
            activateBloodPact(player, target);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }
    private EntityLivingBase findTargetInFront(EntityPlayer player, float range) {
        if (range <= 0) return null;
        Vec3d start = player.getPositionEyes(1.0F);
        Vec3d look = player.getLook(1.0F);
        Vec3d end = start.addVector(look.x * range, look.y * range, look.z * range);
        EntityLivingBase closest = null;
        double closestDist = range * range;
        for (EntityLivingBase entity : player.world.getEntitiesWithinAABB(EntityLivingBase.class, player.getEntityBoundingBox().grow(range))) {
            if (entity == player || entity.isDead) continue;
            AxisAlignedBB aabb = entity.getEntityBoundingBox().grow(0.3F);
            net.minecraft.util.math.RayTraceResult intercept = aabb.calculateIntercept(start, end);
            if (intercept != null) {
                double dist = start.squareDistanceTo(intercept.hitVec);
                if (dist < closestDist) {
                    closestDist = dist;
                    closest = entity;
                }
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
                    com.x4yi.hammersunbound.event.HammerCombatHandler.activeBleedingEntities.add(target);
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
    public void performAoE(EntityPlayer player, int targetEntityId) {
        if (player.world.isRemote) return;
        long currentTick = player.world.getTotalWorldTime();
        if (player.hasCapability(com.x4yi.hammersunbound.capability.ICombatStateCapability.CAPABILITY, null)) {
            com.x4yi.hammersunbound.capability.ICombatStateCapability cap = player.getCapability(com.x4yi.hammersunbound.capability.ICombatStateCapability.CAPABILITY, null);
            if (cap != null) {
                long lastTrigger = cap.getLastSpikeHammerAoETick();
                if (currentTick - lastTrigger < 5) return;
                cap.setLastSpikeHammerAoETick(currentTick);
            }
        }
        SpikeHammerConfig.SpikeHammerMaterialEntry entry = SpikeHammerConfig.getMaterial(materialName);
        if (entry == null) return;
        EntityLivingBase target = null;
        if (targetEntityId != -1) {
            net.minecraft.entity.Entity e = player.world.getEntityByID(targetEntityId);
            if (e instanceof EntityLivingBase) {
                target = (EntityLivingBase) e;
            }
        }
        double aoeSize = (double) entry.bloodPact.aoeAttackSize;
        net.minecraft.util.math.Vec3d look = player.getLook(1.0F);
        double offset = 1.5D;
        double cx = player.posX + look.x * offset;
        double cy = player.posY + player.getEyeHeight() + look.y * offset;
        double cz = player.posZ + look.z * offset;
        net.minecraft.util.math.AxisAlignedBB aabb = new net.minecraft.util.math.AxisAlignedBB(
                cx - aoeSize, cy - aoeSize, cz - aoeSize,
                cx + aoeSize, cy + aoeSize, cz + aoeSize
        );
        java.util.List<EntityLivingBase> list = player.world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
        float damage = (float) player.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        for (EntityLivingBase entity : list) {
            if (entity != null && entity != player && (target == null || (entity != target && entity.getEntityId() != target.getEntityId())) && !entity.isDead) {
                entity.attackEntityFrom(net.minecraft.util.DamageSource.causePlayerDamage(player), damage);
            }
        }
    }
    @Override
    public String getHammerType() {
        return "spikehammer";
    }
}