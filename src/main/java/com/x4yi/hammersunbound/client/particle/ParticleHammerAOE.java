package com.x4yi.hammersunbound.client.particle;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ParticleHammerAOE extends Particle {

    private final float particleTextureJitterX;
    private final float particleTextureJitterY;
    private float uMin;
    private float uMax;
    private float vMin;
    private float vMax;
    
    private final float gravity;
    private final float drag;

    private boolean hasCollided = false;
    private int fadeTicks = 0;
    private final int maxFadeTicks = 12;

    public ParticleHammerAOE(World world, double x, double y, double z,
                             double motionX, double motionY, double motionZ,
                             IBlockState state) {
        this(world, x, y, z, motionX, motionY, motionZ, state, 1.0F, 1.0F, 0.98F, 25 + world.rand.nextInt(15));
    }

    public ParticleHammerAOE(World world, double x, double y, double z,
                             double motionX, double motionY, double motionZ,
                             IBlockState state, float scale, float gravity, float friction, int maxAge) {
        super(world, x, y, z, motionX, motionY, motionZ);
        
        this.motionX = motionX;
        this.motionY = motionY;
        this.motionZ = motionZ;
        
        this.particleMaxAge = maxAge;
        this.canCollide = true; // Enabled physical collision with the ground
        
        this.particleScale = (0.5F + this.rand.nextFloat() * 0.5F) * scale;
        this.gravity = gravity;
        this.drag = friction;
        
        this.particleTextureJitterX = this.rand.nextFloat() * 3.0F;
        this.particleTextureJitterY = this.rand.nextFloat() * 3.0F;
        
        // Resolve texture sprite
        TextureAtlasSprite sprite = null;
        try {
            sprite = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
        } catch (Exception e) {
            // Fallback
        }
        if (sprite == null) {
            try {
                sprite = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(net.minecraft.init.Blocks.STONE.getDefaultState());
            } catch (Exception e) {
                // Ignore
            }
        }
        
        if (sprite != null) {
            this.setParticleTexture(sprite);
            // Precompute UVs once to optimize rendering
            this.uMin = sprite.getInterpolatedU((double)(this.particleTextureJitterX / 4.0F * 16.0F));
            this.uMax = sprite.getInterpolatedU((double)((this.particleTextureJitterX + 1.0F) / 4.0F * 16.0F));
            this.vMin = sprite.getInterpolatedV((double)(this.particleTextureJitterY / 4.0F * 16.0F));
            this.vMax = sprite.getInterpolatedV((double)((this.particleTextureJitterY + 1.0F) / 4.0F * 16.0F));
        }

        // Apply color multiplier
        int colorMultiplier = -1;
        try {
            colorMultiplier = Minecraft.getMinecraft().getBlockColors().colorMultiplier(state, world, new BlockPos(x, y, z), 0);
        } catch (Exception e) {
            // Ignore
        }
        if (colorMultiplier != -1) {
            this.particleRed = (float)(colorMultiplier >> 16 & 255) / 255.0F;
            this.particleGreen = (float)(colorMultiplier >> 8 & 255) / 255.0F;
            this.particleBlue = (float)(colorMultiplier & 255) / 255.0F;
        } else {
            this.particleRed = 1.0F;
            this.particleGreen = 1.0F;
            this.particleBlue = 1.0F;
        }

        // Apply vanilla shading factor (0.6F)
        this.particleRed *= 0.6F;
        this.particleGreen *= 0.6F;
        this.particleBlue *= 0.6F;
        
        this.particleAlpha = 0.8F;
    }

    @Override
    public void onUpdate() {
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        if (!hasCollided) {
            motionY -= 0.03 * gravity; // gravity
            move(motionX, motionY, motionZ);

            motionX *= drag;
            motionZ *= drag;
            motionY *= drag; // Apply drag to vertical motion for natural air resistance

            // Keep full opacity in the air
            this.particleAlpha = 0.8F;

            if (onGround) {
                hasCollided = true;
                fadeTicks = 0;
            }

            // Safety lifespan check for particles in the air (e.g. falling off cliffs)
            if (particleAge++ > 150) {
                setExpired();
            }
        } else {
            // Landed: decelerate movement and perform a smooth fade-out
            motionX *= 0.5;
            motionZ *= 0.5;
            motionY = 0;
            move(motionX, motionY, motionZ);

            fadeTicks++;
            float fadeRatio = (float) fadeTicks / maxFadeTicks;
            this.particleAlpha = 0.8F * (1.0F - fadeRatio * fadeRatio); // smooth quadratic fade-out

            if (fadeTicks >= maxFadeTicks) {
                setExpired();
            }
        }
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        float f = this.uMin;
        float f1 = this.uMax;
        float f2 = this.vMin;
        float f3 = this.vMax;

        float f4 = 0.1F * this.particleScale;

        float f5 = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partialTicks - interpPosX);
        float f6 = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partialTicks - interpPosY);
        float f7 = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partialTicks - interpPosZ);

        int i = this.getBrightnessForRender(partialTicks);
        int j = i >> 16 & 65535;
        int k = i & 65535;

        float r = this.particleRed;
        float g = this.particleGreen;
        float b = this.particleBlue;
        float alpha = this.particleAlpha;

        // Corrected Z coordinate for the fourth vertex (sign of rotationYZ * f4) to avoid skewing/flicker
        buffer.pos((double)(f5 - rotationX * f4 - rotationXY * f4), (double)(f6 - rotationZ * f4), (double)(f7 - rotationYZ * f4 - rotationXZ * f4)).tex((double)f, (double)f3).color(r, g, b, alpha).lightmap(j, k).endVertex();
        buffer.pos((double)(f5 - rotationX * f4 + rotationXY * f4), (double)(f6 + rotationZ * f4), (double)(f7 - rotationYZ * f4 + rotationXZ * f4)).tex((double)f, (double)f2).color(r, g, b, alpha).lightmap(j, k).endVertex();
        buffer.pos((double)(f5 + rotationX * f4 + rotationXY * f4), (double)(f6 + rotationZ * f4), (double)(f7 + rotationYZ * f4 + rotationXZ * f4)).tex((double)f1, (double)f2).color(r, g, b, alpha).lightmap(j, k).endVertex();
        buffer.pos((double)(f5 + rotationX * f4 - rotationXY * f4), (double)(f6 - rotationZ * f4), (double)(f7 + rotationYZ * f4 - rotationXZ * f4)).tex((double)f1, (double)f3).color(r, g, b, alpha).lightmap(j, k).endVertex();
    }

    @Override
    public int getBrightnessForRender(float partialTicks) {
        int i = super.getBrightnessForRender(partialTicks);
        int j = 0;
        BlockPos pos = new BlockPos(this.posX, this.posY, this.posZ);
        if (this.world.isBlockLoaded(pos)) {
            j = this.world.getCombinedLight(pos, 0);
        }
        return i == 0 ? j : i;
    }

    @Override
    public int getFXLayer() {
        return 1; // LOCATION_BLOCKS_TEXTURE atlas
    }
}
