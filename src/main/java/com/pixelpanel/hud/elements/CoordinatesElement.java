package com.pixelpanel.hud.elements;

import com.pixelpanel.hud.PanelElement;
import com.pixelpanel.hud.PanelElementType;
import com.pixelpanel.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;

public class CoordinatesElement extends PanelElement {

    public CoordinatesElement() {
        super(PanelElementType.COORDINATES);
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

        int padding = 4;
        int lineHeight = font.lineHeight + 2;

        context.text(font, String.format("X: \u00A7f%.1f", player.getX()), padding, padding, 0xFFFF6666, true);
        context.text(font, String.format("Y: \u00A7f%.1f", player.getY()), padding, padding + lineHeight, 0xFF66FF66, true);
        context.text(font, String.format("Z: \u00A7f%.1f", player.getZ()), padding, padding + lineHeight * 2, 0xFF6666FF, true);
    }

    @Override
    public int getDefaultWidth() { return 80; }
    @Override
    public int getDefaultHeight() { return 38; }
    @Override
    public String getDisplayName() { return "Coordinates"; }
}
