package com.x4yi.hammers;

import com.x4yi.hammers.handlers.WarHammerCore.WarHammerNetwork;
import com.x4yi.hammers.handlers.WarHammerCore.WarHammerStun;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;

@Mod(
        modid = HammersUnbound.MODID,
        name = "Hammers Unbound",
        version = "Beta-2-spikehammer-Configs",
        guiFactory = "com.x4yi.hammers.client.gui.HammersUnboundGuiFactory"
)
public class HammersUnbound {

    public static final String MODID = "hammersunbound";

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e){
        WarHammerStun.register();
        WarHammerNetwork.init();
    }

}
