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

        int adjustedCount = (int) (particleCount * ClientConfig.aoeParticleDensity);
        if (adjustedCount <= 0) return;

        // Use MutableBlockPos to avoid object allocation in loop
        BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();

        for (int i = 0; i < adjustedCount; i++) {
            // Determine role of particle (Debris 30%, Dust 50%, Shockwave 20%)
            double roll = RANDOM.nextDouble();
            int role;
            if (roll < 0.30) {
                role = 0; // Debris
            } else if (roll < 0.80) {
                role = 1; // Dust
            } else {
                role = 2; // Shockwave
            }

            // Radial horizontal movement directed outwards with angular/distance noise
            double angle = ((double) i / adjustedCount) * Math.PI * 2 + (RANDOM.nextDouble() - 0.5) * 0.4;
            double r = radius;

            double scale = 1.0;
            double gravity = 1.0;
            double friction = 0.98;
            int maxAge = 25;
            double outSpeed = 0.0;
            double motionY = 0.0;

            // Apply different physics and distribution based on role and impact type
            if (role == 0) { // Debris Chunks
                r = radius * (0.1 + RANDOM.nextDouble() * 0.6);
                outSpeed = 0.05 + RANDOM.nextDouble() * 0.12;
                motionY = 0.25 + RANDOM.nextDouble() * 0.35;
                scale = 0.6F + RANDOM.nextFloat() * 0.8F;
                gravity = 1.0F + RANDOM.nextFloat() * 0.4F;
                friction = 0.98F;
                maxAge = 15 + RANDOM.nextInt(10);
            } else if (role == 1) { // Dust Clouds
                r = radius * (0.2 + RANDOM.nextDouble() * 0.8);
                outSpeed = 0.02 + RANDOM.nextDouble() * 0.06;
                motionY = 0.08 + RANDOM.nextDouble() * 0.12;
                scale = 0.3F + RANDOM.nextFloat() * 0.4F;
                gravity = 0.15F + RANDOM.nextFloat() * 0.15F;
                friction = 0.90F + RANDOM.nextFloat() * 0.04F;
                maxAge = 30 + RANDOM.nextInt(20);
            } else { // Shockwave Ring
                r = radius * (0.8 + RANDOM.nextDouble() * 0.2);
                outSpeed = 0.18 + RANDOM.nextDouble() * 0.12;
                motionY = 0.01 + RANDOM.nextDouble() * 0.04;
                scale = 0.2F + RANDOM.nextFloat() * 0.2F;
                gravity = 0.02F + RANDOM.nextFloat() * 0.04F;
                friction = 0.82F + RANDOM.nextFloat() * 0.05F;
                maxAge = 8 + RANDOM.nextInt(6);
            }

            // Adjust parameters based on future impact types if needed
            if (type == ImpactType.SMASH) {
                scale *= 1.2;
                outSpeed *= 1.1;
            } else if (type == ImpactType.SKYBREAKER) {
                motionY *= 1.5;
            }

            double x = center.x + Math.cos(angle) * r;
            double z = center.z + Math.sin(angle) * r;

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
