package com.pixelpanel.hud.elements;

import com.pixelpanel.hud.PanelElement;
import com.pixelpanel.hud.PanelElementType;
import com.pixelpanel.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;

public class BiomeElement extends PanelElement {

    public BiomeElement() {
        super(PanelElementType.BIOME);
    }

    @Override
    public void render(GuiGraphicsExtractor context, float tickDelta, int screenWidth, int screenHeight) {
        Minecraft client = Minecraft.getInstance();
        Player player = client.player;
        if (player == null || client.level == null) return;

        Font font = client.font;
        BlockPos blockPos = player.blockPosition();

        Holder<Biome> biomeEntry = client.level.getBiome(blockPos);
        String biomeName = biomeEntry.unwrapKey()
                .map(key -> formatBiomeName(key.identifier().getPath()))
                .orElse((String) "Unknown");

        if (showBackground()) {
            RenderUtils.drawRect(context, 0, 0, getWidth(), getHeight(), 0x80000000);
        }

        context.text(font, "\u2618 " + biomeName, 4, 4, 0xFF88DD88, true);
    }

    private String formatBiomeName(String raw) {
        String[] parts = raw.split("_");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) sb.append(' ');
            if (!parts[i].isEmpty()) {
                sb.append(Character.toUpperCase(parts[i].charAt(0)));
                sb.append(parts[i].substring(1));
            }
        }
        return sb.toString();
    }

    @Override
    public int getDefaultWidth() { return 120; }
    @Override
    public int getDefaultHeight() { return 18; }
    @Override
    public String getDisplayName() { return "Biome"; }
    @Override
    public boolean isResizable() { return false; }
}
