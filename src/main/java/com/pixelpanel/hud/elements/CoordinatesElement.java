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

        String xText = String.format("X: %.1f", player.getX());
        String yText = String.format("Y: %.1f", player.getY());
        String zText = String.format("Z: %.1f", player.getZ());

        if (showBackground()) {
            RenderUtils.drawRect(context, 0, 0, getWidth(), getHeight(), 0x80000000);
        }

        int padding = 4;
        int lineHeight = font.lineHeight + 2;
        context.text(font, xText, padding, padding, 0xFFFFFFFF, true);
        context.text(font, yText, padding, padding + lineHeight, 0xFFFFFFFF, true);
        context.text(font, zText, padding, padding + lineHeight * 2, 0xFFFFFFFF, true);
    }

    @Override
    public int getDefaultWidth() { return 50; }
    @Override
    public int getDefaultHeight() { return 38; }
    @Override
    public String getDisplayName() { return "Coordinates"; }
}
