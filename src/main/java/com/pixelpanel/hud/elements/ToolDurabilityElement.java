package com.pixelpanel.hud.elements;

import com.pixelpanel.hud.PanelElement;
import com.pixelpanel.hud.PanelElementType;
import com.pixelpanel.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ToolDurabilityElement extends PanelElement {

    public ToolDurabilityElement() {
        super(PanelElementType.TOOL_DURABILITY);
    }

    @Override
    public void render(GuiGraphicsExtractor context, float tickDelta, int screenWidth, int screenHeight) {
        Minecraft client = Minecraft.getInstance();
        Player player = client.player;
        if (player == null) return;

        Font font = client.font;
        ItemStack heldItem = player.getMainHandItem();

        if (showBackground()) {
            RenderUtils.drawRect(context, 0, 0, getWidth(), getHeight(), 0x80000000);
        }

        if (heldItem.isEmpty() || !heldItem.isDamageableItem()) {
            context.text(font, "No tool", 4, 4, 0xFF888888, true);
            return;
        }

        context.item(heldItem, 4, (getHeight() - 16) / 2);

        int maxDamage = heldItem.getMaxDamage();
        int currentDamage = heldItem.getDamageValue();
        int remaining = maxDamage - currentDamage;
        float ratio = (float) remaining / maxDamage;

        String durText = remaining + " / " + maxDamage;
        int durColor = RenderUtils.getDurabilityColor(ratio);

        context.text(font, durText, 24, 4, durColor, true);

        int barX = 24;
        int barY = 4 + font.lineHeight + 2;
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
