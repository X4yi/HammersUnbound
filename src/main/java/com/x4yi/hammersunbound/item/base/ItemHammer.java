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
        if ("warhammer".equals(getHammerType())) {
            com.x4yi.hammersunbound.config.WarHammerConfig.WarHammerMaterialEntry entry = com.x4yi.hammersunbound.config.WarHammerConfig.getMaterial(this.materialName);
            if (entry != null && entry.data != null) {
                return entry.data.baseDamage;
            }
        } else if ("spikehammer".equals(getHammerType())) {
            com.x4yi.hammersunbound.config.SpikeHammerConfig.SpikeHammerMaterialEntry entry = com.x4yi.hammersunbound.config.SpikeHammerConfig.getMaterial(this.materialName);
            if (entry != null && entry.data != null) {
                return entry.data.baseDamage;
            }
        }
        return attackDamage;
    }
    public float getAttackSpeed() {
        if ("warhammer".equals(getHammerType())) {
            com.x4yi.hammersunbound.config.WarHammerConfig.WarHammerMaterialEntry entry = com.x4yi.hammersunbound.config.WarHammerConfig.getMaterial(this.materialName);
            if (entry != null && entry.data != null) {
                return entry.data.attackSpeed;
            }
        } else if ("spikehammer".equals(getHammerType())) {
            com.x4yi.hammersunbound.config.SpikeHammerConfig.SpikeHammerMaterialEntry entry = com.x4yi.hammersunbound.config.SpikeHammerConfig.getMaterial(this.materialName);
            if (entry != null && entry.data != null) {
                return entry.data.attackSpeed;
            }
        }
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
        stack.damageItem(1, attacker);
        return true;
    }
    @Override
    public int getMaxDamage(ItemStack stack) {
        if ("warhammer".equals(getHammerType())) {
            com.x4yi.hammersunbound.config.WarHammerConfig.WarHammerMaterialEntry entry = com.x4yi.hammersunbound.config.WarHammerConfig.getMaterial(this.materialName);
            if (entry != null && entry.data != null) {
                return entry.data.durability;
            }
        } else if ("spikehammer".equals(getHammerType())) {
            com.x4yi.hammersunbound.config.SpikeHammerConfig.SpikeHammerMaterialEntry entry = com.x4yi.hammersunbound.config.SpikeHammerConfig.getMaterial(this.materialName);
            if (entry != null && entry.data != null) {
                return entry.data.durability;
            }
        }
        return this.maxDurability;
    }
    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
        Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(equipmentSlot);
        if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            float currentDamage = getAttackDamage();
            float currentSpeed = getAttackSpeed();
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                    new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", currentDamage, 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
                    new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", currentSpeed, 0));
        }
        return multimap;
    }
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (FMLCommonHandler.instance().getEffectiveSide() != Side.CLIENT)
            return;
        float currentDamage = getAttackDamage();
        float currentSpeed = getAttackSpeed();
        tooltip.add(TextFormatting.DARK_GREEN + "Damage: " + DAMAGE_FORMAT.format(currentDamage));
        float displaySpeed = currentSpeed + 4.0F;
        tooltip.add(TextFormatting.DARK_GRAY + "Speed: " + SPEED_FORMAT.format(displaySpeed));
        int maxDur = getMaxDamage(stack);
        int currentDurability = maxDur - stack.getItemDamage();
        tooltip.add(TextFormatting.GRAY + "Durability: " + currentDurability + "/" + maxDur);
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
    protected List<EntityLivingBase> getEntitiesInRadius(World world, Vec3d center, float radius,
            EntityLivingBase... exclude) {
        List<EntityLivingBase> result = new ArrayList<>();
        if (world == null || center == null || radius <= 0)
            return result;
        AxisAlignedBB aabb = new AxisAlignedBB(
                center.x - radius, center.y - radius, center.z - radius,
                center.x + radius, center.y + radius, center.z + radius);
        List<EntityLivingBase> entities = world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
        for (EntityLivingBase entity : entities) {
            if (entity == null || entity.isDead)
                continue;
            boolean shouldExclude = false;
            for (EntityLivingBase ex : exclude) {
                if (entity == ex) {
                    shouldExclude = true;
                    break;
                }
            }
            if (shouldExclude)
                continue;
            if (entity instanceof net.minecraft.entity.passive.EntityTameable) {
                net.minecraft.entity.passive.EntityTameable tameable = (net.minecraft.entity.passive.EntityTameable) entity;
                if (tameable.isTamed() && exclude.length > 0 && tameable.getOwner() == exclude[0])
                    continue;
            }
            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                if (player.capabilities.isCreativeMode)
                    continue;
            }
            result.add(entity);
        }
        return result;
    }
}