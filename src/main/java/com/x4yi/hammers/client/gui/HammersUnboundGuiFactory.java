package com.x4yi.hammers.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;

import java.util.Set;

public class HammersUnboundGuiFactory implements IModGuiFactory {

    @Override
    public void initialize(Minecraft minecraftInstance) {
    }

    @Override
    public boolean hasConfigGui() {
        return true;
    }

    @Override
    public GuiScreen createConfigGui(GuiScreen parentScreen) {
        return new com.x4yi.hammers.client.gui.GuiHammersUnboundConfig(parentScreen);
    }

    @Override
    public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }
}
