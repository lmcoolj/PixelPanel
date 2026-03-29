package com.pixelpanel.hud.elements;

import com.pixelpanel.hud.HudElement;
import com.pixelpanel.hud.HudElementType;
import com.pixelpanel.util.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;

public class CoordinatesElement extends HudElement {

    public CoordinatesElement() {
        super(HudElementType.COORDINATES);
    }

    @Override
    public void render(DrawContext context, float tickDelta, int screenWidth, int screenHeight) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null) return;

        TextRenderer textRenderer = client.textRenderer;

        double x = player.getX();
        double y = player.getY();
        double z = player.getZ();

        String xText = String.format("X: %.1f", x);
        String yText = String.format("Y: %.1f", y);
        String zText = String.format("Z: %.1f", z);

        // Background
        if (showBackground()) {
            RenderUtils.drawRect(context, 0, 0, getWidth(), getHeight(), 0x80000000);
        }

        // Text
        int padding = 4;
        int lineHeight = textRenderer.fontHeight + 2;
        context.drawText(textRenderer, xText, padding, padding, 0xFFFFFFFF, true);
        context.drawText(textRenderer, yText, padding, padding + lineHeight, 0xFFFFFFFF, true);
        context.drawText(textRenderer, zText, padding, padding + lineHeight * 2, 0xFFFFFFFF, true);
    }

    @Override
    public int getDefaultWidth() { return 50; }

    @Override
    public int getDefaultHeight() { return 38; }

    @Override
    public String getDisplayName() { return "Coordinates"; }
}
