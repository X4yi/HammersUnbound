package com.x4yi.hammers.handlers.WarHammerCore;

import com.x4yi.hammers.config.HammersUnboundClient;
import com.x4yi.hammers.config.HammersUnboundServer;
import com.x4yi.hammers.handlers.RandomHandlers.X4BlockCrack;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class WarHammerParticles {

    public enum ImpactType {
        CRIT,
        SMASH,
        BETTER_SMASH
    }
    public static void send(EntityLivingBase center,
                            float radius,
                            float scale,
                            ImpactType type){

        if(!(center.world instanceof WorldServer))return;

        WorldServer w=(WorldServer)center.world;

        double max=HammersUnboundServer.server.aoeParticleSendDistance;
        double maxSq=max*max;

        float baseDensity;

        switch(type){
            case CRIT:
                baseDensity=0.35f;
                break;
            case SMASH:
                baseDensity=1.0f;
                break;
            case BETTER_SMASH:
                baseDensity=1.7f;
                break;
            default:
                baseDensity=1f;
        }

        float density= (float) (baseDensity*
                        (1+(scale-1)*HammersUnboundServer.server.fallParticleMultiplier));

        PacketAOE pkt=new PacketAOE(center.getEntityId(),radius,density,scale);

        for(EntityPlayerMP p:w.getPlayers(EntityPlayerMP.class,
                x->x.getDistanceSq(center)<=maxSq)){
            WarHammerNetwork.NET.sendTo(pkt,p);
        }
    }

    public static class PacketAOE implements IMessage{

        int id;
        float radius,density,scale;

        public PacketAOE(){}
        public PacketAOE(int id,float r,float d,float s){
            this.id=id;radius=r;density=d;scale=s;
        }

        public void fromBytes(ByteBuf b){
            id=b.readInt();
            radius=b.readFloat();
            density=b.readFloat();
            scale=b.readFloat();
        }

        public void toBytes(ByteBuf b){
            b.writeInt(id);
            b.writeFloat(radius);
            b.writeFloat(density);
            b.writeFloat(scale);
        }
    }

    public static class Handler implements IMessageHandler<PacketAOE,IMessage>{

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketAOE m,MessageContext ctx){

            Minecraft.getMinecraft().addScheduledTask(()->{

                Minecraft mc=Minecraft.getMinecraft();
                if(mc.world==null)return;
                if(!HammersUnboundClient.client.aoeParticlesEnabled)return;

                Entity e=mc.world.getEntityByID(m.id);
                if(!(e instanceof EntityLivingBase))return;

                spawn((EntityLivingBase)e,m.radius,m.density,m.scale);

            });

            return null;
        }
    }

    @SideOnly(Side.CLIENT)
    private static void spawn(EntityLivingBase e,float radius,float serverDensity,float scale){

        World w=e.world;
        if(w==null)return;

        int cap=HammersUnboundClient.client.aoeParticlesMaxCap;

        float clientDensity=HammersUnboundClient.client.aoeParticlesDensity;
        float finalDensity=serverDensity*clientDensity;

        int rings=Math.max(1,(int)(radius*3.5f));
        int basePerRing=(int)((12+radius*6f)*finalDensity);

        double cx=e.posX;
        double cz=e.posZ;
        double baseY=e.getEntityBoundingBox().minY;

        int total=0;

        for(int ring=1;ring<=rings;ring++){

            double r=(radius/rings)*ring;
            int points=(int)(basePerRing*(0.7+0.3*((double)ring/rings)));

            for(int i=0;i<points;i++){

                double angle=(2*Math.PI*i)/points;

                double x=cx+Math.cos(angle)*r;
                double z=cz+Math.sin(angle)*r;

                spawnGround(w,x,baseY,z,ring,rings,scale);

                total++;
                if(total>=cap)return;
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private static void spawnGround(World w,double x,double y,double z,int ring,int maxRing,float scale){

        BlockPos pos=new BlockPos(x,y,z);

        if(!w.isBlockLoaded(pos))return;

        while(pos.getY()>0&&w.isAirBlock(pos)){
            pos=pos.down();
        }

        if(!w.isBlockLoaded(pos))return;

        IBlockState st=w.getBlockState(pos);
        if(!st.getMaterial().isSolid())return;

        double centerFactor=1.0-((double)ring/maxRing);
        double strength=Math.sqrt(scale);

        double vx=(Math.random()-0.5)*0.06*strength;
        double vz=(Math.random()-0.5)*0.06*strength;
        double vy=(0.15+0.45*centerFactor)*strength;

        Minecraft.getMinecraft().effectRenderer.addEffect(
                new X4BlockCrack.CustomBlockCrack(
                        w,
                        x,
                        pos.getY()+1.01,
                        z,
                        vx,vy,vz,
                        st,
                        scale,
                        1.4f
                )
        );
    }
}
