package com.x4yi.hammersunbound.network;
import com.x4yi.hammersunbound.client.gui.GuiHammerForge;
import com.x4yi.hammersunbound.inventory.ContainerHammerForge;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
public class ModGuiHandler implements IGuiHandler {
    public static final int HAMMER_FORGE_GUI = 0;
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == HAMMER_FORGE_GUI) {
            return new ContainerHammerForge(player.inventory);
        }
        return null;
    }
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == HAMMER_FORGE_GUI) {
            return new GuiHammerForge(player);
        }
        return null;
    }
}