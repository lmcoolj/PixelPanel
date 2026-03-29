package com.pixelpanel.hud.elements;

import com.pixelpanel.hud.HudElement;
import com.pixelpanel.hud.HudElementType;
import com.pixelpanel.util.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.LightType;

import java.util.ArrayList;
import java.util.List;

public class DebugInfoElement extends HudElement {

    public DebugInfoElement() {
        super(HudElementType.DEBUG_INFO);
    }

    @Override
    public void render(DrawContext context, float tickDelta, int screenWidth, int screenHeight) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null || client.world == null) return;

        TextRenderer textRenderer = client.textRenderer;
        List<String> lines = new ArrayList<>();

        // FPS
        lines.add("FPS: " + client.getCurrentFps());

        // Position
        lines.add(String.format("XYZ: %.1f / %.1f / %.1f", player.getX(), player.getY(), player.getZ()));

        // Block position
        BlockPos blockPos = player.getBlockPos();
        lines.add(String.format("Block: %d %d %d", blockPos.getX(), blockPos.getY(), blockPos.getZ()));

        // Chunk
        int chunkX = blockPos.getX() >> 4;
        int chunkZ = blockPos.getZ() >> 4;
        lines.add(String.format("Chunk: %d %d", chunkX, chunkZ));

        // Facing direction
        String facing = getFacingDirection(player.getYaw());
        lines.add("Facing: " + facing + String.format(" (%.1f)", player.getYaw()));

        // Biome
        RegistryEntry<Biome> biomeEntry = client.world.getBiome(blockPos);
        String biomeName = biomeEntry.getKey()
                .map(key -> key.getValue().getPath())
                .orElse("unknown");
        lines.add("Biome: " + biomeName);

        // Light level
        int blockLight = client.world.getLightLevel(LightType.BLOCK, blockPos);
        int skyLight = client.world.getLightLevel(LightType.SKY, blockPos);
        lines.add(String.format("Light: %d (block: %d, sky: %d)", Math.max(blockLight, skyLight), blockLight, skyLight));

        // Draw background
        int lineHeight = textRenderer.fontHeight + 2;
        int totalHeight = lines.size() * lineHeight + 6;
        int maxWidth = 4;
        for (String line : lines) {
            maxWidth = Math.max(maxWidth, textRenderer.getWidth(line) + 8);
        }

        if (showBackground()) {
            RenderUtils.drawRect(context, 0, 0, Math.max(getWidth(), maxWidth), Math.max(getHeight(), totalHeight), 0x80000000);
        }

        // Draw text
        int yPos = 3;
        for (String line : lines) {
            context.drawText(textRenderer, line, 4, yPos, 0xFFFFFFFF, true);
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
