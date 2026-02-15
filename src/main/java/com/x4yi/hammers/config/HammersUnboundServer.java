package com.x4yi.hammers.config;

import net.minecraftforge.common.config.Config;

@Config(
        modid = "hammersunbound",
        name = "HammersUnbound/HammersUnboundServer",
        type = Config.Type.INSTANCE
)
public class HammersUnboundServer {

    public static Server server = new Server();
    public static class Server {

        @Config.RangeDouble(min = 4.0, max = 256.0)
        @Config.RequiresWorldRestart
        @Config.Comment("Maximum distance to send AOE particle packets .Requires World Restart")
        public double aoeParticleSendDistance = 48.0;

        @Config.RangeDouble(min = 1, max = 100)
        @Config.RequiresWorldRestart
        @Config.Comment("Minimum distance of ground to made Smash")
        public double minFallDistanceSmash = 6;

        @Config.RangeDouble(min = 1, max = 100)
        @Config.RequiresWorldRestart
        @Config.Comment("Minimum distance of ground to made BetterSmash")
        public double minFallDistanceBetterSmash = 5;

        @Config.RangeDouble(min = 0.0, max = 10.0)
        @Config.RequiresWorldRestart
        @Config.Comment("Fall amplification damage multiplier. Requires World Restart")
        public double fallSmashDamageMultiplier = 1.4;

        @Config.RangeDouble(min = 0.0, max = 5.0)
        @Config.RequiresWorldRestart
        @Config.Comment("Fall amplification damage BetterSmash multiplier. Requires World Restart")
        public Double fallBetterSmashDamageMultiplier = 2.0;

        @Config.RangeDouble(min = 0.0, max = 10.0)
        @Config.RequiresWorldRestart
        @Config.Comment("Fall amplification AOE radius multiplier. Requires World Restart")
        public double fallRadiusMultiplier = 1.3;

        @Config.RangeDouble(min = 0.0, max = 10.0)
        @Config.RequiresWorldRestart
        @Config.Comment("Fall amplification particle density multiplier. Requires World Restart")
        public double fallParticleMultiplier = 1.8;

        @Config.RangeDouble(min = 0.0, max = 10.0)
        @Config.RequiresWorldRestart
        @Config.Comment("Fall amplification particle density multiplier. Requires World Restart")
        public double fallScaleCap = 2;
    }
}
