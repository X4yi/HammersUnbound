package com.x4yi.hammersunbound.effects;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import java.util.List;

public class BloodPactPhysicsManager {

    public static void applyRepulsionField(EntityPlayer player, List<Integer> targetEntityIds, float fieldRadius, float repulsionForce) {
        if (player == null || player.world == null || player.world.isRemote) return;
        double radius = (double) fieldRadius;
        AxisAlignedBB aabb = player.getEntityBoundingBox().grow(radius);
        List<EntityLivingBase> nearby = player.world.getEntitiesWithinAABB(EntityLivingBase.class, aabb);
        
        for (EntityLivingBase mob : nearby) {
            if (mob == null || mob == player || mob.isDead) continue;
            if (targetEntityIds.contains(mob.getEntityId())) continue;
            
            double dx = mob.posX - player.posX;
            double dz = mob.posZ - player.posZ;
            double dist = Math.sqrt(dx * dx + dz * dz);
            
            if (dist > 0.01D && dist < radius) {
                // Apply a horizontal kinetic push based on how close they are
                double force = ((radius - dist) / radius) * repulsionForce;
                double pushX = (dx / dist) * force;
                double pushZ = (dz / dist) * force;
                
                mob.motionX += pushX;
                // Add a very slight vertical lift if they are on the ground so friction doesn't eat the push immediately
                if (mob.onGround) {
                    mob.motionY += 0.05D;
                }
                mob.motionZ += pushZ;
                mob.velocityChanged = true;
            }
        }
    }

    public static void applyAttraction(EntityPlayer player, List<EntityLivingBase> targetEntities, float attractionForce) {
        if (player == null || player.world == null || player.world.isRemote) return;
        
        for (EntityLivingBase target : targetEntities) {
            if (target == null || target.isDead) continue;
            
            double dx = player.posX - target.posX;
            double dy = player.posY - target.posY;
            double dz = player.posZ - target.posZ;
            double dist = Math.sqrt(dx * dx + dz * dz);
            
            if (dist > 2.0D) {
                double force = attractionForce;
                double pullX = (dx / dist) * force;
                double pullZ = (dz / dist) * force;
                
                target.motionX += pullX;
                if (target.onGround) {
                    target.motionY += 0.01D;
                }
                target.motionZ += pullZ;
                target.velocityChanged = true;
                
                if (target instanceof net.minecraft.entity.EntityLiving) {
                    ((net.minecraft.entity.EntityLiving) target).getNavigator().tryMoveToEntityLiving(player, 1.25D);
                }
            }
        }
    }
}
