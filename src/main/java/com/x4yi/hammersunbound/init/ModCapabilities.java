package com.x4yi.hammersunbound.init;
import com.x4yi.hammersunbound.capability.IBleedingCapability;
import com.x4yi.hammersunbound.capability.IBloodPactCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
@Mod.EventBusSubscriber(modid = "hammersunbound")
public class ModCapabilities {
    private static final ResourceLocation BLEEDING_CAP = new ResourceLocation("hammersunbound", "bleeding");
    private static final ResourceLocation BLOODPACT_CAP = new ResourceLocation("hammersunbound", "bloodpact");
    public static void register() {
        CapabilityManager.INSTANCE.register(IBleedingCapability.class, new IBleedingCapability.BleedingCapabilityStorage(), IBleedingCapability.BleedingCapability.class);
        CapabilityManager.INSTANCE.register(IBloodPactCapability.class, new IBloodPactCapability.BloodPactCapabilityStorage(), IBloodPactCapability.BloodPactCapability.class);
    }
    @SubscribeEvent
    public static void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityLivingBase) {
            event.addCapability(BLEEDING_CAP, new IBleedingCapability.BleedingCapabilityProvider());
            event.addCapability(BLOODPACT_CAP, new IBloodPactCapability.BloodPactCapabilityProvider());
        }
    }
}