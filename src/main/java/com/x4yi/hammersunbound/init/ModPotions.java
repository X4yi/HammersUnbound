package com.x4yi.hammersunbound.init;
import com.x4yi.hammersunbound.HammersUnbound;
import com.x4yi.hammersunbound.potion.StunPotion;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
@Mod.EventBusSubscriber(modid = HammersUnbound.MODID)
public class ModPotions {
    public static Potion STUN;
    @SubscribeEvent
    public static void onPotionRegister(RegistryEvent.Register<Potion> event) {
        IForgeRegistry<Potion> registry = event.getRegistry();
        STUN = new StunPotion();
        registry.register(STUN);
    }
}