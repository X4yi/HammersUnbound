package com.x4yi.hammers.handlers.WarHammerCore;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.*;
import net.minecraftforge.fml.common.network.simpleimpl.*;
import net.minecraftforge.fml.relauncher.Side;

public class WarHammerNetwork {

    public static final SimpleNetworkWrapper NET=
            NetworkRegistry.INSTANCE.newSimpleChannel("wh_core");

    public static void init(){
        NET.registerMessage(SyncHandler.class,PacketSync.class,0,Side.CLIENT);
        NET.registerMessage(WarHammerParticles.Handler.class,
                WarHammerParticles.PacketAOE.class,
                1,
                Side.CLIENT);

    }

    public static void sync(EntityLivingBase e,boolean s,int ticks){
        if(!(e.world instanceof WorldServer))return;
        WorldServer w=(WorldServer)e.world;
        PacketSync pkt=new PacketSync(e.getEntityId(),s,ticks);
        for(EntityPlayerMP p:w.getPlayers(EntityPlayerMP.class,x->x.getDistanceSq(e)<256*256))
            NET.sendTo(pkt,p);
    }

    public static class PacketSync implements IMessage{
        int id,t;boolean s;
        public PacketSync(){}
        public PacketSync(int i,boolean s,int t){id=i;this.s=s;this.t=t;}
        public void fromBytes(ByteBuf b){id=b.readInt();s=b.readBoolean();t=b.readInt();}
        public void toBytes(ByteBuf b){b.writeInt(id);b.writeBoolean(s);b.writeInt(t);}
    }

    public static class SyncHandler implements IMessageHandler<PacketSync,IMessage>{
        public IMessage onMessage(PacketSync m,MessageContext c){
            net.minecraft.client.Minecraft.getMinecraft().addScheduledTask(()->{
                Entity e=net.minecraft.client.Minecraft.getMinecraft().world.getEntityByID(m.id);
                if(e instanceof EntityLivingBase){
                    WarHammerStun.IStun cap=e.getCapability(WarHammerStun.CAP,null);
                    if(cap!=null){cap.set(m.s);cap.setTicks(m.t);}
                }
            });
            return null;
        }
    }
}
