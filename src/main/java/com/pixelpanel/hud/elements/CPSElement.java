package com.pixelpanel.hud.elements;

import com.pixelpanel.hud.PanelElement;
import com.pixelpanel.hud.PanelElementType;
import com.pixelpanel.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.LinkedList;

public class CPSElement extends PanelElement {

    private final LinkedList<Long> clickTimestamps = new LinkedList<>();
    private boolean wasAttackDown = false;

    public CPSElement() {
        super(PanelElementType.CPS);
    }

    @Override
    public void render(GuiGraphicsExtractor context, float tickDelta, int screenWidth, int screenHeight) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        Font font = client.font;

        // Detect attack key rising edge
        boolean attackDown = client.options.keyAttack.isDown();
        if (attackDown && !wasAttackDown) {
            clickTimestamps.add(System.currentTimeMillis());
        }
        wasAttackDown = attackDown;

        // Remove clicks older than 1 second
        long now = System.currentTimeMillis();
        while (!clickTimestamps.isEmpty() && now - clickTimestamps.getFirst() > 1000) {
            clickTimestamps.removeFirst();
        }

        int cps = clickTimestamps.size();

        if (showBackground()) {
            RenderUtils.drawRect(context, 0, 0, getWidth(), getHeight(), 0x80000000);
        }

        int color;
        if (cps >= 10) {
            color = 0xFFFF5555; // Red for high CPS
        } else if (cps >= 6) {
            color = 0xFFFFFF55; // Yellow
        } else {
            color = 0xFFFFFFFF; // White
        }

        context.text(font, cps + " CPS", 4, 4, color, true);
    }

    @Override
    public int getDefaultWidth() { return 60; }
    @Override
    public int getDefaultHeight() { return 18; }
    @Override
    public String getDisplayName() { return "CPS Counter"; }
    @Override
    public boolean isResizable() { return false; }
}
