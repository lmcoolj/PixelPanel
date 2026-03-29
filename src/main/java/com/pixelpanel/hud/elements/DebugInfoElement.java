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
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.List;

public class DebugInfoElement extends PanelElement {

    public DebugInfoElement() {
        super(PanelElementType.DEBUG_INFO);
    }

    @Override
    public void render(GuiGraphicsExtractor context, float tickDelta, int screenWidth, int screenHeight) {
        Minecraft client = Minecraft.getInstance();
        Player player = client.player;
        if (player == null || client.level == null) return;

        Font font = client.font;
        List<String> lines = new ArrayList<>();

        lines.add("FPS: " + client.getFps());
        lines.add(String.format("XYZ: %.1f / %.1f / %.1f", player.getX(), player.getY(), player.getZ()));

        BlockPos blockPos = player.blockPosition();
        lines.add(String.format("Block: %d %d %d", blockPos.getX(), blockPos.getY(), blockPos.getZ()));

        int chunkX = blockPos.getX() >> 4;
        int chunkZ = blockPos.getZ() >> 4;
        lines.add(String.format("Chunk: %d %d", chunkX, chunkZ));

        String facing = getFacingDirection(player.getYRot());
        lines.add("Facing: " + facing + String.format(" (%.1f)", player.getYRot()));

        Holder<Biome> biomeEntry = client.level.getBiome(blockPos);
        String biomeName = biomeEntry.unwrapKey()
                .map(key -> key.identifier().getPath())
                .orElse((String) "unknown");
        lines.add("Biome: " + biomeName);

        int blockLight = client.level.getBrightness(LightLayer.BLOCK, blockPos);
        int skyLight = client.level.getBrightness(LightLayer.SKY, blockPos);
        lines.add(String.format("Light: %d (block: %d, sky: %d)", Math.max(blockLight, skyLight), blockLight, skyLight));

        int lineHeight = font.lineHeight + 2;
        int totalHeight = lines.size() * lineHeight + 6;
        int maxWidth = 4;
        for (String line : lines) {
            maxWidth = Math.max(maxWidth, font.width(line) + 8);
        }

        if (showBackground()) {
            RenderUtils.drawRect(context, 0, 0, Math.max(getWidth(), maxWidth), Math.max(getHeight(), totalHeight), 0x80000000);
        }

        int yPos = 3;
        for (String line : lines) {
            context.text(font, line, 4, yPos, 0xFFFFFFFF, true);
            yPos += lineHeight;
        }
    }

    private String getFacingDirection(float yaw) {
        yaw = yaw % 360;
        if (yaw < 0) yaw += 360;

        if (yaw >= 337.5 || yaw < 22.5) return "South";
        if (yaw < 67.5) return "Southwest";
        if (yaw < 112.5) return "West";
        if (yaw < 157.5) return "Northwest";
        if (yaw < 202.5) return "North";
        if (yaw < 247.5) return "Northeast";
        if (yaw < 292.5) return "East";
        return "Southeast";
    }

    @Override
    public int getDefaultWidth() { return 200; }
    @Override
    public int getDefaultHeight() { return 80; }
    @Override
    public String getDisplayName() { return "Debug Info"; }
}
