package com.pixelpanel.gui;

import com.pixelpanel.hud.HudElement;
import com.pixelpanel.hud.HudElementRegistry;
import com.pixelpanel.hud.HudElementType;
import com.pixelpanel.util.RenderUtils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class ElementPickerWidget {
    private final int x, y, width, height;
    private final HudElementRegistry registry;
    private final Consumer<HudElement> onElementAdded;
    private int scrollOffset = 0;

    private static final int ENTRY_HEIGHT = 24;
    private static final int PADDING = 4;

    public ElementPickerWidget(int x, int y, int width, int height, HudElementRegistry registry, Consumer<HudElement> onElementAdded) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.registry = registry;
        this.onElementAdded = onElementAdded;
    }

    public void render(DrawContext context, TextRenderer textRenderer, int mouseX, int mouseY) {
        // Background
        RenderUtils.drawRect(context, x, y, width, height, 0xE0222222);
        RenderUtils.drawRectOutline(context, x, y, width, height, 0xFF555555);

        // Title
        context.drawText(textRenderer, Text.translatable("pixelpanel.picker.title"), x + PADDING, y + PADDING, 0xFFFFFFFF, true);

        int startY = y + PADDING + textRenderer.fontHeight + PADDING;
        HudElementType[] types = HudElementType.values();

        for (int i = 0; i < types.length; i++) {
            int entryY = startY + i * ENTRY_HEIGHT - scrollOffset;

            if (entryY < startY - ENTRY_HEIGHT || entryY > y + height) continue;

            HudElementType type = types[i];
            boolean exists = !type.isAllowMultiple() && registry.hasType(type);
            boolean hovered = mouseX >= x + PADDING && mouseX <= x + width - PADDING
                    && mouseY >= entryY && mouseY <= entryY + ENTRY_HEIGHT - 2;

            // Entry background
            int bgColor = exists ? 0x40444444 : (hovered ? 0x60555588 : 0x40333333);
            RenderUtils.drawRect(context, x + PADDING, entryY, width - PADDING * 2, ENTRY_HEIGHT - 2, bgColor);

            // Entry text
            String name = Text.translatable(type.getTranslationKey()).getString();
            int textColor = exists ? 0xFF666666 : 0xFFFFFFFF;
            context.drawText(textRenderer, name, x + PADDING * 2, entryY + (ENTRY_HEIGHT - 2 - textRenderer.fontHeight) / 2, textColor, true);

            // "Added" indicator
            if (exists) {
                context.drawText(textRenderer, "\u2713", x + width - PADDING * 2 - textRenderer.getWidth("\u2713"), entryY + (ENTRY_HEIGHT - 2 - textRenderer.fontHeight) / 2, 0xFF55FF55, true);
            }
        }
    }

    public boolean mouseScrolled(int mouseX, int mouseY, double amount) {
        if (mouseX < x || mouseX > x + width || mouseY < y || mouseY > y + height) {
            return false;
        }

        int totalContentHeight = HudElementType.values().length * ENTRY_HEIGHT;
        int visibleHeight = height - PADDING - 9 - PADDING; // subtract title area
        int maxScroll = Math.max(0, totalContentHeight - visibleHeight);

        scrollOffset = (int) Math.max(0, Math.min(maxScroll, scrollOffset - amount * ENTRY_HEIGHT));
        return true;
    }

    public boolean mouseClicked(int mouseX, int mouseY) {
        if (mouseX < x || mouseX > x + width || mouseY < y || mouseY > y + height) {
            return false;
        }

        int startY = y + PADDING + 9 + PADDING; // After title
        HudElementType[] types = HudElementType.values();

        for (int i = 0; i < types.length; i++) {
            int entryY = startY + i * ENTRY_HEIGHT - scrollOffset;
            HudElementType type = types[i];

            if (mouseY >= entryY && mouseY <= entryY + ENTRY_HEIGHT - 2) {
                boolean exists = !type.isAllowMultiple() && registry.hasType(type);
                if (!exists) {
                    HudElement element = type.create();
                    onElementAdded.accept(element);
                }
                return true;
            }
        }

        return true; // Consume click even if not on an entry
    }
}
