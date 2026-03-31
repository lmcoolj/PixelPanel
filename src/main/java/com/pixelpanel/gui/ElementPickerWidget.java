package com.pixelpanel.gui;

import com.pixelpanel.config.ConfigManager;
import com.pixelpanel.hud.PanelElement;
import com.pixelpanel.hud.PanelElementRegistry;
import com.pixelpanel.hud.PanelElementType;
import com.pixelpanel.util.RenderUtils;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

public class ElementPickerWidget {
    private final int x, y, width, height;
    private final PanelElementRegistry registry;
    private final ConfigManager configManager;
    private final int screenWidth;
    private int scrollOffset = 0;

    private static final int ENTRY_HEIGHT = 26;
    private static final int PADDING = 4;
    private static final int TITLE_HEIGHT = 20;

    public ElementPickerWidget(int x, int y, int width, int height,
                               PanelElementRegistry registry, ConfigManager configManager, int screenWidth) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.registry = registry;
        this.configManager = configManager;
        this.screenWidth = screenWidth;
    }

    public void render(GuiGraphicsExtractor context, Font font, int mouseX, int mouseY) {
        // Panel background — dark purple/black
        RenderUtils.drawRect(context, x, y, width, height, 0xE8100818);
        RenderUtils.drawRectOutline(context, x, y, width, height, 0xFF6633AA);

        // Title
        context.text(font, Component.translatable("pixelpanel.picker.title"),
                x + PADDING + 2, y + PADDING + 2, 0xFFBB88FF, true);

        // Title underline
        int underlineY = y + TITLE_HEIGHT;
        context.fill(x + PADDING, underlineY, x + width - PADDING, underlineY + 1, 0xFF6633AA);

        int startY = y + TITLE_HEIGHT + PADDING;
        int listHeight = height - TITLE_HEIGHT - PADDING;
        PanelElementType[] types = PanelElementType.values();

        for (int i = 0; i < types.length; i++) {
            int entryY = startY + i * ENTRY_HEIGHT - scrollOffset;

            if (entryY + ENTRY_HEIGHT < startY || entryY > y + height) continue;

            PanelElementType type = types[i];
            boolean exists = registry.hasType(type);
            boolean hovered = mouseX >= x + PADDING && mouseX <= x + width - PADDING
                    && mouseY >= entryY && mouseY <= entryY + ENTRY_HEIGHT - 2;

            // Entry background
            int bgColor;
            if (exists) {
                bgColor = hovered ? 0x50442244 : 0x30331133;
            } else {
                bgColor = hovered ? 0x60442266 : 0x40181024;
            }
            RenderUtils.drawRect(context, x + PADDING, entryY, width - PADDING * 2, ENTRY_HEIGHT - 2, bgColor);

            // Hover border
            if (hovered) {
                RenderUtils.drawRectOutline(context, x + PADDING, entryY, width - PADDING * 2, ENTRY_HEIGHT - 2, 0x806633AA);
            }

            // Toggle indicator — filled circle for active, empty for inactive
            int dotX = x + PADDING + 3;
            int dotY = entryY + ENTRY_HEIGHT / 2 - 3;
            if (exists) {
                context.fill(dotX, dotY, dotX + 6, dotY + 6, 0xFF88DD55); // green filled
            } else {
                RenderUtils.drawRectOutline(context, dotX, dotY, 6, 6, 0xFF666666); // gray outline
            }

            // Element name
            String name = Component.translatable(type.getTranslationKey()).getString();
            int textColor = exists ? 0xFFBBDDAA : (hovered ? 0xFFDDBBFF : 0xFFBBBBBB);
            context.text(font, name, x + PADDING + 14, entryY + (ENTRY_HEIGHT - 2 - font.lineHeight) / 2, textColor, true);

            // Status text on right
            if (exists) {
                context.text(font, "ON", x + width - PADDING * 2 - font.width("ON"),
                        entryY + (ENTRY_HEIGHT - 2 - font.lineHeight) / 2, 0xFF88DD55, true);
            } else {
                context.text(font, "OFF", x + width - PADDING * 2 - font.width("OFF"),
                        entryY + (ENTRY_HEIGHT - 2 - font.lineHeight) / 2, 0xFF666666, true);
            }
        }

        // Scrollbar
        int totalContentHeight = types.length * ENTRY_HEIGHT;
        if (totalContentHeight > listHeight) {
            int scrollbarHeight = Math.max(10, listHeight * listHeight / totalContentHeight);
            int scrollbarY = startY + (int)((float) scrollOffset / (totalContentHeight - listHeight) * (listHeight - scrollbarHeight));
            int scrollbarX = x + width - 3;
            context.fill(scrollbarX, startY, scrollbarX + 2, y + height, 0x20FFFFFF);
            context.fill(scrollbarX, scrollbarY, scrollbarX + 2, scrollbarY + scrollbarHeight, 0x80BB88FF);
        }
    }

    public boolean mouseScrolled(int mouseX, int mouseY, double amount) {
        if (mouseX < x || mouseX > x + width || mouseY < y || mouseY > y + height) {
            return false;
        }

        int totalContentHeight = PanelElementType.values().length * ENTRY_HEIGHT;
        int visibleHeight = height - TITLE_HEIGHT - PADDING;
        int maxScroll = Math.max(0, totalContentHeight - visibleHeight);

        scrollOffset = (int) Math.max(0, Math.min(maxScroll, scrollOffset - amount * ENTRY_HEIGHT));
        return true;
    }

    public boolean mouseClicked(int mouseX, int mouseY) {
        if (mouseX < x || mouseX > x + width || mouseY < y || mouseY > y + height) {
            return false;
        }

        int startY = y + TITLE_HEIGHT + PADDING;
        PanelElementType[] types = PanelElementType.values();

        for (int i = 0; i < types.length; i++) {
            int entryY = startY + i * ENTRY_HEIGHT - scrollOffset;
            PanelElementType type = types[i];

            if (mouseY >= entryY && mouseY <= entryY + ENTRY_HEIGHT - 2) {
                boolean exists = registry.hasType(type);
                if (exists) {
                    // Toggle OFF — remove the element of this type
                    registry.getAll().stream()
                            .filter(e -> e.getType() == type)
                            .findFirst()
                            .ifPresent(e -> registry.remove(e.getId()));
                    configManager.save();
                } else {
                    // Toggle ON — add the element
                    PanelElement element = type.create();
                    element.setAnchorX(0.5f - (float) element.getDefaultWidth() / (2 * screenWidth));
                    element.setAnchorY(0.3f);
                    registry.add(element);
                    configManager.save();
                }
                return true;
            }
        }

        return true;
    }
}
