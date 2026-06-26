package com.x4yi.hammersunbound.capability;
import com.x4yi.hammersunbound.effects.BloodPactEffect;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
public interface IBloodPactCapability {
    @CapabilityInject(IBloodPactCapability.class)
    Capability<IBloodPactCapability> CAPABILITY = null;
    BloodPactEffect getBloodPactEffect();
    class BloodPactCapability implements IBloodPactCapability {
        private final BloodPactEffect effect = new BloodPactEffect();
        @Override
        public BloodPactEffect getBloodPactEffect() {
            return effect;
        }
    }
    class BloodPactCapabilityStorage implements Capability.IStorage<IBloodPactCapability> {
        @Override
        public NBTBase writeNBT(Capability<IBloodPactCapability> capability, IBloodPactCapability instance, EnumFacing side) {
            if (instance == null || instance.getBloodPactEffect() == null) {
                return new NBTTagCompound();
            }
            return instance.getBloodPactEffect().serializeNBT();
        }
        @Override
        public void readNBT(Capability<IBloodPactCapability> capability, IBloodPactCapability instance, EnumFacing side, NBTBase nbt) {
            if (instance != null && instance.getBloodPactEffect() != null && nbt instanceof NBTTagCompound) {
                instance.getBloodPactEffect().deserializeNBT((NBTTagCompound) nbt);
            }
        }
    }
    class BloodPactCapabilityProvider implements ICapabilitySerializable<NBTBase> {
        private final IBloodPactCapability instance = new BloodPactCapability();
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
            return new BloodPactCapabilityStorage().writeNBT(CAPABILITY, instance, null);
        }
        @Override
        public void deserializeNBT(NBTBase nbt) {
            new BloodPactCapabilityStorage().readNBT(CAPABILITY, instance, null, nbt);
        }
    }
}