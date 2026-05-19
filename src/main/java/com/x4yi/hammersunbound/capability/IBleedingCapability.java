package com.x4yi.hammersunbound.capability;

import com.x4yi.hammersunbound.effects.BleedingEffect;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public interface IBleedingCapability {

    @CapabilityInject(IBleedingCapability.class)
    Capability<IBleedingCapability> CAPABILITY = null;

    BleedingEffect getBleedingEffect();

    class BleedingCapability implements IBleedingCapability {

        private final BleedingEffect effect = new BleedingEffect();

        @Override
        public BleedingEffect getBleedingEffect() {
            return effect;
        }
    }

    class BleedingCapabilityStorage implements Capability.IStorage<IBleedingCapability> {
        @Override
        public NBTBase writeNBT(Capability<IBleedingCapability> capability, IBleedingCapability instance, EnumFacing side) {
            if (instance == null || instance.getBleedingEffect() == null) {
                return new NBTTagCompound();
            }
            return instance.getBleedingEffect().serializeNBT();
        }

        @Override
        public void readNBT(Capability<IBleedingCapability> capability, IBleedingCapability instance, EnumFacing side, NBTBase nbt) {
            if (instance != null && instance.getBleedingEffect() != null && nbt instanceof NBTTagCompound) {
                instance.getBleedingEffect().deserializeNBT((NBTTagCompound) nbt);
            }
        }
    }

    class BleedingCapabilityProvider implements ICapabilitySerializable<NBTBase> {

        private final IBleedingCapability instance = new BleedingCapability();

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
            return new BleedingCapabilityStorage().writeNBT(CAPABILITY, instance, null);
        }

        @Override
        public void deserializeNBT(NBTBase nbt) {
            new BleedingCapabilityStorage().readNBT(CAPABILITY, instance, null, nbt);
        }
    }
}
