package com.pixelpanel.hud.elements;

import com.pixelpanel.hud.HudElement;
import com.pixelpanel.hud.HudElementType;
import com.pixelpanel.util.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class FpsCounterElement extends HudElement {

    public FpsCounterElement() {
        super(HudElementType.FPS_COUNTER);
    }

    @Override
    public void render(DrawContext context, float tickDelta, int screenWidth, int screenHeight) {
        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;

        int fps = client.getCurrentFps();

        int color;
        if (fps >= 60) {
            color = 0xFF55FF55; // Green
        } else if (fps >= 30) {
            color = 0xFFFFFF55; // Yellow
        } else {
            color = 0xFFFF5555; // Red
        }

        String fpsText = fps + " FPS";

        if (showBackground()) {
            RenderUtils.drawRect(context, 0, 0, getWidth(), getHeight(), 0x80000000);
        }
        context.drawText(textRenderer, fpsText, 4, 4, color, true);
    }

    @Override
    public int getDefaultWidth() { return 70; }

    @Override
    public int getDefaultHeight() { return 18; }

    @Override
    public String getDisplayName() { return "FPS Counter"; }

    @Override
    public boolean isResizable() { return false; }
}
