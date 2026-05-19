package com.x4yi.hammersunbound.client.particle;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleHammerAOE extends Particle {

    private final double targetHeight;
    private final double centerX;
    private final double centerZ;
    private final double radius;
    private final float maxLifetime;
    private float ageRatio;

    public ParticleHammerAOE(World world, double x, double y, double z,
                             double centerX, double centerZ, double radius,
                             double targetHeight, float maxLifetime) {
        super(world, x, y, z, 0, 0, 0);
        this.targetHeight = targetHeight;
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.radius = radius;
        this.maxLifetime = maxLifetime;
        this.particleMaxAge = (int) (maxLifetime * 20);
        this.ageRatio = 0.0f;

        double dx = x - centerX;
        double dz = z - centerZ;
        double dist = Math.sqrt(dx * dx + dz * dz);

        this.motionX = dx / (dist + 0.01) * 0.1;
        this.motionY = targetHeight / (this.particleMaxAge + 0.01) * 1.2;
        this.motionZ = dz / (dist + 0.01) * 0.1;

        this.particleRed = 1.0f;
        this.particleGreen = 0.8f;
        this.particleBlue = 0.2f;
        this.particleAlpha = 0.8f;
        this.particleScale = 0.5f + (float) (dist / radius) * 0.5f;

        setBlockTexture();
    }

    private void setBlockTexture() {
        BlockPos pos = new BlockPos(posX, posY - 0.5, posZ);
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock() == net.minecraft.init.Blocks.AIR) {
            for (int i = 1; i <= 5; i++) {
                IBlockState downState = world.getBlockState(pos.down(i));
                if (downState.getBlock() != net.minecraft.init.Blocks.AIR) {
                    state = downState;
                    break;
                }
            }
            if (state.getBlock() == net.minecraft.init.Blocks.AIR) {
                state = net.minecraft.init.Blocks.STONE.getDefaultState();
            }
        }
        try {
            setParticleTexture(Minecraft.getMinecraft().getBlockRendererDispatcher()
                    .getBlockModelShapes().getTexture(state));
        } catch (Exception e) {
            try {
                setParticleTexture(Minecraft.getMinecraft().getBlockRendererDispatcher()
                        .getBlockModelShapes().getTexture(net.minecraft.init.Blocks.STONE.getDefaultState()));
            } catch (Exception ex) {
            }
        }
    }

    @Override
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        ageRatio = (float) particleAge / particleMaxAge;

        motionY *= 0.98;
        move(motionX, motionY, motionZ);

        motionX *= 0.95;
        motionZ *= 0.95;

        if (ageRatio < 0.2f) {
            particleAlpha = ageRatio / 0.2f * 0.8f;
        } else if (ageRatio > 0.7f) {
            particleAlpha = (1.0f - ageRatio) / 0.3f * 0.8f;
        } else {
            particleAlpha = 0.8f;
        }

        particleAge++;
        if (particleAge > particleMaxAge) {
            setExpired();
        }
    }

    @Override
    public int getFXLayer() {
        return 1;
    }

    public float getAgeRatio() {
        return ageRatio;
    }

    public double getTargetHeight() {
        return targetHeight;
    }
}
