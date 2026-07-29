package com.x4yi.hammersunbound.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.LinkedList;

public class ParticleBloodPact extends Particle {

    public static double interpPosX;
    public static double interpPosY;
    public static double interpPosZ;

    private final Entity targetPlayer;
    private Entity targetEnemy;
    private final float baseRadius;
    private float currentAngle;
    private float currentHeight;
    private final float orbitSpeed;
    private final float heightSpeed;

    private int madness;
    private int burstTimer;
    private boolean pingPongActive;

    public void setBurstTimer(int timer) { this.burstTimer = timer; }
    public void setTargetEnemy(Entity enemy) { this.targetEnemy = enemy; }
    public Entity getTargetPlayer() { return this.targetPlayer; }

    public final LinkedList<Vec3d> trailPositions = new LinkedList<>();
    public static final int MAX_TRAIL_LENGTH = 15;

    public ParticleBloodPact(World worldIn, Entity targetPlayer, Entity targetEnemy, float baseRadius, int madness, int burstTimer, boolean pingPongActive) {
        super(worldIn, targetPlayer.posX, targetPlayer.posY + targetPlayer.height / 2.0D, targetPlayer.posZ);
        this.targetPlayer = targetPlayer;
        this.targetEnemy = targetEnemy;
        this.baseRadius = baseRadius;
        this.madness = madness;
        this.burstTimer = burstTimer;
        this.pingPongActive = pingPongActive;

        this.particleMaxAge = 60 + rand.nextInt(20);
        this.particleScale = 0.5f + rand.nextFloat() * 0.5f;

        this.currentAngle = rand.nextFloat() * (float) Math.PI * 2.0f;
        this.currentHeight = (rand.nextFloat() - 0.5f) * 4.0f;
        
        float madnessMult = 1.0f + (madness / 100.0f) * 2.0f;
        this.orbitSpeed = (0.1f + rand.nextFloat() * 0.15f) * madnessMult;
        this.heightSpeed = (0.05f + rand.nextFloat() * 0.1f) * (rand.nextBoolean() ? 1 : -1) * madnessMult;

        this.particleRed = 1.0f;
        this.particleGreen = 0.0f;
        this.particleBlue = 0.0f;
        this.particleAlpha = 0.9f;
    }

    public float getRed() { return this.particleRed; }
    public float getGreen() { return this.particleGreen; }
    public float getBlue() { return this.particleBlue; }
    public float getAlpha() { return this.particleAlpha; }
    public float getScale() { return this.particleScale; }

    @Override
    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        trailPositions.addFirst(new Vec3d(this.posX, this.posY, this.posZ));
        if (trailPositions.size() > MAX_TRAIL_LENGTH) {
            trailPositions.removeLast();
        }

        if (this.particleAge++ >= this.particleMaxAge || this.targetPlayer.isDead) {
            this.setExpired();
            return;
        }

        if (pingPongActive) {
            this.particleRed = 1.0f;
            this.particleGreen = 0.5f;
            this.particleBlue = 0.0f;
        } else {
            this.particleRed = 1.0f;
            this.particleGreen = 0.0f;
            this.particleBlue = 0.0f;
        }

        double targetX, targetY, targetZ;

        if (burstTimer > 0) {
            if (targetEnemy != null && !targetEnemy.isDead) {
                // Burst homing missile logic to enemy
                targetX = targetEnemy.posX;
                targetY = targetEnemy.posY + targetEnemy.height / 2.0;
                targetZ = targetEnemy.posZ;
            } else {
                // Burst homing missile logic back to player (premature death of targets)
                targetX = targetPlayer.posX;
                targetY = targetPlayer.posY + targetPlayer.height / 2.0;
                targetZ = targetPlayer.posZ;
            }
            this.motionX = (targetX - this.posX) * 0.3;
            this.motionY = (targetY - this.posY) * 0.3;
            this.motionZ = (targetZ - this.posZ) * 0.3;
            
            this.particleScale = 1.5f;
        } else {
            // Whirlwind logic
            this.currentAngle += this.orbitSpeed;
            this.currentHeight += this.heightSpeed;

            if (this.currentHeight > 3.0f) this.currentHeight = -2.0f;
            if (this.currentHeight < -2.0f) this.currentHeight = 3.0f;

            // Cone shape for whirlwind
            float radius = baseRadius * (1.5f - Math.abs(currentHeight) / 5.0f);
            
            targetX = targetPlayer.posX + Math.cos(currentAngle) * radius;
            targetZ = targetPlayer.posZ + Math.sin(currentAngle) * radius;
            targetY = targetPlayer.posY + (targetPlayer.height / 2.0) + currentHeight;

            this.motionX = (targetX - this.posX) * 0.4;
            this.motionY = (targetY - this.posY) * 0.4;
            this.motionZ = (targetZ - this.posZ) * 0.4;
        }

        this.move(this.motionX, this.motionY, this.motionZ);
    }

    @Override
    public int getFXLayer() {
        return 3;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ) {
        // We do not render the quad here anymore, because we will render the ribbon trail directly in HammerClientHandler.
    }
}
