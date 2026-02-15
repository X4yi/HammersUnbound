package com.x4yi.hammers.items;

import com.google.common.collect.Multimap;
import com.x4yi.hammers.config.HammersUnboundItems;
import com.x4yi.hammers.config.Items;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class ItemSpikeHammer extends Item {

    private static final UUID DAMAGE_UUID =
            UUID.fromString("cb3f55d3-645c-4f38-a497-9c13a33db5cf");
    private static final UUID SPEED_UUID =
            UUID.fromString("fa233e1c-4180-4865-b01b-bcce9785aca3");

    private final MaterialType type;

    public ItemSpikeHammer(MaterialType type) {
        this.type = type;

        HammersUnboundItems.SpikeHammer cfg = getBaseCfg();
        setMaxStackSize(1);
        setMaxDamage(cfg.durability);
        setCreativeTab(CreativeTabs.COMBAT);
    }

    @Override
    public boolean hitEntity(ItemStack stack, net.minecraft.entity.EntityLivingBase target,
                             net.minecraft.entity.EntityLivingBase attacker) {
        stack.damageItem(1, attacker);
        return true;
    }

    public HammersUnboundItems.SpikeHammer getBaseCfg() {
        switch (type) {
            case WOOD:
                return Items.spikehammer.WOOD;
            case STONE:
                return Items.spikehammer.STONE;
            case IRON:
                return Items.spikehammer.IRON;
            case GOLD:
                return Items.spikehammer.GOLD;
            case DIAMOND:
                return Items.spikehammer.DIAMOND;
        }
        return Items.spikehammer.DIAMOND;
    }

    public SpikeCfg getSpikeCfg() {
        return new SpikeCfg(getBaseCfg());
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
        Multimap<String, AttributeModifier> map = super.getItemAttributeModifiers(slot);

        if (slot == EntityEquipmentSlot.MAINHAND) {
            HammersUnboundItems.SpikeHammer cfg = getBaseCfg();

            map.put(
                    SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                    new AttributeModifier(DAMAGE_UUID, "SpikeHammer damage", cfg.damage, 0)
            );

            map.put(
                    SharedMonsterAttributes.ATTACK_SPEED.getName(),
                    new AttributeModifier(
                            SPEED_UUID,
                            "SpikeHammer speed",
                            cfg.speed - 4.0F,
                            0
                    )
            );
        }
        return map;
    }

    public MaterialType getMaterialType() {
        return this.type;
    }


    public enum MaterialType {
        WOOD, STONE, IRON, GOLD, DIAMOND
    }

    public static class SpikeCfg {
        public final int maxLevel;
        public final int baseDuration;
        public final int minInterval;
        public final int maxInterval;

        public SpikeCfg(HammersUnboundItems.SpikeHammer s) {
            this.maxLevel = s.maxLevel;
            this.baseDuration = s.baseDuration;
            this.minInterval = s.minInterval;
            this.maxInterval = s.maxInterval;
        }
    }

    @Override
    public void addInformation(ItemStack stack, net.minecraft.world.World world,
                               java.util.List<String> tooltip,
                               net.minecraft.client.util.ITooltipFlag flag) {

        hideModifiers(stack);

        HammersUnboundItems.SpikeHammer cfg = getBaseCfg();

        tooltip.add("§cDamage: §f" + cfg.damage);
        tooltip.add("§eAttack Speed: §f" + cfg.speed);

        tooltip.add("");

        if (net.minecraft.client.gui.GuiScreen.isShiftKeyDown()) {
            tooltip.add("§4Bleeding");
            tooltip.add(" §7• Max Level: §f" + cfg.maxLevel);
            tooltip.add(" §7• Duration: §f" + (cfg.baseDuration / 20f) + "s");
            tooltip.add(" §7• Interval: §f" +
                    cfg.minInterval + " - " + cfg.maxInterval + " ticks");
        } else {
            tooltip.add("§8Hold §fShift §8for bleeding details");
        }
    }

    private static void hideModifiers(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
        }
        stack.getTagCompound().setInteger("HideFlags", 2);
    }

}
