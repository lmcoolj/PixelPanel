package com.pixelpanel.hud.elements;

import com.pixelpanel.hud.PanelElement;
import com.pixelpanel.hud.PanelElementType;
import com.pixelpanel.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;

public class CompassElement extends PanelElement {

    private static final String[] DIRECTIONS = {"S", "SW", "W", "NW", "N", "NE", "E", "SE"};

    public CompassElement() {
        super(PanelElementType.COMPASS);
    }

    @Override
    public void render(GuiGraphicsExtractor context, float tickDelta, int screenWidth, int screenHeight) {
        Minecraft client = Minecraft.getInstance();
        Player player = client.player;
        if (player == null) return;

        Font font = client.font;

        if (showBackground()) {
            RenderUtils.drawRect(context, 0, 0, getWidth(), getHeight(), 0x80000000);
        }

        float yaw = player.getYRot() % 360;
        if (yaw < 0) yaw += 360;

        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        for (int i = 0; i < DIRECTIONS.length; i++) {
            float directionAngle = i * 45.0f;
            float diff = directionAngle - yaw;

            while (diff > 180) diff -= 360;
            while (diff < -180) diff += 360;

            float visibleRange = 90.0f;
            if (Math.abs(diff) <= visibleRange) {
                int drawX = centerX + (int)(diff * (getWidth() / 2.0f) / visibleRange);
                String dir = DIRECTIONS[i];

                int textWidth = font.width(dir);
                boolean isCardinal = dir.length() == 1;
                int color = isCardinal ? 0xFFFFFFFF : 0xFFAAAAAA;

                context.text(font, dir, drawX - textWidth / 2, centerY - font.lineHeight / 2, color, true);

                if (isCardinal) {
                    context.fill(drawX, centerY + font.lineHeight / 2 + 1, drawX + 1, centerY + font.lineHeight / 2 + 3, 0xFFFFFFFF);
                }
            }
        }

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
