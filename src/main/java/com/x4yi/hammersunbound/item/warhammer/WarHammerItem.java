package com.x4yi.hammersunbound.item.warhammer;
import com.x4yi.hammersunbound.config.HammerMaterialData;
import com.x4yi.hammersunbound.config.ServerConfig;
import com.x4yi.hammersunbound.config.WarHammerConfig;
import com.x4yi.hammersunbound.init.ModCreativeTabs;
import com.x4yi.hammersunbound.item.base.ItemHammer;
import com.x4yi.hammersunbound.network.ModNetworkHandler;
import com.x4yi.hammersunbound.network.PacketAOEParticleSpawn;
import com.x4yi.hammersunbound.network.PacketSkybreaker;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumActionResult;
import net.minecraft.world.World;
import net.minecraft.util.SoundCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
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
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        ItemStack itemstack = playerIn.getHeldItem(handIn);
        if (worldIn.isRemote && playerIn.isSneaking() && playerIn.rotationPitch < -45.0f) {
            NBTTagCompound nbt = itemstack.getTagCompound();
            if (nbt == null) {
                nbt = new NBTTagCompound();
                itemstack.setTagCompound(nbt);
            }
            long currentTime = worldIn.getTotalWorldTime();
            long cooldownEnd = nbt.getLong("SkybreakerCooldown");
            if (currentTime >= cooldownEnd) {
                prepareSkybreakerLeap(playerIn);
                ModNetworkHandler.INSTANCE.sendToServer(new PacketSkybreaker());
                return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
            }
        }
        return new ActionResult<>(EnumActionResult.PASS, itemstack);
    }
    public void performSkybreaker(EntityPlayer playerIn, ItemStack itemstack) {
        if (playerIn.world.isRemote) return;
        NBTTagCompound nbt = itemstack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            itemstack.setTagCompound(nbt);
        }
        long currentTime = playerIn.world.getTotalWorldTime();
        long cooldownEnd = nbt.getLong("SkybreakerCooldown");
        if (currentTime >= cooldownEnd) {
            prepareSkybreakerLeap(playerIn);
            playerIn.velocityChanged = true;
            WarHammerConfig.WarHammerMaterialEntry entry = WarHammerConfig.getMaterial(materialName);
            long maxCooldown = (entry != null) ? entry.abilities.skybreakerCooldown : 600;
            nbt.setLong("SkybreakerCooldown", currentTime + maxCooldown);
        }
    }
    private void prepareSkybreakerLeap(EntityPlayer playerIn) {
        playerIn.getEntityData().setBoolean("SkybreakerJumpBuff", true);
        Vec3d look = playerIn.getLookVec();
        playerIn.motionX = look.x * 1.5;
        playerIn.motionY = 2.0;
        playerIn.motionZ = look.z * 1.5;
        playerIn.isAirBorne = true;
        playerIn.fallDistance = 0;
        playerIn.getEntityData().setBoolean("SkybreakerImmunity", true);
        playerIn.world.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_ENDERDRAGON_FLAP, SoundCategory.PLAYERS, 1.0F, 0.8F);
    }
    @Override
    public void onCriticalHit(EntityLivingBase target, EntityLivingBase attacker, ItemStack stack) {
        if (!(attacker instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) attacker;
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        long currentTime = player.world.getTotalWorldTime();
        long cooldownEnd = nbt.getLong("SkillCooldown");
        if (currentTime < cooldownEnd) return;
        WarHammerConfig.WarHammerMaterialEntry entry = WarHammerConfig.getMaterial(materialName);
        if (entry == null) return;
        if (entry.data.skillCooldown > 0) {
            nbt.setLong("SkillCooldown", currentTime + entry.data.skillCooldown);
        }
        float fallDistance = Math.max(0, player.fallDistance);
        float extraDamage = fallDistance * 1.0f;
        float extraRadius = fallDistance * 0.5f;
        int extraStun = (int) (fallDistance * 10);
        if (fallDistance > 1.5f) {
            player.fallDistance = 0;
            player.getEntityData().setBoolean("SkybreakerImmunity", true);
        }
        if (ServerConfig.warhammerEnableStun) {
            int stunDuration = (int) (entry.abilities.stunDuration * ServerConfig.warhammerStunDurationMultiplier) + extraStun;
            applyStunToTarget(target, stunDuration, entry.abilities.stunAmplifier);
        }
        if (ServerConfig.warhammerEnableAOE) {
            float aoeRadius = entry.abilities.aoeRadius + extraRadius;
            float aoeDamage = entry.abilities.aoeDamage + extraDamage;
            int scaledAoeStun = (int) (entry.abilities.aoeStunDuration * ServerConfig.warhammerStunDurationMultiplier) + extraStun;
            applyAOEDamage(player, target, aoeRadius, aoeDamage, scaledAoeStun, entry.abilities.aoeStunAmplifier);
        }
        spawnAOEParticles(player, target, entry.abilities.aoeRadius + extraRadius);
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
            if (entity == primaryTarget) continue;
            entity.hurtResistantTime = 0;
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
        int particleCount = (int) (radius * radius * 15.0f);
        particleCount = Math.max(48, particleCount);
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
    public void performGroundSlam(EntityPlayer player, float fallDistance) {
        if (player.world.isRemote) return;
        WarHammerConfig.WarHammerMaterialEntry entry = WarHammerConfig.getMaterial(materialName);
        if (entry == null) return;
        float baseRadius = entry.abilities.aoeRadius;
        float baseDamage = entry.abilities.aoeDamage;
        int baseStun = entry.abilities.aoeStunDuration;
        float slamRadius = baseRadius * 0.5f;
        float slamDamage = baseDamage * 0.5f;
        int slamStun = (int)(baseStun * 0.5f);
        slamRadius += (fallDistance * 0.5f) * 0.5f;
        slamDamage += (fallDistance * 1.0f) * 0.5f;
        slamStun += (int)((fallDistance * 10) * 0.5f);
        if (ServerConfig.warhammerEnableAOE) {
            applyAOEDamage(player, player, slamRadius, slamDamage, slamStun, entry.abilities.aoeStunAmplifier);
        }
        spawnAOEParticles(player, player, slamRadius);
    }
    @Override
    public String getHammerType() {
        return "warhammer";
    }
}