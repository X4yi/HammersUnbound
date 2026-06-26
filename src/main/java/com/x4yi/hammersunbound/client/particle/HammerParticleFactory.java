package com.x4yi.hammersunbound.client.particle;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import javax.annotation.Nullable;
@SideOnly(Side.CLIENT)
public class HammerParticleFactory implements IParticleFactory {
    @Nullable
    @Override
    public Particle createParticle(int particleID, World world, double x, double y, double z,
                                   double xSpeed, double ySpeed, double zSpeed, int... parameters) {
        return new ParticleHammerAOE(world, x, y, z, xSpeed, ySpeed, zSpeed, Blocks.STONE.getDefaultState());
    }
}