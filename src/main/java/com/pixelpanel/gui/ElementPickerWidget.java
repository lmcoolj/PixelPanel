package com.pixelpanel.gui;

import com.pixelpanel.hud.PanelElement;
import com.pixelpanel.hud.PanelElementRegistry;
import com.pixelpanel.hud.PanelElementType;
import com.pixelpanel.util.RenderUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;

public class ElementPickerWidget {
    private final int x, y, width, height;
    private final PanelElementRegistry registry;
    private final Consumer<PanelElement> onElementAdded;
    private int scrollOffset = 0;

    private static final int ENTRY_HEIGHT = 24;
    private static final int PADDING = 4;

    public ElementPickerWidget(int x, int y, int width, int height, PanelElementRegistry registry, Consumer<PanelElement> onElementAdded) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.registry = registry;
        this.onElementAdded = onElementAdded;
    }

    public void render(GuiGraphicsExtractor context, Font font, int mouseX, int mouseY) {
        RenderUtils.drawRect(context, x, y, width, height, 0xE0222222);
        RenderUtils.drawRectOutline(context, x, y, width, height, 0xFF555555);

        context.text(font, Component.translatable("pixelpanel.picker.title"), x + PADDING, y + PADDING, 0xFFFFFFFF, true);

        int startY = y + PADDING + font.lineHeight + PADDING;
        PanelElementType[] types = PanelElementType.values();

        for (int i = 0; i < types.length; i++) {
            int entryY = startY + i * ENTRY_HEIGHT - scrollOffset;

            if (entryY < startY - ENTRY_HEIGHT || entryY > y + height) continue;

            PanelElementType type = types[i];
            boolean exists = !type.isAllowMultiple() && registry.hasType(type);
            boolean hovered = mouseX >= x + PADDING && mouseX <= x + width - PADDING
                    && mouseY >= entryY && mouseY <= entryY + ENTRY_HEIGHT - 2;

            int bgColor = exists ? 0x40444444 : (hovered ? 0x60555588 : 0x40333333);
            RenderUtils.drawRect(context, x + PADDING, entryY, width - PADDING * 2, ENTRY_HEIGHT - 2, bgColor);

            String name = Component.translatable(type.getTranslationKey()).getString();
            int textColor = exists ? 0xFF666666 : 0xFFFFFFFF;
            context.text(font, name, x + PADDING * 2, entryY + (ENTRY_HEIGHT - 2 - font.lineHeight) / 2, textColor, true);

            if (exists) {
                context.text(font, "\u2713", x + width - PADDING * 2 - font.width("\u2713"), entryY + (ENTRY_HEIGHT - 2 - font.lineHeight) / 2, 0xFF55FF55, true);
            }
        }
    }

    public boolean mouseScrolled(int mouseX, int mouseY, double amount) {
        if (mouseX < x || mouseX > x + width || mouseY < y || mouseY > y + height) {
            return false;
        }

        int totalContentHeight = PanelElementType.values().length * ENTRY_HEIGHT;
        int visibleHeight = height - PADDING - 9 - PADDING;
        int maxScroll = Math.max(0, totalContentHeight - visibleHeight);

        scrollOffset = (int) Math.max(0, Math.min(maxScroll, scrollOffset - amount * ENTRY_HEIGHT));
        return true;
    }

    public boolean mouseClicked(int mouseX, int mouseY) {
        if (mouseX < x || mouseX > x + width || mouseY < y || mouseY > y + height) {
            return false;
        }

        int startY = y + PADDING + 9 + PADDING;
        PanelElementType[] types = PanelElementType.values();

        for (int i = 0; i < types.length; i++) {
            int entryY = startY + i * ENTRY_HEIGHT - scrollOffset;
            PanelElementType type = types[i];

            if (mouseY >= entryY && mouseY <= entryY + ENTRY_HEIGHT - 2) {
                boolean exists = !type.isAllowMultiple() && registry.hasType(type);
                if (!exists) {
                    PanelElement element = type.create();
                    onElementAdded.accept(element);
                }
                return true;
            }
        }

        return true;
    }
}
