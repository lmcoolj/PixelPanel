package com.pixelpanel.hud.elements;

import com.pixelpanel.hud.PanelElement;
import com.pixelpanel.hud.PanelElementType;
import com.pixelpanel.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class TimeOfDayElement extends PanelElement {

    public TimeOfDayElement() {
        super(PanelElementType.TIME_OF_DAY);
    }

    @Override
    public void render(GuiGraphicsExtractor context, float tickDelta, int screenWidth, int screenHeight) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) return;

        Font font = client.font;

        if (showBackground()) {
            RenderUtils.drawRect(context, 0, 0, getWidth(), getHeight(), 0x80000000);
        }

        long timeOfDay = client.level.getOverworldClockTime() % 24000;

        int hours = (int) ((timeOfDay / 1000 + 6) % 24);
        int minutes = (int) ((timeOfDay % 1000) * 60 / 1000);

        String period = hours >= 12 ? "PM" : "AM";
        int displayHour = hours % 12;
        if (displayHour == 0) displayHour = 12;

        String timeText = String.format("%d:%02d %s", displayHour, minutes, period);

        boolean isDay = timeOfDay < 12000;
        String icon = isDay ? "\u2600" : "\u263D";
        int iconColor = isDay ? 0xFFFFDD55 : 0xFFAAAAFF;

        context.text(font, icon, 4, 4, iconColor, true);
        context.text(font, timeText, 16, 4, 0xFFFFFFFF, true);
    }

    @Override
    public int getDefaultWidth() { return 90; }
    @Override
    public int getDefaultHeight() { return 18; }
    @Override
    public String getDisplayName() { return "Time of Day"; }
    @Override
    public boolean isResizable() { return false; }
}
