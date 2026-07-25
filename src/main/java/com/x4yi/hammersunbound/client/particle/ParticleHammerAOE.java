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
    private final float initialScale;
    private final float rotationSpeed;
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
        this.canCollide = true;
        this.particleScale = (0.5F + this.rand.nextFloat() * 0.5F) * scale;
        this.initialScale = this.particleScale;
        this.gravity = gravity;
        this.drag = friction;
        this.rotationSpeed = (this.rand.nextFloat() - 0.5F) * 0.6F;
        this.particleAngle = this.rand.nextFloat() * ((float)Math.PI * 2F);
        this.particleTextureJitterX = this.rand.nextFloat() * 3.0F;
        this.particleTextureJitterY = this.rand.nextFloat() * 3.0F;
        TextureAtlasSprite sprite = null;
        try {
            sprite = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(state);
        } catch (Exception e) {
        }
        if (sprite == null) {
            try {
                sprite = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getTexture(net.minecraft.init.Blocks.STONE.getDefaultState());
            } catch (Exception e) {
            }
        }
        if (sprite != null) {
            this.setParticleTexture(sprite);
            this.uMin = sprite.getInterpolatedU((double)(this.particleTextureJitterX / 4.0F * 16.0F));
            this.uMax = sprite.getInterpolatedU((double)((this.particleTextureJitterX + 1.0F) / 4.0F * 16.0F));
            this.vMin = sprite.getInterpolatedV((double)(this.particleTextureJitterY / 4.0F * 16.0F));
            this.vMax = sprite.getInterpolatedV((double)((this.particleTextureJitterY + 1.0F) / 4.0F * 16.0F));
        }
        int colorMultiplier = -1;
        try {
            colorMultiplier = Minecraft.getMinecraft().getBlockColors().colorMultiplier(state, world, new BlockPos(x, y, z), 0);
        } catch (Exception e) {
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
        this.prevParticleAngle = this.particleAngle;
        if (!hasCollided) {
            motionY -= 0.045 * gravity;
            move(motionX, motionY, motionZ);
            motionX *= drag;
            motionZ *= drag;
            motionY *= drag;
            this.particleAngle += this.rotationSpeed;
            this.particleAlpha = 0.8F;
            if (onGround) {
                hasCollided = true;
                fadeTicks = 0;
            }
            if (particleAge++ > 150) {
                setExpired();
            }
        } else {
            motionX *= 0.5;
            motionZ *= 0.5;
            motionY = 0;
            move(motionX, motionY, motionZ);
            fadeTicks++;
            float fadeRatio = (float) fadeTicks / maxFadeTicks;
            this.particleScale = this.initialScale * (1.0F - fadeRatio);
            this.particleAlpha = 0.8F;
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
        float f8 = this.prevParticleAngle + (this.particleAngle - this.prevParticleAngle) * partialTicks;
        float cos = (float)Math.cos(f8);
        float sin = (float)Math.sin(f8);
        float[] cx = new float[] {-1.0F, -1.0F, 1.0F, 1.0F};
        float[] cy = new float[] {-1.0F, 1.0F, 1.0F, -1.0F};
        float[] vx = new float[4];
        float[] vy = new float[4];
        float[] vz = new float[4];
        for(int m = 0; m < 4; m++) {
            float rx = cx[m] * cos - cy[m] * sin;
            float ry = cx[m] * sin + cy[m] * cos;
            vx[m] = rotationX * rx * f4 + rotationXY * ry * f4;
            vy[m] = rotationZ * ry * f4;
            vz[m] = rotationYZ * rx * f4 + rotationXZ * ry * f4;
        }
        buffer.pos((double)f5 + vx[0], (double)f6 + vy[0], (double)f7 + vz[0]).tex((double)f, (double)f3).color(r, g, b, alpha).lightmap(j, k).endVertex();
        buffer.pos((double)f5 + vx[1], (double)f6 + vy[1], (double)f7 + vz[1]).tex((double)f, (double)f2).color(r, g, b, alpha).lightmap(j, k).endVertex();
        buffer.pos((double)f5 + vx[2], (double)f6 + vy[2], (double)f7 + vz[2]).tex((double)f1, (double)f2).color(r, g, b, alpha).lightmap(j, k).endVertex();
        buffer.pos((double)f5 + vx[3], (double)f6 + vy[3], (double)f7 + vz[3]).tex((double)f1, (double)f3).color(r, g, b, alpha).lightmap(j, k).endVertex();
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
        return 1;
    }
}