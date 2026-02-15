package com.x4yi.hammers.config;

import net.minecraftforge.common.config.Config;

@Config(
        modid = "hammersunbound",
        name = "HammersUnbound/HammersUnbound"
)
public class HammersUnboundClient {

    public static Client client = new Client();

    public static class Client {

        @Config.Comment("Enable AOE particles")
        public boolean aoeParticlesEnabled = true;
        @Config.RangeInt(min = 1, max = 100)
        @Config.Comment("AOE particle density")
        public int aoeParticlesDensity = 2;
        @Config.RangeInt(min = 1, max = 10000)
        @Config.Comment("AOE particles max cap")
        public int aoeParticlesMaxCap = 160;
        @Config.Comment("Enable bleed particles")
        public boolean bleedParticlesEnabled = true;
        @Config.RangeInt(min = 1, max = 100)
        @Config.Comment("Bleed particle density")
        public int bleedParticlesDensity = 16;
        @Config.RangeInt(min = 0, max = 400)
        @Config.Comment("Bleed particle lifetime on ground")
        public int bleedParticleGroundLife = 240;
        @Config.Comment("Should bleed particles fade")
        public boolean bleedParticleFade = true;
    }
}
