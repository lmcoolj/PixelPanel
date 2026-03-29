package com.pixelpanel;

import com.pixelpanel.config.ConfigManager;
import com.pixelpanel.gui.HudEditScreen;
import com.pixelpanel.hud.PanelElementRegistry;
import com.pixelpanel.hud.HudRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class PixelPanelClient implements ClientModInitializer {
    public static final String MOD_ID = "pixelpanel";

    private static KeyMapping openEditorKey;
    private static PanelElementRegistry registry;
    private static ConfigManager configManager;

    @Override
    public void onInitializeClient() {
        registry = new PanelElementRegistry();
        configManager = new ConfigManager(registry);
        configManager.load();

        openEditorKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "pixelpanel.key.open_editor",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                KeyMapping.Category.MISC
        ));

        HudElementRegistry.addLast(
                Identifier.fromNamespaceAndPath(MOD_ID, "hud_renderer"),
                new HudRenderer(registry)
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openEditorKey.consumeClick()) {
                if (client.screen == null) {
                    client.setScreen(new HudEditScreen(registry, configManager));
                }
            }
        });
    }

    public static PanelElementRegistry getRegistry() {
        return registry;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }
}
