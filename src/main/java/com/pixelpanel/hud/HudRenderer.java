package com.pixelpanel.hud;

import com.pixelpanel.gui.HudEditScreen;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class HudRenderer implements HudElement {
    private final PanelElementRegistry registry;

    public HudRenderer(PanelElementRegistry registry) {
        this.registry = registry;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, DeltaTracker deltaTracker) {
        Minecraft client = Minecraft.getInstance();

        if (client.screen instanceof HudEditScreen) {
            return;
        }

        if (client.player == null || client.level == null) {
            return;
        }

        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();
        float tickDelta = deltaTracker.getGameTimeDeltaPartialTick(true);

        for (PanelElement element : registry.getAll()) {
            if (!element.isVisible()) continue;

            int x = element.getScreenX(screenWidth);
            int y = element.getScreenY(screenHeight);

            context.pose().pushMatrix();
            context.pose().translate(x, y);
            context.pose().scale(element.getScale(), element.getScale());

            element.render(context, tickDelta, screenWidth, screenHeight);

            context.pose().popMatrix();
        }
    }
}
