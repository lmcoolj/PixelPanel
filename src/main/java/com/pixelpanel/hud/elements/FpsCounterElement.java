package com.pixelpanel.hud.elements;

import com.pixelpanel.hud.PanelElement;
import com.pixelpanel.hud.PanelElementType;
import com.pixelpanel.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class FpsCounterElement extends PanelElement {

    public FpsCounterElement() {
        super(PanelElementType.FPS_COUNTER);
    }

    @Override
    public void render(GuiGraphicsExtractor context, float tickDelta, int screenWidth, int screenHeight) {
        Minecraft client = Minecraft.getInstance();
        Font font = client.font;
        int fps = client.getFps();

        int color;
        if (fps >= 60) color = 0xFF55FF55;
        else if (fps >= 30) color = 0xFFFFFF55;
        else color = 0xFFFF5555;

        String fpsText = fps + " FPS";

        if (showBackground()) {
            RenderUtils.drawRect(context, 0, 0, getWidth(), getHeight(), 0x80000000);
        }
        context.text(font, fpsText, 4, 4, color, true);
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
