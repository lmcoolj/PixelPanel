package com.pixelpanel.hud.elements;

import com.pixelpanel.hud.HudElement;
import com.pixelpanel.hud.HudElementType;
import com.pixelpanel.util.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class ArmorDurabilityElement extends HudElement {

    public ArmorDurabilityElement() {
        super(HudElementType.ARMOR_DURABILITY);
    }

    @Override
    public void render(DrawContext context, float tickDelta, int screenWidth, int screenHeight) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null) return;

        TextRenderer textRenderer = client.textRenderer;

        if (showBackground()) {
            RenderUtils.drawRect(context, 0, 0, getWidth(), getHeight(), 0x80000000);
        }

        EquipmentSlot[] armorSlots = { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };
        int yOffset = 2;
        boolean hasArmor = false;

        for (EquipmentSlot slot : armorSlots) {
            ItemStack armorPiece = player.getEquippedStack(slot);
            if (armorPiece.isEmpty()) continue;

            hasArmor = true;

            // Draw item icon
            context.drawItem(armorPiece, 2, yOffset);

            if (armorPiece.isDamageable()) {
                int maxDamage = armorPiece.getMaxDamage();
                int currentDamage = armorPiece.getDamage();
                int remaining = maxDamage - currentDamage;
                float ratio = (float) remaining / maxDamage;
                int durColor = RenderUtils.getDurabilityColor(ratio);

                // Durability bar
                int barX = 22;
                int barY = yOffset + 6;
                int barWidth = getWidth() - 26;
                int barHeight = 4;

                RenderUtils.drawRect(context, barX, barY, barWidth, barHeight, 0xFF333333);
                RenderUtils.drawRect(context, barX, barY, (int)(barWidth * ratio), barHeight, durColor);
            }

            yOffset += 20;
        }

        if (!hasArmor) {
            context.drawText(textRenderer, "No armor", 4, 4, 0xFF888888, true);
        }
    }

    @Override
    public int getDefaultWidth() { return 110; }

    @Override
    public int getDefaultHeight() { return 82; }

    @Override
    public int getMinWidth() { return 80; }

    @Override
    public int getMinHeight() { return 82; }

    @Override
    public int getMaxWidth() { return 180; }

    @Override
    public int getMaxHeight() { return 90; }

    @Override
    public String getDisplayName() { return "Armor Durability"; }
}
