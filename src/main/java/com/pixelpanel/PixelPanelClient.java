package com.pixelpanel;

import com.pixelpanel.config.ConfigManager;
import com.pixelpanel.gui.HudEditScreen;
import com.pixelpanel.hud.HudElementRegistry;
import com.pixelpanel.hud.HudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class PixelPanelClient implements ClientModInitializer {
    public static final String MOD_ID = "pixelpanel";

    private static KeyBinding openEditorKey;
    private static HudElementRegistry registry;
    private static ConfigManager configManager;

    @Override
    public void onInitializeClient() {
        registry = new HudElementRegistry();
        configManager = new ConfigManager(registry);
        configManager.load();

        openEditorKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "pixelpanel.key.open_editor",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                KeyBinding.Category.MISC
        ));

        HudRenderCallback.EVENT.register(new HudRenderer(registry));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openEditorKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new HudEditScreen(registry, configManager));
                }
            }
        });
    }

    public static HudElementRegistry getRegistry() {
        return registry;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }
}
