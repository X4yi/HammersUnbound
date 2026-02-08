package com.x4yi.hammers.items;

import com.google.common.collect.Multimap;
import com.x4yi.hammers.config.HammerConfig;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;

import java.util.UUID;

public class ItemSpikeHammer extends Item {

    private static final UUID DAMAGE_UUID =
            UUID.fromString("cb3f55d3-645c-4f38-a497-9c13a33db5cf");
    private static final UUID SPEED_UUID =
            UUID.fromString("fa233e1c-4180-4865-b01b-bcce9785aca3");

    private final MaterialType type;

    public ItemSpikeHammer(MaterialType type) {
        this.type = type;

        HammerConfig.SpikeHammer cfg = getBaseCfg();
        setMaxStackSize(1);
        setMaxDamage(cfg.durability);
    }

    public HammerConfig.SpikeHammer getBaseCfg() {
        switch (type) {
            case WOOD:    return HammerConfig.spikehammer.WOOD;
            case STONE:   return HammerConfig.spikehammer.STONE;
            case IRON:    return HammerConfig.spikehammer.IRON;
            case GOLD:    return HammerConfig.spikehammer.GOLD;
            case DIAMOND: return HammerConfig.spikehammer.DIAMOND;
        }
        return HammerConfig.spikehammer.WOOD;
    }

    public SpikeCfg getSpikeCfg() {
        switch (type) {
            case WOOD:    return new SpikeCfg(HammerConfig.spikehammer.WOOD);
            case STONE:   return new SpikeCfg(HammerConfig.spikehammer.STONE);
            case IRON:    return new SpikeCfg(HammerConfig.spikehammer.IRON);
            case GOLD:    return new SpikeCfg(HammerConfig.spikehammer.GOLD);
            case DIAMOND: return new SpikeCfg(HammerConfig.spikehammer.DIAMOND);
        }
        return new SpikeCfg(HammerConfig.spikehammer.WOOD);
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
        Multimap<String, AttributeModifier> map = super.getItemAttributeModifiers(slot);

        if (slot == EntityEquipmentSlot.MAINHAND) {
            HammerConfig.SpikeHammer cfg = getBaseCfg();

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

        public SpikeCfg(HammerConfig.SpikeHammer s) {
            this.maxLevel = s.maxLevel;
            this.baseDuration = s.baseDuration;
            this.minInterval = s.minInterval;
            this.maxInterval = s.maxInterval;
        }
    }
}
