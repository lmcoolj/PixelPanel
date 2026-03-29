package com.pixelpanel.hud.elements;

import com.pixelpanel.hud.HudElement;
import com.pixelpanel.hud.HudElementType;
import com.pixelpanel.util.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public class TimeOfDayElement extends HudElement {

    public TimeOfDayElement() {
        super(HudElementType.TIME_OF_DAY);
    }

    @Override
    public void render(DrawContext context, float tickDelta, int screenWidth, int screenHeight) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;

        TextRenderer textRenderer = client.textRenderer;

        if (showBackground()) {
            RenderUtils.drawRect(context, 0, 0, getWidth(), getHeight(), 0x80000000);
        }

        long timeOfDay = client.world.getTimeOfDay() % 24000;

        // MC time: 0 = 6:00 AM, 6000 = noon, 12000 = 6:00 PM, 18000 = midnight
        int hours = (int) ((timeOfDay / 1000 + 6) % 24);
        int minutes = (int) ((timeOfDay % 1000) * 60 / 1000);

        String period = hours >= 12 ? "PM" : "AM";
        int displayHour = hours % 12;
        if (displayHour == 0) displayHour = 12;

        String timeText = String.format("%d:%02d %s", displayHour, minutes, period);

        // Icon indicator for day/night
        boolean isDay = timeOfDay < 12000;
        String icon = isDay ? "\u2600" : "\u263D"; // Sun / Moon unicode
        int iconColor = isDay ? 0xFFFFDD55 : 0xFFAAAAFF;

        context.drawText(textRenderer, icon, 4, 4, iconColor, true);
        context.drawText(textRenderer, timeText, 16, 4, 0xFFFFFFFF, true);
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
