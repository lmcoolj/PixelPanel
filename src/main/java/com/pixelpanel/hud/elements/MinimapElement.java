package com.pixelpanel.hud.elements;

import com.pixelpanel.hud.PanelElement;
import com.pixelpanel.hud.PanelElementType;
import com.pixelpanel.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.MapColor;

public class MinimapElement extends PanelElement {

    private int[][] colorCache;
    private long lastUpdateTick = 0;
    private static final int UPDATE_INTERVAL = 20;
    private static final int MAP_RADIUS = 32;

    public MinimapElement() {
        super(PanelElementType.MINIMAP);
        setWidth(getDefaultWidth());
        setHeight(getDefaultHeight());
    }

    @Override
    public void render(GuiGraphicsExtractor context, float tickDelta, int screenWidth, int screenHeight) {
        Minecraft client = Minecraft.getInstance();
        Player player = client.player;
        if (player == null || client.level == null) return;

        Font font = client.font;
        int size = Math.min(getWidth(), getHeight());

        RenderUtils.drawRect(context, 0, 0, size, size, 0xFF000000);
        RenderUtils.drawRect(context, 1, 1, size - 2, size - 2, 0xFF1a1a2e);

        long currentTick = client.level.getGameTime();
        if (colorCache == null || currentTick - lastUpdateTick >= UPDATE_INTERVAL) {
            updateColorCache(client.level, player);
            lastUpdateTick = currentTick;
        }

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

        int centerX = size / 2;
        int centerY = size / 2;
        context.fill(centerX - 2, centerY - 2, centerX + 2, centerY + 2, 0xFFFFFFFF);
        context.fill(centerX - 1, centerY - 1, centerX + 1, centerY + 1, 0xFFFF0000);

        context.text(font, "N", centerX - 2, 3, 0xFFFFFFFF, true);
        context.text(font, "S", centerX - 2, size - font.lineHeight - 2, 0xFFFFFFFF, true);
        context.text(font, "W", 3, centerY - font.lineHeight / 2, 0xFFFFFFFF, true);
        context.text(font, "E", size - font.width("E") - 3, centerY - font.lineHeight / 2, 0xFFFFFFFF, true);

        RenderUtils.drawRectOutline(context, 0, 0, size, size, 0xFF555555);
    }

    private void updateColorCache(Level world, Player player) {
        int mapSize = MAP_RADIUS * 2;
        colorCache = new int[mapSize][mapSize];

        int playerX = (int) player.getX();
        int playerZ = (int) player.getZ();

        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int dx = -MAP_RADIUS; dx < MAP_RADIUS; dx++) {
            for (int dz = -MAP_RADIUS; dz < MAP_RADIUS; dz++) {
                int worldX = playerX + dx;
                int worldZ = playerZ + dz;

                int topY = world.getHeight(Heightmap.Types.MOTION_BLOCKING, worldX, worldZ) - 1;
                if (topY < world.getMinY()) continue;

                pos.set(worldX, topY, worldZ);
                BlockState state = world.getBlockState(pos);
                MapColor mapColor = state.getMapColor(world, pos);
                if (mapColor != MapColor.NONE) {
                    colorCache[dx + MAP_RADIUS][dz + MAP_RADIUS] = mapColorToArgb(mapColor);
                }
            }
        }
    }

    private int mapColorToArgb(MapColor mapColor) {
        int color = mapColor.col;
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
