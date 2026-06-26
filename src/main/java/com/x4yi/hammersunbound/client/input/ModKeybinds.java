package com.x4yi.hammersunbound.client.input;
import com.x4yi.hammersunbound.HammersUnbound;
import com.x4yi.hammersunbound.client.gui.GuiConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;
public class ModKeybinds {
    public static final KeyBinding OPEN_CONFIG = new KeyBinding(
            "key." + HammersUnbound.MODID + ".open_config",
            Keyboard.KEY_APOSTROPHE,
            "key.category." + HammersUnbound.MODID
    );
    public static void register() {
        ClientRegistry.registerKeyBinding(OPEN_CONFIG);
    }
    @SubscribeEvent
    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (OPEN_CONFIG.isPressed()) {
            Minecraft mc = Minecraft.getMinecraft();
            if (mc.currentScreen == null) {
                mc.displayGuiScreen(new GuiConfigScreen(null));
            }
        }
    }
}