package com.x4yi.hammers.config;

import net.minecraftforge.common.config.Config;

@Config(modid = "hammersunbound", name = "HammersUnbound")
public class HammerConfig {

    @Config.LangKey("config.hammers.client")
    public static Client client = new Client();

    @Config.LangKey("config.hammers.materials")
    public static WarMaterials warhammer = new WarMaterials();

    @Config.LangKey("config.hammers.spikehammer")
    public static SpikeMaterials spikehammer = new SpikeMaterials();


    public static class Client {

        public boolean AOEparticlesEnabled = true;
        public int AOEparticlesDensity = 10;
        public boolean BleedParticlesEnabled = true;
        public int BleedParticlesDensity = 6;
    }


    public static class WarHammer {
        public float damage;
        public float speed;
        public float aoeRadius;
        public int stunDuration;
        public int stunCooldown;
        public int durability;

        public WarHammer(float damage, float speed, float aoeRadius, int stunDuration, int stunCooldown, int durability) {
            this.damage = damage;
            this.speed = speed;
            this.aoeRadius = aoeRadius;
            this.stunDuration = stunDuration;
            this.stunCooldown = stunCooldown;
            this.durability = durability;
        }
    }

    public static class WarMaterials {
        public WarHammer WOOD = new WarHammer(5.0f, 1.2f, 1.5f, 40, 120, 100);
        public WarHammer STONE = new WarHammer(6.0f, 1.1f, 2.0f, 50, 150, 180);
        public WarHammer IRON = new WarHammer(8.0f, 1.0f, 3.0f, 60, 180, 350);
        public WarHammer GOLD = new WarHammer(11.0f, 0.6f, 5.0f, 100, 300, 70);
        public WarHammer DIAMOND = new WarHammer(10.0f, 0.85f, 4.0f, 80, 240, 850);
    }

    public static class SpikeHammer {

        public float damage;
        public float speed;
        public int durability;

        public int maxLevel;
        public int baseDuration;
        public int minInterval;
        public int maxInterval;

        public SpikeHammer(
                float damage,
                float speed,
                int durability,
                int maxLevel,
                int baseDuration,
                int minInterval,
                int maxInterval
        ) {
            this.damage = damage;
            this.speed = speed;
            this.durability = durability;
            this.maxLevel = maxLevel;
            this.baseDuration = baseDuration;
            this.minInterval = minInterval;
            this.maxInterval = maxInterval;
        }
    }

    public static class SpikeMaterials {
        public SpikeHammer WOOD = new SpikeHammer(4.0f, 1.6f, 100, 3, 120, 40, 60);
        public SpikeHammer STONE = new SpikeHammer(5.0f, 1.4f, 160, 4, 140, 35, 60);
        public SpikeHammer IRON = new SpikeHammer(6.5f, 1.2f, 300, 5, 160, 30, 55);
        public SpikeHammer GOLD = new SpikeHammer(8.0f, 1.0f, 60, 6, 130, 20, 45);
        public SpikeHammer DIAMOND = new SpikeHammer(7.5f, 1.1f, 700, 6, 200, 15, 40);
    }
}
