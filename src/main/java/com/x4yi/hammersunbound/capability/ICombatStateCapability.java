package com.x4yi.hammersunbound.capability;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
public interface ICombatStateCapability {
    @CapabilityInject(ICombatStateCapability.class)
    Capability<ICombatStateCapability> CAPABILITY = null;
    boolean hasSkybreakerJumpBuff();
    void setSkybreakerJumpBuff(boolean val);
    boolean hasSkybreakerImmunity();
    void setSkybreakerImmunity(boolean val);
    int getSkybreakerJumpTicks();
    void setSkybreakerJumpTicks(int val);
    long getLastSpikeHammerAoETick();
    void setLastSpikeHammerAoETick(long val);
    class CombatStateCapability implements ICombatStateCapability {
        private boolean jumpBuff;
        private boolean immunity;
        private int jumpTicks;
        private long lastAoETick;
        @Override public boolean hasSkybreakerJumpBuff() { return jumpBuff; }
        @Override public void setSkybreakerJumpBuff(boolean val) { this.jumpBuff = val; }
        @Override public boolean hasSkybreakerImmunity() { return immunity; }
        @Override public void setSkybreakerImmunity(boolean val) { this.immunity = val; }
        @Override public int getSkybreakerJumpTicks() { return jumpTicks; }
        @Override public void setSkybreakerJumpTicks(int val) { this.jumpTicks = val; }
        @Override public long getLastSpikeHammerAoETick() { return lastAoETick; }
        @Override public void setLastSpikeHammerAoETick(long val) { this.lastAoETick = val; }
    }
    class CombatStateCapabilityStorage implements Capability.IStorage<ICombatStateCapability> {
        @Override
        public NBTBase writeNBT(Capability<ICombatStateCapability> capability, ICombatStateCapability instance, EnumFacing side) {
            NBTTagCompound nbt = new NBTTagCompound();
            if (instance != null) {
                nbt.setBoolean("JumpBuff", instance.hasSkybreakerJumpBuff());
                nbt.setBoolean("Immunity", instance.hasSkybreakerImmunity());
                nbt.setInteger("JumpTicks", instance.getSkybreakerJumpTicks());
                nbt.setLong("LastAoETick", instance.getLastSpikeHammerAoETick());
            }
            return nbt;
        }
        @Override
        public void readNBT(Capability<ICombatStateCapability> capability, ICombatStateCapability instance, EnumFacing side, NBTBase nbt) {
            if (instance != null && nbt instanceof NBTTagCompound) {
                NBTTagCompound tag = (NBTTagCompound) nbt;
                instance.setSkybreakerJumpBuff(tag.getBoolean("JumpBuff"));
                instance.setSkybreakerImmunity(tag.getBoolean("Immunity"));
                instance.setSkybreakerJumpTicks(tag.getInteger("JumpTicks"));
                instance.setLastSpikeHammerAoETick(tag.getLong("LastAoETick"));
            }
        }
    }
    class CombatStateCapabilityProvider implements ICapabilitySerializable<NBTBase> {
        private final ICombatStateCapability instance = new CombatStateCapability();
        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
            return CAPABILITY != null && capability == CAPABILITY;
        }
        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            if (CAPABILITY != null && capability == CAPABILITY) {
                return CAPABILITY.cast(instance);
            }
            return null;
        }
        @Override
        public NBTBase serializeNBT() {
            return new CombatStateCapabilityStorage().writeNBT(CAPABILITY, instance, null);
        }
        @Override
        public void deserializeNBT(NBTBase nbt) {
            new CombatStateCapabilityStorage().readNBT(CAPABILITY, instance, null, nbt);
        }
    }
}