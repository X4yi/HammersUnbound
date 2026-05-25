package com.x4yi.hammersunbound.client.particle;

import com.x4yi.hammersunbound.config.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.init.Blocks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

@SideOnly(Side.CLIENT)
public class AOEParticleSpawner {

    private static final Random RANDOM = new Random();

    public static void spawnAOEParticles(World world, Vec3d center, float radius, int particleCount) {
        spawnAOEParticles(world, center, radius, particleCount, ImpactType.NORMAL);
    }

    public static void spawnAOEParticles(World world, Vec3d center, float radius, int particleCount, ImpactType type) {
        if (world == null || !world.isRemote) return;
        if (!ClientConfig.aoeEnabled) return;

        int adjustedCount = (int) (particleCount * ClientConfig.aoeParticleCountMultiplier * ClientConfig.aoeParticleDensityMultiplier);
        if (adjustedCount <= 0) return;

        // Use MutableBlockPos to avoid object allocation in loop
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();

        int numRings = Math.max(3, (int) Math.ceil(radius / 0.75));
        double velocityMultiplier = ClientConfig.aoeParticleHeightMultiplier;

        int totalWeight = (numRings * (numRings + 1)) / 2;

        for (int ringIndex = 1; ringIndex <= numRings; ringIndex++) {
            double currentRadius = ((double) ringIndex / numRings) * radius;
            double heightGradient = 1.0 - ((ringIndex - 1.0) / Math.max(1.0, (numRings - 1.0))); // 1.0 at center, 0.0 at outer

            int ringParticles = (int) (adjustedCount * ((double) ringIndex / totalWeight));
            if (ringParticles <= 0) ringParticles = 1;

            for (int i = 0; i < ringParticles; i++) {
                double angle = ((double) i / ringParticles) * Math.PI * 2 + (RANDOM.nextDouble() - 0.5) * 0.2; // slight jitter

                double scale = 1.0;
                double gravity = 1.0;
                double friction = 0.98;
                int maxAge = 25;
                double outSpeed = 0.0;
                double motionY = 0.0;

                double roll = RANDOM.nextDouble();
                if (roll < 0.40) { // Debris Chunks
                    outSpeed = 0.05 + RANDOM.nextDouble() * 0.1;
                    motionY = (0.3 + RANDOM.nextDouble() * 0.4) * heightGradient * velocityMultiplier + 0.1;
                    scale = 0.6F + RANDOM.nextFloat() * 0.8F;
                    gravity = 1.0F + RANDOM.nextFloat() * 0.4F;
                    friction = 0.98F;
                    maxAge = 15 + RANDOM.nextInt(10);
                } else if (roll < 0.80) { // Dust Clouds
                    outSpeed = 0.02 + RANDOM.nextDouble() * 0.05;
                    motionY = (0.1 + RANDOM.nextDouble() * 0.2) * heightGradient * velocityMultiplier + 0.05;
                    scale = 0.3F + RANDOM.nextFloat() * 0.4F;
                    gravity = 0.15F + RANDOM.nextFloat() * 0.15F;
                    friction = 0.90F + RANDOM.nextFloat() * 0.04F;
                    maxAge = 30 + RANDOM.nextInt(20);
                } else { // Shockwave Ring (Small fast debris)
                    outSpeed = 0.15 + RANDOM.nextDouble() * 0.15;
                    motionY = 0.05 * heightGradient * velocityMultiplier + 0.02;
                    scale = 0.2F + RANDOM.nextFloat() * 0.2F;
                    gravity = 0.5F + RANDOM.nextFloat() * 0.5F;
                    friction = 0.85F + RANDOM.nextFloat() * 0.05F;
                    maxAge = 10 + RANDOM.nextInt(8);
                }

                if (type == ImpactType.SMASH) {
                    scale *= 1.2;
                    outSpeed *= 1.1;
                } else if (type == ImpactType.SKYBREAKER) {
                    motionY *= 1.5;
                }

                double x = center.x + Math.cos(angle) * currentRadius;
                double z = center.z + Math.sin(angle) * currentRadius;

            // Trace vertical coordinates to find the ground and corresponding blockstate
            double groundY = center.y;
            IBlockState state = Blocks.AIR.getDefaultState();
            boolean foundGround = false;
            boolean liquidHit = false;
            boolean isLava = false;

            checkPos.setPos(
                (int) Math.floor(x),
                (int) Math.floor(center.y + 1.0),
                (int) Math.floor(z)
            );

            for (int j = 0; j < 5; j++) {
                IBlockState current = world.getBlockState(checkPos);
                if (current.getBlock() != Blocks.AIR) {
                    if (current.getMaterial().isLiquid()) {
                        liquidHit = true;
                        isLava = (current.getMaterial() == Material.LAVA);
                        groundY = checkPos.getY() + 1.0; // spawn at surface of liquid
                        state = current;
                        foundGround = true;
                        break;
                    } else if (isValidGround(current, world, checkPos)) {
                        groundY = checkPos.getY() + current.getBlock().getBoundingBox(current, world, checkPos).maxY;
                        state = current;
                        foundGround = true;
                        break;
                    }
                }
                checkPos.setY(checkPos.getY() - 1);
            }

            double motionX = Math.cos(angle) * outSpeed;
            double motionZ = Math.sin(angle) * outSpeed;

            if (liquidHit) {
                spawnLiquidParticles(world, x, groundY, z, motionX, motionY, motionZ, isLava);
                continue;
            }

            if (foundGround) {
                ParticleHammerAOE particle = new ParticleHammerAOE(
                        world, x, groundY, z,
                        motionX, motionY, motionZ,
                        state, (float) scale, (float) gravity, (float) friction, maxAge
                );
                Minecraft.getMinecraft().effectRenderer.addEffect(particle);
            }
            }
        }
    }

    private static boolean isValidGround(IBlockState state, World world, BlockPos pos) {
        if (state.getBlock() == Blocks.AIR) return false;
        if (state.getMaterial().isLiquid()) return false;
        // Skip replaceable non-solid blocks (like flowers, tall grass, fire)
        if (state.getBlock().isReplaceable(world, pos) && !state.getMaterial().isSolid()) {
            return false;
        }
        return state.getMaterial().isSolid() || state.getMaterial() == Material.CARPET;
    }

    private static void spawnLiquidParticles(World world, double x, double y, double z, double motionX, double motionY, double motionZ, boolean isLava) {
        if (isLava) {
            world.spawnParticle(EnumParticleTypes.LAVA, x, y, z, motionX, motionY, motionZ);
            if (RANDOM.nextDouble() < 0.5) {
                world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, motionX * 0.5, motionY * 0.5 + 0.1, motionZ * 0.5);
            }
        } else {
            world.spawnParticle(EnumParticleTypes.WATER_SPLASH, x, y, z, motionX, motionY + 0.1, motionZ);
            if (RANDOM.nextDouble() < 0.3) {
                world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, x, y, z, motionX, motionY, motionZ);
            }
        }
    }
}
