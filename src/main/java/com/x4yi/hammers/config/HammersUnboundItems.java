package com.x4yi.hammers.config;

import net.minecraftforge.common.config.Config;

public class HammersUnboundItems {

    @Config.Name("WarHammer")
    public static WarMaterials warhammer = new WarMaterials();

    @Config.Name("SpikeHammer")
    public static SpikeMaterials spikehammer = new SpikeMaterials();

    public static class WarHammer {

        @Config.RangeDouble(min = 0.0, max = 100.0)
        @Config.Comment("Base damage")
        public float damage;

        @Config.RangeDouble(min = 0.1, max = 5.0)
        @Config.Comment("Attack speed")
        public float speed;

        @Config.RangeDouble(min = 0.0, max = 30.0)
        @Config.Comment("Damage in area radius")
        public float aoeRadius;

        @Config.RangeInt(min = 0, max = 800)
        @Config.Comment("Stun duration in ticks")
        public int stunDuration;

        @Config.RangeInt(min = 0, max = 10000)
        @Config.Comment("Cooldown between stuns in ticks")
        public int stunCooldown;

        @Config.RangeInt(min = 1, max = 1000000)
        @Config.Comment("Durability of the hammer")
        public int durability;

        @Config.RangeDouble(min = -2, max = 6)
        @Config.Comment("I recommended don't touch")
        public double reach;

        public WarHammer(float damage, float speed, float aoeRadius,
                         int stunDuration, int stunCooldown, int durability, int reach) {

            this.damage = damage;
            this.speed = speed;
            this.aoeRadius = aoeRadius;
            this.stunDuration = stunDuration;
            this.stunCooldown = stunCooldown;
            this.durability = durability;
            this.reach = reach;
        }
    }

    public static class WarMaterials {

        public WarHammer WOOD = new WarHammer(5.0f, 1.2f, 1.5f, 40, 120, 100, 1);
        public WarHammer STONE = new WarHammer(6.0f, 1.1f, 2.0f, 50, 150, 180, 1);
        public WarHammer IRON = new WarHammer(8.0f, 1.0f, 3.0f, 60, 180, 350, 1);
        public WarHammer GOLD = new WarHammer(11.0f, 0.6f, 5.0f, 100, 300, 70, 1);
        public WarHammer DIAMOND = new WarHammer(10.0f, 0.85f, 4.0f, 80, 240, 850, 1);
    }

    public static class SpikeHammer {

        @Config.RangeDouble(min = 0.0, max = 100.0)
        @Config.Comment("Base damage")
        public float damage;

        @Config.RangeDouble(min = 0.1, max = 4.0)
        @Config.Comment("Attack speed")
        public float speed;

        @Config.RangeInt(min = 1, max = 10000)
        @Config.Comment("Durability")
        public int durability;

        @Config.RangeInt(min = 1, max = 10)
        @Config.Comment("Maximum bleeding level")
        public int maxLevel;

        @Config.RangeInt(min = 0, max = 400)
        @Config.Comment("Base duration in ticks")
        public int baseDuration;

        @Config.RangeInt(min = 0, max = 200)
        @Config.Comment("Minimum interval")
        public int minInterval;

        @Config.RangeInt(min = 0, max = 200)
        @Config.Comment("Maximum interval")
        public int maxInterval;

        public SpikeHammer(float damage, float speed, int durability,
                           int maxLevel, int baseDuration,
                           int minInterval, int maxInterval) {

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

        public SpikeHammer WOOD = new SpikeHammer(5.0f, 1.4f, 100, 3, 120, 40, 60);
        public SpikeHammer STONE = new SpikeHammer(6.0f, 1.3f, 160, 4, 140, 35, 60);
        public SpikeHammer IRON = new SpikeHammer(7.5f, 1.2f, 300, 5, 160, 30, 55);
        public SpikeHammer GOLD = new SpikeHammer(5.0f, 1.3f, 60, 6, 130, 20, 45);
        public SpikeHammer DIAMOND = new SpikeHammer(8.5f, 1.0f, 700, 6, 200, 15, 40);
    }
}
