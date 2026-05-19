package com.x4yi.hammersunbound.client.particle;

import com.x4yi.hammersunbound.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

@SideOnly(Side.CLIENT)
public class AOEParticleSpawner {

    private static final Random RANDOM = new Random();

    public static void spawnAOEParticles(World world, Vec3d center, float radius, int particleCount) {
        if (world == null || !world.isRemote) return;
        if (!ClientConfig.aoeEnabled) return;

        int adjustedCount = (int) (particleCount * ClientConfig.aoeParticleDensity);
        int tiers = Math.max(3, (int) radius);
        float maxHeight = ClientConfig.aoeMaxHeight;

        for (int tier = 0; tier < tiers; tier++) {
            double tierRadius = (radius / tiers) * (tier + 1);
            double heightMultiplier = 1.0 - (tier / (double) tiers);
            double targetHeight = 0.5 + heightMultiplier * maxHeight;

            int particlesPerTier = adjustedCount / tiers;

            for (int i = 0; i < particlesPerTier; i++) {
                double angle = RANDOM.nextDouble() * Math.PI * 2;
                double dist = tierRadius * (0.5 + RANDOM.nextDouble() * 0.5);
                double x = center.x + Math.cos(angle) * dist;
                double z = center.z + Math.sin(angle) * dist;
                double y = center.y + RANDOM.nextDouble() * 0.5;

                ParticleHammerAOE particle = new ParticleHammerAOE(
                        world, x, y, z,
                        center.x, center.z, radius,
                        targetHeight,
                        30 + RANDOM.nextFloat() * 20
                );

                Minecraft.getMinecraft().effectRenderer.addEffect(particle);
            }
        }
    }
}
