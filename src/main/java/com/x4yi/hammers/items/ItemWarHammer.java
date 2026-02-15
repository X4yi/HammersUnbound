package com.x4yi.hammers.items;

import com.google.common.collect.Multimap;
import com.x4yi.hammers.config.HammersUnboundItems;
import com.x4yi.hammers.config.Items;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
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
        this.setMaxDamage(getCfg().durability);
        setCreativeTab(CreativeTabs.COMBAT);
    }

    @Override
    public boolean hitEntity(ItemStack stack, @Nonnull EntityLivingBase target,
                             @Nonnull EntityLivingBase attacker) {
        stack.damageItem(1, attacker);
        return true;
    }

    public HammersUnboundItems.WarHammer getCfg() {
        switch (this.type) {
            case WOOD: return Items.warhammer.WOOD;
            case STONE: return Items.warhammer.STONE;
            case IRON: return Items.warhammer.IRON;
            case GOLD: return Items.warhammer.GOLD;
            default: return Items.warhammer.DIAMOND;
        }
    }

    @Override
    public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot slot) {
        Multimap<String, AttributeModifier> map = super.getItemAttributeModifiers(slot);

        if (slot == EntityEquipmentSlot.MAINHAND) {
            map.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(),
                    new AttributeModifier(DAMAGE_UUID, "WarHammer damage", this.getCfg().damage, 0));
            map.put(SharedMonsterAttributes.ATTACK_SPEED.getName(),
                    new AttributeModifier(SPEED_UUID, "WarHammer speed", this.getCfg().speed - 4.0f, 0));
        }

        return map;
    }

    @Override
    public void addInformation(ItemStack stack, World world, List<String> tooltip, ITooltipFlag flag) {
        hideModifiers(stack);
        HammersUnboundItems.WarHammer cfg = getCfg();

        tooltip.add(TextFormatting.RED + "Damage: " + TextFormatting.WHITE + cfg.damage);
        tooltip.add(TextFormatting.YELLOW + "Attack Speed: " + TextFormatting.WHITE + cfg.speed);
        tooltip.add(TextFormatting.GOLD + "Reach: " + TextFormatting.WHITE + "+" + cfg.reach);
        tooltip.add("");

        if (GuiScreen.isShiftKeyDown()) {
            tooltip.add(TextFormatting.DARK_PURPLE + "Stun Info");
            tooltip.add(TextFormatting.GRAY + " • AOE Radius: " + TextFormatting.WHITE + cfg.aoeRadius);
            tooltip.add(TextFormatting.GRAY + " • Stun Duration: " + TextFormatting.WHITE + (cfg.stunDuration / 20f) + "s");
            tooltip.add(TextFormatting.GRAY + " • Cooldown: " + TextFormatting.WHITE + (cfg.stunCooldown / 20f) + "s");
        } else {
            tooltip.add(TextFormatting.DARK_GRAY + "Hold " + TextFormatting.WHITE + "Shift" +
                    TextFormatting.DARK_GRAY + " for Stun details");
        }

        tooltip.add("");
        tooltip.add(TextFormatting.GRAY + "Durability: " +
                (stack.getMaxDamage() - stack.getItemDamage()) + "/" + stack.getMaxDamage());
    }

    private static void hideModifiers(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new net.minecraft.nbt.NBTTagCompound());
        }
        stack.getTagCompound().setInteger("HideFlags", 2);
    }
    @Mod.EventBusSubscriber
    public static class ReachHandler {
        @SubscribeEvent
        public static void onAttackEntity(AttackEntityEvent event) {
            ItemStack main = event.getEntityPlayer().getHeldItemMainhand();
            if (main.isEmpty() || !(main.getItem() instanceof ItemWarHammer)) return;

            ItemWarHammer hammer = (ItemWarHammer) main.getItem();
            double maxReach = 3.0 + hammer.getCfg().reach;

            if (event.getTarget().getDistance(event.getEntityPlayer()) > maxReach) {
                event.setCanceled(true);
            }
        }
    }
        public enum Type {
            WOOD, STONE, IRON, GOLD, DIAMOND
        }
    }

