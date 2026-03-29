package com.pixelpanel.hud.elements;

import com.pixelpanel.hud.HudElement;
import com.pixelpanel.hud.HudElementType;
import com.pixelpanel.util.RenderUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

public class MinimapElement extends HudElement {

    private int[][] colorCache;
    private long lastUpdateTick = 0;
    private static final int UPDATE_INTERVAL = 20; // Update every 20 ticks (1 second)
    private static final int MAP_RADIUS = 32;

    public MinimapElement() {
        super(HudElementType.MINIMAP);
        setWidth(getDefaultWidth());
        setHeight(getDefaultHeight());
    }

    @Override
    public void render(DrawContext context, float tickDelta, int screenWidth, int screenHeight) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;
        if (player == null || client.world == null) return;

        TextRenderer textRenderer = client.textRenderer;
        int size = Math.min(getWidth(), getHeight());

        // Background/border
        RenderUtils.drawRect(context, 0, 0, size, size, 0xFF000000);
        RenderUtils.drawRect(context, 1, 1, size - 2, size - 2, 0xFF1a1a2e);

        // Update color cache periodically
        long currentTick = client.world.getTime();
        if (colorCache == null || currentTick - lastUpdateTick >= UPDATE_INTERVAL) {
            updateColorCache(client.world, player);
            lastUpdateTick = currentTick;
        }

        // Draw minimap pixels
        if (colorCache != null) {
            int mapSize = MAP_RADIUS * 2;
            float pixelSize = (float)(size - 4) / mapSize;

            for (int mx = 0; mx < mapSize; mx++) {
                for (int mz = 0; mz < mapSize; mz++) {
                    int color = colorCache[mx][mz];
                    if (color == 0) continue;

                    int drawX = 2 + (int)(mx * pixelSize);
                    int drawY = 2 + (int)(mz * pixelSize);
                    int drawW = Math.max(1, (int)pixelSize);
                    int drawH = Math.max(1, (int)pixelSize);

                    context.fill(drawX, drawY, drawX + drawW, drawY + drawH, color);
                }
            }
        }

        // Player dot in center
        int centerX = size / 2;
        int centerY = size / 2;
        context.fill(centerX - 2, centerY - 2, centerX + 2, centerY + 2, 0xFFFFFFFF);
        context.fill(centerX - 1, centerY - 1, centerX + 1, centerY + 1, 0xFFFF0000);

        // Cardinal direction labels
        context.drawText(textRenderer, "N", centerX - 2, 3, 0xFFFFFFFF, true);
        context.drawText(textRenderer, "S", centerX - 2, size - textRenderer.fontHeight - 2, 0xFFFFFFFF, true);
        context.drawText(textRenderer, "W", 3, centerY - textRenderer.fontHeight / 2, 0xFFFFFFFF, true);
        context.drawText(textRenderer, "E", size - textRenderer.getWidth("E") - 3, centerY - textRenderer.fontHeight / 2, 0xFFFFFFFF, true);

        // Border
        RenderUtils.drawRectOutline(context, 0, 0, size, size, 0xFF555555);
    }

    private void updateColorCache(World world, PlayerEntity player) {
        int mapSize = MAP_RADIUS * 2;
        colorCache = new int[mapSize][mapSize];

        int playerX = (int) player.getX();
        int playerZ = (int) player.getZ();

        BlockPos.Mutable pos = new BlockPos.Mutable();

        for (int dx = -MAP_RADIUS; dx < MAP_RADIUS; dx++) {
            for (int dz = -MAP_RADIUS; dz < MAP_RADIUS; dz++) {
                int worldX = playerX + dx;
                int worldZ = playerZ + dz;

                // Use heightmap for O(1) top-block lookup instead of scanning Y column
                int topY = world.getTopY(Heightmap.Type.MOTION_BLOCKING, worldX, worldZ) - 1;
                if (topY < world.getBottomY()) continue;

                pos.set(worldX, topY, worldZ);
                BlockState state = world.getBlockState(pos);
                MapColor mapColor = state.getMapColor(world, pos);
                if (mapColor != MapColor.CLEAR) {
                    colorCache[dx + MAP_RADIUS][dz + MAP_RADIUS] = mapColorToArgb(mapColor);
                }
            }
        }
    }

    private int mapColorToArgb(MapColor mapColor) {
        int color = mapColor.color;
        // MapColor.color is RGB, we need ARGB
        return 0xFF000000 | color;
    }

    @Override
    public int getDefaultWidth() { return 100; }

    @Override
    public int getDefaultHeight() { return 100; }

    @Override
    public String getDisplayName() { return "Minimap"; }

    @Override
    public boolean isResizable() { return true; }

    @Override
    public int getMinWidth() { return 60; }

    @Override
    public int getMinHeight() { return 60; }

    @Override
    public int getMaxWidth() { return 250; }

    @Override
    public int getMaxHeight() { return 250; }
}
