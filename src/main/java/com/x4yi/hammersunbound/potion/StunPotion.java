package com.x4yi.hammersunbound.potion;

import com.x4yi.hammersunbound.HammersUnbound;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;

public class StunPotion extends Potion {

    public StunPotion() {
        super(true, 0x808080); // isBadEffect = true, liquidColor = gray (neutral)
        setPotionName("effect.hammersunbound.stun");
        setRegistryName(new ResourceLocation(HammersUnbound.MODID, "stun"));
        
        // Operation 2: multiply current speed by (1 + amount).
        // amount = -1.0D reduces speed by 100%.
        this.registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "a9a16f2c-cbbe-4f4c-bc4a-e4559812ea11", -1.0D, 2);
    }

    @Override
    public boolean hasStatusIcon() {
        return false;
    }

    @Override
    public boolean shouldRender(PotionEffect effect) {
        return false;
    }

    @Override
    public boolean shouldRenderInvText(PotionEffect effect) {
        return false;
    }

    @Override
    public boolean shouldRenderHUD(PotionEffect effect) {
        return false;
    }
}
