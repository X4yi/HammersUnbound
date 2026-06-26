package com.x4yi.hammersunbound.client.input;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MouseHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
@SideOnly(Side.CLIENT)
public class StunMouseHelper extends MouseHelper {
    private final MouseHelper parent;
    public StunMouseHelper(MouseHelper parent) {
        this.parent = parent;
    }
    @Override
    public void mouseXYChange() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player != null && com.x4yi.hammersunbound.init.ModPotions.STUN != null && mc.player.isPotionActive(com.x4yi.hammersunbound.init.ModPotions.STUN)) {
            this.deltaX = 0;
            this.deltaY = 0;
        } else {
            this.parent.mouseXYChange();
            this.deltaX = this.parent.deltaX;
            this.deltaY = this.parent.deltaY;
        }
    }
    @Override
    public void grabMouseCursor() {
        this.parent.grabMouseCursor();
    }
    @Override
    public void ungrabMouseCursor() {
        this.parent.ungrabMouseCursor();
    }
}