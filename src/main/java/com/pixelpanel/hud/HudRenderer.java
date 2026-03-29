package com.pixelpanel.hud;

import com.pixelpanel.gui.HudEditScreen;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;

public class HudRenderer implements HudRenderCallback {
    private final HudElementRegistry registry;

    public HudRenderer(HudElementRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();

        // Don't render HUD elements when the edit screen is open (it renders them itself)
        if (client.currentScreen instanceof HudEditScreen) {
            return;
        }

        if (client.player == null || client.world == null) {
            return;
        }

        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
        float tickDelta = tickCounter.getTickProgress(true);

        for (HudElement element : registry.getAll()) {
            if (!element.isVisible()) continue;

            int x = element.getScreenX(screenWidth);
            int y = element.getScreenY(screenHeight);

            context.getMatrices().pushMatrix();
            context.getMatrices().translate(x, y);
            context.getMatrices().scale(element.getScale(), element.getScale());

            element.render(context, tickDelta, screenWidth, screenHeight);

            context.getMatrices().popMatrix();
        }
    }
}
