package com.x4yi.hammersunbound.client.particle;

import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class HammerParticleFactory implements IParticleFactory {

    @Nullable
    @Override
    public Particle createParticle(int particleID, World world, double x, double y, double z,
                                   double xSpeed, double ySpeed, double zSpeed, int... parameters) {
        if (parameters.length >= 4) {
            double centerX = parameters[0];
            double centerZ = parameters[1];
            double radius = parameters[2];
            double targetHeight = parameters[3];
            float maxLifetime = parameters.length > 4 ? parameters[4] : 40;

            return new ParticleHammerAOE(world, x, y, z, centerX, centerZ, radius, targetHeight, maxLifetime);
        }
        return null;
    }
}
