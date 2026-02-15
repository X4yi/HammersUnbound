package com.x4yi.hammers.handlers.WarHammerCore;

import net.minecraft.entity.*;
import net.minecraft.nbt.*;
import net.minecraft.util.*;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber
public class WarHammerStun {

    public interface IStun {
        boolean stunned();
        void set(boolean s);
        int ticks();
        void setTicks(int t);
        float yaw();
        void setYaw(float y);
        float pitch();
        void setPitch(float p);
    }

    public static class Impl implements IStun {
        private boolean s;
        private int t;
        private float y,p;
        public boolean stunned(){return s;}
        public void set(boolean s){this.s=s;}
        public int ticks(){return t;}
        public void setTicks(int t){this.t=t;}
        public float yaw(){return y;}
        public void setYaw(float y){this.y=y;}
        public float pitch(){return p;}
        public void setPitch(float p){this.p=p;}
    }

    public static class Storage implements Capability.IStorage<IStun>{
        public NBTBase writeNBT(Capability<IStun> c,IStun i,EnumFacing s){
            NBTTagCompound n=new NBTTagCompound();
            n.setBoolean("s",i.stunned());
            n.setInteger("t",i.ticks());
            n.setFloat("y",i.yaw());
            n.setFloat("p",i.pitch());
            return n;
        }
        public void readNBT(Capability<IStun> c,IStun i,EnumFacing s,NBTBase n){
            NBTTagCompound t=(NBTTagCompound)n;
            i.set(t.getBoolean("s"));
            i.setTicks(t.getInteger("t"));
            i.setYaw(t.getFloat("y"));
            i.setPitch(t.getFloat("p"));
        }
    }

    @CapabilityInject(IStun.class)
    public static final Capability<IStun> CAP;

    static {
        CAP = null;
    }

    public static void register(){
        CapabilityManager.INSTANCE.register(IStun.class,new Storage(),Impl::new);
    }

    public static class Provider implements ICapabilitySerializable<NBTTagCompound>{
        private final IStun inst=new Impl();
        public boolean hasCapability(@Nonnull Capability<?> c, EnumFacing s){return false;}
        public <T> T getCapability(@Nonnull Capability<T> c, EnumFacing s){return CAP!=null&&c==CAP?CAP.cast(inst):null;}
        public NBTTagCompound serializeNBT(){return (NBTTagCompound)CAP.getStorage().writeNBT(CAP,inst,null);}
        public void deserializeNBT(NBTTagCompound n){CAP.getStorage().readNBT(CAP,inst,null,n);}
    }

    @SubscribeEvent
    public static void attach(AttachCapabilitiesEvent<Entity> e){
        if(e.getObject()instanceof EntityLivingBase)
            e.addCapability(new ResourceLocation("hammers","stun"),new Provider());
    }

    public static void apply(EntityLivingBase e,int duration){
        if(!e.isEntityAlive())return;
        IStun c=e.getCapability(CAP,null);if(c==null)return;
        c.set(true);
        c.setTicks(duration);
        c.setYaw(e.rotationYaw);
        c.setPitch(e.rotationPitch);
        WarHammerNetwork.sync(e,true,duration);
        e.motionX = 0;
        e.motionZ = 0;
    }

    @SubscribeEvent
    public static void tick(LivingEvent.LivingUpdateEvent e){
        EntityLivingBase en=e.getEntityLiving();
        if(en.world.isRemote)return;
        IStun c=en.getCapability(CAP,null);
        if(c==null||!c.stunned())return;

        if(c.ticks()<=0){
            c.set(false);
            WarHammerNetwork.sync(en,false,0);
            return;
        }

        c.setTicks(c.ticks()-1);

        en.motionX=0;
        en.motionY=0;
        en.motionZ=0;
        en.velocityChanged=true;
        en.rotationYaw=c.yaw();
        en.rotationPitch=c.pitch();
        en.fallDistance=0;
    }
    @SubscribeEvent
    public static void cancelOutgoingAttack(LivingAttackEvent e){
        if(!(e.getSource().getTrueSource() instanceof EntityLivingBase)) return;

        EntityLivingBase attacker = (EntityLivingBase)e.getSource().getTrueSource();
        IStun cap = attacker.getCapability(CAP,null);

        if(cap!=null && cap.stunned())
            e.setCanceled(true);
    }

    @SubscribeEvent
    public static void cancelUse(PlayerInteractEvent e){
        IStun c=e.getEntityPlayer().getCapability(CAP,null);
        if(c!=null&&c.stunned())e.setCanceled(true);
    }

    @SubscribeEvent
    public static void cancelBreak(PlayerEvent.BreakSpeed e){
        IStun c=e.getEntityPlayer().getCapability(CAP,null);
        if(c!=null&&c.stunned())e.setNewSpeed(0);
    }
}
