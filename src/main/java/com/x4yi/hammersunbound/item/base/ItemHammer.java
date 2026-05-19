package com.x4yi.hammersunbound.item.base;

import com.google.common.collect.Multimap;
import com.x4yi.hammersunbound.config.HammerMaterialData;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class ItemHammer extends net.minecraft.item.Item {

    protected static final UUID ATTACK_DAMAGE_MODIFIER = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    protected static final UUID ATTACK_SPEED_MODIFIER = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");

    protected final String materialName;
    protected final HammerMaterialData materialData;
    protected final float attackDamage;
    protected final float attackSpeed;
    protected final int maxDurability;

    private static final DecimalFormat DAMAGE_FORMAT = new DecimalFormat("#.#");
    private static final DecimalFormat SPEED_FORMAT = new DecimalFormat("#.#");

    public ItemHammer(String materialName, HammerMaterialData data) {
        this.materialName = materialName;
        this.materialData = data;
        this.attackDamage = data.baseDamage;
        this.attackSpeed = data.attackSpeed;
        this.maxDurability = data.durability;

        this.setMaxDamage(data.durability);
        this.setMaxStackSize(1);
    }

    public float getAttackDamage() {
        return attackDamage;
    }

    public float getAttackSpeed() {
        return attackSpeed;
    }

    public String getMaterialName() {
        return materialName;
    }

    public HammerMaterialData getMaterialData() {
        return materialData;
    }

    public abstract String getHammerType();

    public abstract void onCriticalHit(EntityLivingBase target, EntityLivingBase attacker, ItemStack stack);

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (attacker instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) attacker;
            if (isCriticalHit(player)) {
                onCriticalHit(target, attacker, stack);
            }
        }
        stack.damageItem(1, attacker);
        return true;
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
        Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);

        if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                    new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", attackDamage, 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
                    new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", attackSpeed, 0));
        }

        return multimap;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (FMLCommonHandler.instance().getEffectiveSide() != Side.CLIENT) return;

        tooltip.add(TextFormatting.GREEN + "Damage: " + DAMAGE_FORMAT.format(attackDamage));

        float displaySpeed = attackSpeed + 4.0F;
        tooltip.add(TextFormatting.YELLOW + "Speed: " + SPEED_FORMAT.format(displaySpeed));

        int currentDurability = stack.getMaxDamage() - stack.getItemDamage();
        tooltip.add(TextFormatting.GRAY + "Durability: " + currentDurability + "/" + stack.getMaxDamage());

        if (flagIn.isAdvanced()) {
            tooltip.add(TextFormatting.DARK_GRAY + "Material: " + materialName);
            tooltip.add(TextFormatting.DARK_GRAY + "Type: " + getHammerType());
        }

        addAbilityTooltips(tooltip);
    }



    protected void addAbilityTooltips(List<String> tooltip) {
        tooltip.add("");
        tooltip.add(TextFormatting.GOLD + "Special Abilities:");

        if (this instanceof com.x4yi.hammersunbound.item.warhammer.WarHammerItem) {
            tooltip.add(TextFormatting.RED + "  Critical Hit: AOE Damage + Stun");
        } else if (this instanceof com.x4yi.hammersunbound.item.spikehammer.SpikeHammerItem) {
            tooltip.add(TextFormatting.DARK_RED + "  Sprint Hit: Applies Bleeding");
            tooltip.add(TextFormatting.DARK_RED + "  Critical Hit: Enhanced Bleeding");
            tooltip.add(TextFormatting.DARK_PURPLE + "  Right Click: Blood Pact");
        }
    }

    @Override
    public boolean isFull3D() {
        return true;
    }

    @Override
    public boolean canHarvestBlock(IBlockState blockIn) {
        return false;
    }

    @Override
    public float getDestroySpeed(ItemStack stack, IBlockState state) {
        return 0.5f;
    }

    @Override
    public int getItemEnchantability() {
        return 10;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    protected boolean isCriticalHit(EntityPlayer player) {
        return player.fallDistance > 0.0F
                && !player.onGround
                && !player.isOnLadder()
                && !player.isInWater()
                && !player.isPotionActive(net.minecraft.init.MobEffects.BLINDNESS)
                && player.getRidingEntity() == null;
    }

    protected boolean isSprintHit(EntityPlayer player) {
        return player.isSprinting();
    }

    protected List<EntityLivingBase> getEntitiesInRadius(World world, Vec3d center, float radius, EntityLivingBase... exclude) {
        List<EntityLivingBase> result = new ArrayList<>();
        if (world == null || center == null || radius <= 0) return result;

        AxisAlignedBB aabb = new AxisAlignedBB(
                center.x - radius, center.y - radius, center.z - radius,
                center.x + radius, center.y + radius, center.z + radius
        );

        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
        for (EntityLivingBase entity : entities) {
            if (entity == null || entity.isDead) continue;

            boolean shouldExclude = false;
            for (EntityLivingBase ex : exclude) {
                if (entity == ex) {
                    shouldExclude = true;
                    break;
                }
            }
            if (shouldExclude) continue;

            if (entity instanceof net.minecraft.entity.passive.EntityTameable) {
                net.minecraft.entity.passive.EntityTameable tameable = (net.minecraft.entity.passive.EntityTameable) entity;
                if (tameable.isTamed() && exclude.length > 0 && tameable.getOwner() == exclude[0]) continue;
            }

            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                if (player.capabilities.isCreativeMode) continue;
            }

            result.add(entity);
        }

        return result;
    }
}
