package com.x4yi.hammersunbound.effects;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.Vec3d;
public class PingPongManager {
    public static void tickPingPong(BloodPactEffect effect, EntityPlayer player) {
        if (effect.getPingPongPhase() <= 0) return;
        net.minecraft.entity.Entity e = player.world.getEntityByID(effect.getPingPongTargetId());
        if (!(e instanceof EntityLivingBase) || e.isDead || !effect.getTargetEntities().contains((EntityLivingBase) e)) {
            effect.cancelPingPong();
            return;
        }
        EntityLivingBase mob = (EntityLivingBase) e;
        int timer = effect.getPingPongTimer();
        int phase = effect.getPingPongPhase();
        Vec3d dir = effect.getPingPongDirection();
        if (phase == 1) {
            mob.motionX = dir.x * 1.5D;
            mob.motionY = dir.y * 1.5D;
            mob.motionZ = dir.z * 1.5D;
            mob.velocityChanged = true;
            Vec3d startPos = new Vec3d(mob.posX, mob.posY + mob.height / 2.0, mob.posZ);
            Vec3d endPos = startPos.addVector(mob.motionX, mob.motionY, mob.motionZ);
            net.minecraft.util.math.RayTraceResult result = mob.world.rayTraceBlocks(startPos, endPos, false, true, false);
            if (result != null && result.typeOfHit == net.minecraft.util.math.RayTraceResult.Type.BLOCK) {
                float impactDamage = (float) player.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() * 0.8f;
                mob.attackEntityFrom(DamageSource.causePlayerDamage(player), impactDamage);
                mob.world.playSound(null, mob.posX, mob.posY, mob.posZ,
                        net.minecraft.init.SoundEvents.BLOCK_ANVIL_PLACE,
                        net.minecraft.util.SoundCategory.PLAYERS, 0.8F, 1.2F);
                effect.setPingPongPhase(2);
                effect.setPingPongTimer(20);
                effect.syncToTrackingAndSelf();
                return;
            }
            timer--;
            effect.setPingPongTimer(timer);
            if (timer <= 0) {
                effect.setPingPongPhase(2);
                effect.setPingPongTimer(20);
                effect.syncToTrackingAndSelf();
            }
        } else if (phase == 2) {
            Vec3d pull = new Vec3d(player.posX - mob.posX, player.posY + player.getEyeHeight() / 2.0 - (mob.posY + mob.getEyeHeight() / 2.0), player.posZ - mob.posZ);
            double dist = pull.lengthVector();
            if (dist < 1.5D) {
                float finalDamage = (float) player.getEntityAttribute(net.minecraft.entity.SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue() * 1.2f;
                mob.attackEntityFrom(DamageSource.causePlayerDamage(player), finalDamage);
                effect.cancelPingPong();
                return;
            }
            pull = pull.normalize();
            mob.motionX = pull.x * 1.8D;
            mob.motionY = pull.y * 1.8D;
            mob.motionZ = pull.z * 1.8D;
            mob.velocityChanged = true;
            timer--;
            effect.setPingPongTimer(timer);
            if (timer <= 0) {
                effect.cancelPingPong();
            }
        }
    }
}