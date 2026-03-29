package com.pixelpanel.hud.elements;

import com.pixelpanel.hud.HudElement;
import com.pixelpanel.hud.HudElementType;
import com.pixelpanel.util.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;

public class CompassElement extends HudElement {

    private static final String[] DIRECTIONS = {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};

    public CompassElement() {
        super(HudElementType.COMPASS);
    }

    @Override
    public void render(DrawContext context, float tickDelta, int screenWidth, int screenHeight) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null) return;

        TextRenderer textRenderer = client.textRenderer;

        if (showBackground()) {
            RenderUtils.drawRect(context, 0, 0, getWidth(), getHeight(), 0x80000000);
        }

        float yaw = player.getYaw() % 360;
        if (yaw < 0) yaw += 360;

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // Draw direction markers across the compass strip
        for (int i = 0; i < DIRECTIONS.length; i++) {
            float directionAngle = i * 45.0f;
            float diff = directionAngle - yaw;

            // Normalize to -180..180
            while (diff > 180) diff -= 360;
            while (diff < -180) diff += 360;

            // Only draw if within visible range
            float visibleRange = 90.0f;
            if (Math.abs(diff) <= visibleRange) {
                int drawX = centerX + (int)(diff * (getWidth() / 2.0f) / visibleRange);
                String dir = DIRECTIONS[i];

                int textWidth = textRenderer.getWidth(dir);
                boolean isCardinal = dir.length() == 1;
                int color = isCardinal ? 0xFFFFFFFF : 0xFFAAAAAA;

                context.drawText(textRenderer, dir, drawX - textWidth / 2, centerY - textRenderer.fontHeight / 2, color, true);

                // Draw tick mark for cardinal directions
                if (isCardinal) {
                    context.fill(drawX, centerY + textRenderer.fontHeight / 2 + 1, drawX + 1, centerY + textRenderer.fontHeight / 2 + 3, 0xFFFFFFFF);
                }
            }
        }

        // Center indicator
        context.fill(centerX, 0, centerX + 1, 3, 0xFFFF5555);
        context.fill(centerX, getHeight() - 3, centerX + 1, getHeight(), 0xFFFF5555);
    }

    @Override
    public int getDefaultWidth() { return 150; }

    @Override
    public int getDefaultHeight() { return 20; }

    @Override
    public String getDisplayName() { return "Compass"; }
}
