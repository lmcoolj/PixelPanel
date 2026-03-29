package com.pixelpanel.hud.elements;

import com.pixelpanel.hud.PanelElement;
import com.pixelpanel.hud.PanelElementType;
import com.pixelpanel.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ArmorDurabilityElement extends PanelElement {

    public ArmorDurabilityElement() {
        super(PanelElementType.ARMOR_DURABILITY);
    }

    @Override
    public void render(GuiGraphicsExtractor context, float tickDelta, int screenWidth, int screenHeight) {
        Minecraft client = Minecraft.getInstance();
        Player player = client.player;
        if (player == null) return;

        Font font = client.font;

        if (showBackground()) {
            RenderUtils.drawRect(context, 0, 0, getWidth(), getHeight(), 0x80000000);
        }

        EquipmentSlot[] armorSlots = { EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };
        int yOffset = 2;
        boolean hasArmor = false;

        for (EquipmentSlot slot : armorSlots) {
            ItemStack armorPiece = player.getItemBySlot(slot);
            if (armorPiece.isEmpty()) continue;

            hasArmor = true;
            context.item(armorPiece, 2, yOffset);

            if (armorPiece.isDamageableItem()) {
                int maxDamage = armorPiece.getMaxDamage();
                int currentDamage = armorPiece.getDamageValue();
                int remaining = maxDamage - currentDamage;
                float ratio = (float) remaining / maxDamage;
                int durColor = RenderUtils.getDurabilityColor(ratio);

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
            context.text(font, "No armor", 4, 4, 0xFF888888, true);
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
