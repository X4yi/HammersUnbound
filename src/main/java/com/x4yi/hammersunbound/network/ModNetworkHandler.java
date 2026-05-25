package com.x4yi.hammersunbound.network;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class ModNetworkHandler {

    public static final SimpleNetworkWrapper INSTANCE = new SimpleNetworkWrapper("hammersunbound");

    public static void init() {
        INSTANCE.registerMessage(PacketBleedingSync.Handler.class, PacketBleedingSync.class, 0, Side.CLIENT);
        INSTANCE.registerMessage(PacketBloodPactVisual.Handler.class, PacketBloodPactVisual.class, 1, Side.CLIENT);
        INSTANCE.registerMessage(PacketAOEParticleSpawn.Handler.class, PacketAOEParticleSpawn.class, 2, Side.CLIENT);
        INSTANCE.registerMessage(PacketRequestSyncConfig.Handler.class, PacketRequestSyncConfig.class, 3, Side.SERVER);
        INSTANCE.registerMessage(PacketSyncConfig.Handler.class, PacketSyncConfig.class, 4, Side.CLIENT);
        INSTANCE.registerMessage(PacketBleedingParticle.Handler.class, PacketBleedingParticle.class, 5, Side.CLIENT);
    }
}
