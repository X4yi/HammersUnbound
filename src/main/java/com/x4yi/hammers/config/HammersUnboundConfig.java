package com.x4yi.hammers.config;

import net.minecraftforge.common.config.Config;

@Config(
        modid = "hammersunbound",
        name = "HammersUnbound/HammersUnbound"
)
public class HammersUnbound {

    public static Client client = new Client();

    public static class Client {

        public boolean aoeParticlesEnabled = true;
        public int aoeParticlesDensity = 10;

        public boolean bleedParticlesEnabled = true;
        public int bleedParticlesDensity = 8;

        public int bleedParticleGroundLife = 160;
        public boolean bleedParticleFade = true;

        public boolean showLinuxConfigHelper = true;
    }
}
