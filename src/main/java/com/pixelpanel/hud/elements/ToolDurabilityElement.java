package com.pixelpanel.hud.elements;

import com.pixelpanel.hud.HudElement;
import com.pixelpanel.hud.HudElementType;
import com.pixelpanel.util.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class ToolDurabilityElement extends HudElement {

    public ToolDurabilityElement() {
        super(HudElementType.TOOL_DURABILITY);
    }

    @Override
    public void render(DrawContext context, float tickDelta, int screenWidth, int screenHeight) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null) return;

        TextRenderer textRenderer = client.textRenderer;
        ItemStack heldItem = player.getMainHandStack();

        if (showBackground()) {
            RenderUtils.drawRect(context, 0, 0, getWidth(), getHeight(), 0x80000000);
        }

        if (heldItem.isEmpty() || !heldItem.isDamageable()) {
            context.drawText(textRenderer, "No tool", 4, 4, 0xFF888888, true);
            return;
        }

        // Draw item icon
        context.drawItem(heldItem, 4, (getHeight() - 16) / 2);

        // Durability info
        int maxDamage = heldItem.getMaxDamage();
        int currentDamage = heldItem.getDamage();
        int remaining = maxDamage - currentDamage;
        float ratio = (float) remaining / maxDamage;

        String durText = remaining + " / " + maxDamage;
        int durColor = RenderUtils.getDurabilityColor(ratio);

        context.drawText(textRenderer, durText, 24, 4, durColor, true);

        // Durability bar
        int barX = 24;
        int barY = 4 + textRenderer.fontHeight + 2;
        int barWidth = getWidth() - 28;
        int barHeight = 4;

        RenderUtils.drawRect(context, barX, barY, barWidth, barHeight, 0xFF333333);
        RenderUtils.drawRect(context, barX, barY, (int)(barWidth * ratio), barHeight, durColor);
    }

    @Override
    public int getDefaultWidth() { return 130; }

    @Override
    public int getDefaultHeight() { return 28; }

    @Override
    public int getMinWidth() { return 80; }

    @Override
    public int getMinHeight() { return 28; }

    @Override
    public int getMaxWidth() { return 200; }

    @Override
    public int getMaxHeight() { return 40; }

    @Override
    public String getDisplayName() { return "Tool Durability"; }
}
