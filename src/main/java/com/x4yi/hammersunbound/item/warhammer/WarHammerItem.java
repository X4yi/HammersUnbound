package com.x4yi.hammersunbound.item.warhammer;
import com.x4yi.hammersunbound.config.HammerMaterialData;
import com.x4yi.hammersunbound.config.ServerConfig;
import com.x4yi.hammersunbound.config.WarHammerConfig;
import com.x4yi.hammersunbound.init.ModCreativeTabs;
import com.x4yi.hammersunbound.item.base.ItemHammer;
import com.x4yi.hammersunbound.network.ModNetworkHandler;
import com.x4yi.hammersunbound.network.PacketAOEParticleSpawn;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import java.util.List;
public class WarHammerItem extends ItemHammer {
    public WarHammerItem(String materialName, HammerMaterialData data) {
        super(materialName, data);
        setCreativeTab(ModCreativeTabs.HAMMERS_UNBOUND);
    }
    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        stack.damageItem(1, attacker);
        return true;
    }
    @Override
    public void onCriticalHit(EntityLivingBase target, EntityLivingBase attacker, ItemStack stack) {
        if (!(attacker instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) attacker;
        if (player.getCooldownTracker().hasCooldown(this)) return;
        WarHammerConfig.WarHammerMaterialEntry entry = WarHammerConfig.getMaterial(materialName);
        if (entry == null) return;
        if (entry.data.skillCooldown > 0) {
            player.getCooldownTracker().setCooldown(this, entry.data.skillCooldown);
        }
        if (ServerConfig.warhammerEnableStun) {
            int stunDuration = (int) (entry.abilities.stunDuration * ServerConfig.warhammerStunDurationMultiplier);
            applyStunToTarget(target, stunDuration, entry.abilities.stunAmplifier);
        }
        if (ServerConfig.warhammerEnableAOE) {
            float aoeRadius = entry.abilities.aoeRadius;
            float aoeDamage = entry.abilities.aoeDamage;
            int scaledAoeStun = (int) (entry.abilities.aoeStunDuration * ServerConfig.warhammerStunDurationMultiplier);
            applyAOEDamage(player, target, aoeRadius, aoeDamage,
                    scaledAoeStun, entry.abilities.aoeStunAmplifier);
        }
        spawnAOEParticles(player, target, entry.abilities.aoeRadius);
    }
    private void applyStunToTarget(EntityLivingBase target, int duration, int amplifier) {
        if (target == null || target.isDead) return;
        if (target instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) target;
            if (player.capabilities.isCreativeMode) return;
        }
        if (com.x4yi.hammersunbound.init.ModPotions.STUN != null) {
            target.addPotionEffect(new PotionEffect(com.x4yi.hammersunbound.init.ModPotions.STUN, duration, amplifier));
        }
    }
    private void applyAOEDamage(EntityPlayer attacker, EntityLivingBase primaryTarget,
                                float radius, float aoeDamage, int aoeStunDuration, int aoeStunAmplifier) {
        if (attacker == null || attacker.world == null) return;
        Vec3d center = new Vec3d(primaryTarget.posX, primaryTarget.posY, primaryTarget.posZ);
        List<EntityLivingBase> entities = getEntitiesInRadius(attacker.world, center, radius, primaryTarget, attacker);
        for (EntityLivingBase entity : entities) {
            entity.attackEntityFrom(DamageSource.causePlayerDamage(attacker), aoeDamage);
            if (aoeStunDuration > 0 && ServerConfig.warhammerEnableStun) {
                if (com.x4yi.hammersunbound.init.ModPotions.STUN != null) {
                    entity.addPotionEffect(new PotionEffect(com.x4yi.hammersunbound.init.ModPotions.STUN, aoeStunDuration, aoeStunAmplifier));
                }
            }
        }
    }
    private void spawnAOEParticles(EntityPlayer player, EntityLivingBase target, float radius) {
        if (player.world == null || player.world.isRemote) return;
        player.world.playSound(null, target.posX, target.posY, target.posZ,
                net.minecraft.init.SoundEvents.ENTITY_GENERIC_EXPLODE, net.minecraft.util.SoundCategory.PLAYERS,
                1.0F, 1.5F + player.world.rand.nextFloat() * 0.2F);
        net.minecraft.util.math.BlockPos pos = new net.minecraft.util.math.BlockPos(target.posX, target.posY - 0.2, target.posZ);
        net.minecraft.block.state.IBlockState state = player.world.getBlockState(pos);
        if (state.getBlock() != net.minecraft.init.Blocks.AIR) {
            net.minecraft.util.SoundEvent breakSound = state.getBlock().getSoundType(state, player.world, pos, null).getBreakSound();
            player.world.playSound(null, target.posX, target.posY, target.posZ,
                    breakSound, net.minecraft.util.SoundCategory.BLOCKS,
                    2.0F, 0.8F + player.world.rand.nextFloat() * 0.2F);
        }
        int particleCount = Math.max(48, (int) Math.ceil(radius * 28.0f));
        PacketAOEParticleSpawn packet = new PacketAOEParticleSpawn(
                target.posX, target.posY, target.posZ,
                radius, particleCount
        );
        double range = ServerConfig.serverAoeParticleSyncDistance;
        for (EntityPlayerMP nearby : player.world.getEntitiesWithinAABB(EntityPlayerMP.class,
                new net.minecraft.util.math.AxisAlignedBB(
                        target.posX - range, target.posY - range, target.posZ - range,
                        target.posX + range, target.posY + range, target.posZ + range
                ))) {
            ModNetworkHandler.INSTANCE.sendTo(packet, nearby);
        }
    }
    @Override
    public String getHammerType() {
        return "warhammer";
    }
}