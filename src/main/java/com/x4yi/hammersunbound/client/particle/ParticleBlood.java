package com.x4yi.hammersunbound.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleBlood extends Particle {
    private static final ResourceLocation TEXTURE = new ResourceLocation("hammersunbound", "textures/particle/blood.png");

    public ParticleBlood(World world, double x, double y, double z, double motionX, double motionY, double motionZ) {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        
        // Deep crimson red colors
        this.particleRed = 0.6F + this.rand.nextFloat() * 0.4F;
        this.particleGreen = 0.0F + this.rand.nextFloat() * 0.1F;
        this.particleBlue = 0.0F + this.rand.nextFloat() * 0.1F;
        
        this.particleAlpha = 1.0F;
        this.particleScale = 0.25F + this.rand.nextFloat() * 0.25F;
        this.particleMaxAge = 15 + this.rand.nextInt(15);
    }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }

        this.move(this.motionX, this.motionY, this.motionZ);

        this.motionX *= 0.98;
        this.motionY *= 0.98;
        this.motionZ *= 0.98;

        this.motionY -= 0.03D;

        if (this.onGround) {
            this.motionX *= 0.6;
            this.motionZ *= 0.6;
        }
    }

    @Override
    public int getFXLayer() {
        return 3; // Custom layer for custom texture binding
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);

        float ageRatio = (float) this.particleAge / (float) this.particleMaxAge;
        float scale = this.particleScale * (1.0F - ageRatio * ageRatio * 0.5F);

        float f5 = (float) (this.prevPosX + (this.posX - this.prevPosX) * (double) partialTicks - interpPosX);
        float f6 = (float) (this.prevPosY + (this.posY - this.prevPosY) * (double) partialTicks - interpPosY);
        float f7 = (float) (this.prevPosZ + (this.posZ - this.prevPosZ) * (double) partialTicks - interpPosZ);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha);

        buffer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);

        double x1 = f5 - rotationX * scale - rotationXY * scale;
        double y1 = f6 - rotationZ * scale;
        double z1 = f7 - rotationYZ * scale - rotationXZ * scale;

        double x2 = f5 - rotationX * scale + rotationXY * scale;
        double y2 = f6 + rotationZ * scale;
        double z2 = f7 - rotationYZ * scale + rotationXZ * scale;

        double x3 = f5 + rotationX * scale + rotationXY * scale;
        double y3 = f6 + rotationZ * scale;
        double z3 = f7 + rotationYZ * scale + rotationXZ * scale;

        double x4 = f5 + rotationX * scale - rotationXY * scale;
        double y4 = f6 - rotationZ * scale;
        double z4 = f7 + rotationYZ * scale - rotationXZ * scale;

        buffer.pos(x1, y1, z1).tex(0.0D, 1.0D).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(x2, y2, z2).tex(0.0D, 0.0D).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(x3, y3, z3).tex(1.0D, 0.0D).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).normal(0.0F, 1.0F, 0.0F).endVertex();
        buffer.pos(x4, y4, z4).tex(1.0D, 1.0D).color(this.particleRed, this.particleGreen, this.particleBlue, this.particleAlpha).normal(0.0F, 1.0F, 0.0F).endVertex();

        Tessellator.getInstance().draw();
    }
}
