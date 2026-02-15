package com.x4yi.hammers.handlers.RandomHandlers;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class X4BlockCrack {

    @SideOnly(Side.CLIENT)
    public static class CustomBlockCrack extends Particle {

        private final float gravity;

        public CustomBlockCrack(World world,
                                double x, double y, double z,
                                double mx, double my, double mz,
                                IBlockState state,
                                float scale,
                                float gravity) {

            super(world, x, y, z, mx, my, mz);

            this.gravity = gravity;

            this.setParticleTexture(
                    Minecraft.getMinecraft()
                            .getBlockRendererDispatcher()
                            .getBlockModelShapes()
                            .getTexture(state)
            );

            this.particleTextureJitterX = this.rand.nextInt(4);
            this.particleTextureJitterY = this.rand.nextInt(4);

            this.motionX = mx;
            this.motionY = my;
            this.motionZ = mz;

            float baseSize = 0.4F + world.rand.nextFloat() * 0.4F;
            this.particleScale = Math.min(
                    baseSize * (0.7F + 0.3F * MathHelper.sqrt(scale)),
                    4F
            );

            this.particleMaxAge = 40 + world.rand.nextInt(20);
            this.canCollide = false;
        }

        @Override
        public void renderParticle(BufferBuilder buffer,
                                   Entity entityIn,
                                   float partialTicks,
                                   float rotationX,
                                   float rotationZ,
                                   float rotationYZ,
                                   float rotationXY,
                                   float rotationXZ) {

            float u0 = this.particleTexture.getInterpolatedU(
                    (this.particleTextureJitterX / 4F) * 16F);
            float u1 = this.particleTexture.getInterpolatedU(
                    ((this.particleTextureJitterX + 1) / 4F) * 16F);
            float v0 = this.particleTexture.getInterpolatedV(
                    (this.particleTextureJitterY / 4F) * 16F);
            float v1 = this.particleTexture.getInterpolatedV(
                    ((this.particleTextureJitterY + 1) / 4F) * 16F);

            float scale = this.particleScale * 0.1F;

            float x = (float) (this.prevPosX +
                    (this.posX - this.prevPosX) * partialTicks - interpPosX);
            float y = (float) (this.prevPosY +
                    (this.posY - this.prevPosY) * partialTicks - interpPosY);
            float z = (float) (this.prevPosZ +
                    (this.posZ - this.prevPosZ) * partialTicks - interpPosZ);

            int brightness = this.getBrightnessForRender(partialTicks);
            int sky = brightness >> 16 & 65535;
            int block = brightness & 65535;

            buffer.pos(x - rotationX * scale - rotationXY * scale,
                            y - rotationZ * scale,
                            z - rotationYZ * scale - rotationXZ * scale)
                    .tex(u1, v1)
                    .color(this.particleRed, this.particleGreen,
                            this.particleBlue, this.particleAlpha)
                    .lightmap(sky, block)
                    .endVertex();

            buffer.pos(x - rotationX * scale + rotationXY * scale,
                            y + rotationZ * scale,
                            z - rotationYZ * scale + rotationXZ * scale)
                    .tex(u1, v0)
                    .color(this.particleRed, this.particleGreen,
                            this.particleBlue, this.particleAlpha)
                    .lightmap(sky, block)
                    .endVertex();

            buffer.pos(x + rotationX * scale + rotationXY * scale,
                            y + rotationZ * scale,
                            z + rotationYZ * scale + rotationXZ * scale)
                    .tex(u0, v0)
                    .color(this.particleRed, this.particleGreen,
                            this.particleBlue, this.particleAlpha)
                    .lightmap(sky, block)
                    .endVertex();

            buffer.pos(x + rotationX * scale - rotationXY * scale,
                            y - rotationZ * scale,
                            z + rotationYZ * scale - rotationXZ * scale)
                    .tex(u0, v1)
                    .color(this.particleRed, this.particleGreen,
                            this.particleBlue, this.particleAlpha)
                    .lightmap(sky, block)
                    .endVertex();
        }

        @Override
        public int getFXLayer() {
            return 1;
        }

        @Override
        public void onUpdate() {

            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;

            if (this.particleAge++ >= this.particleMaxAge) {
                this.setExpired();
                return;
            }

            this.motionY -= 0.04D * this.gravity;

            this.move(this.motionX, this.motionY, this.motionZ);

            this.motionX *= 0.9D;
            this.motionZ *= 0.9D;

            if (this.onGround) {
                this.motionX *= 0.5D;
                this.motionZ *= 0.5D;
            }
        }
    }
}
