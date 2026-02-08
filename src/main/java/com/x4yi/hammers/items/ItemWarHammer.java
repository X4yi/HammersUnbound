package com.x4yi.hammers.items;

import com.google.common.collect.Multimap;
import com.x4yi.hammers.config.HammerConfig;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class ItemWarHammer extends Item {

    public final Type type;

    private static final UUID DAMAGE_UUID =
            UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
    private static final UUID SPEED_UUID =
            UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");

    public ItemWarHammer(Type type) {
        this.type = type;
        this.setMaxStackSize(1);
        this.setMaxDamage(this.getCfg().durability);
    }

    public HammerConfig.WarHammer getCfg() {
        switch (this.type) {
            case WOOD:  return HammerConfig.warhammer.WOOD;
            case STONE: return HammerConfig.warhammer.STONE;
            case IRON:  return HammerConfig.warhammer.IRON;
            case GOLD:  return HammerConfig.warhammer.GOLD;
            default:    return HammerConfig.warhammer.DIAMOND;
        }
    }

    @Override
    public Multimap<String, AttributeModifier>
    getItemAttributeModifiers(EntityEquipmentSlot slot) {

        Multimap<String, AttributeModifier> map =
                super.getItemAttributeModifiers(slot);

        if (slot == EntityEquipmentSlot.MAINHAND) {
            map.put(
                    SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                    new AttributeModifier(
                            DAMAGE_UUID,
                            "WarHammer damage",
                            this.getCfg().damage,
                            0
                    )
            );

            map.put(
                    SharedMonsterAttributes.ATTACK_SPEED.getName(),
                    new AttributeModifier(
                            SPEED_UUID,
                            "WarHammer speed",
                            this.getCfg().speed - 4.0f,
                            0
                    )
            );
        }

        return map;
    }

    @Override
    public void addInformation(
            ItemStack stack,
            World world,
            List<String> tooltip,
            ITooltipFlag flag
    ) {
        HammerConfig.WarHammer cfg = this.getCfg();

        tooltip.add(TextFormatting.BLUE + " " + cfg.damage + " Attack Damage");
        tooltip.add(TextFormatting.BLUE + " " + cfg.speed + " Attack Speed");

        tooltip.add(
                TextFormatting.GRAY + " Durability: " +
                        (stack.getMaxDamage() - stack.getItemDamage()) +
                        "/" + stack.getMaxDamage()
        );
    }

    public enum Type {
        WOOD, STONE, IRON, GOLD, DIAMOND
    }
}
