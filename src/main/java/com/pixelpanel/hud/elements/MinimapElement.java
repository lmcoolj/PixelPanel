package com.pixelpanel.hud.elements;

import com.mojang.blaze3d.platform.NativeImage;
import com.pixelpanel.hud.PanelElement;
import com.pixelpanel.hud.PanelElementType;
import com.pixelpanel.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.MapColor;

public class MinimapElement extends PanelElement {

    private static final int MAP_SIZE = 128;
    private static final float BRIGHTNESS_SCALE = 0.72f;
    // Force a texture rebuild when the player moves this many blocks from center.
    // This keeps the texture fresh when flying fast, while allowing smooth
    // sub-block scrolling for small movements via the translate offset.
    private static final int REBUILD_DISTANCE = 4;

    private NativeImage mapImage;
    private DynamicTexture mapTexture;
    private Identifier textureId;
    private boolean textureDirty = false;

    // The world-space block coordinate the texture is currently centered on.
    // The smooth offset is computed relative to this, so there is never a
    // discontinuity when the texture is rebuilt — the translate changes by
    // exactly the amount the texture center shifts.
    private double textureCenterX = Double.NaN;
    private double textureCenterZ = Double.NaN;

    public MinimapElement() {
        super(PanelElementType.MINIMAP);
        setWidth(getDefaultWidth());
        setHeight(getDefaultHeight());
    }

    private void ensureTextureInitialized() {
        if (mapImage == null) {
            mapImage = new NativeImage(NativeImage.Format.RGBA, MAP_SIZE, MAP_SIZE, false);
            for (int x = 0; x < MAP_SIZE; x++) {
                for (int z = 0; z < MAP_SIZE; z++) {
                    mapImage.setPixelABGR(x, z, toABGR(0x12, 0x12, 0x20));
                }
            }
            mapTexture = new DynamicTexture(() -> "pixelpanel_minimap_" + getId(), mapImage);
            textureId = Identifier.fromNamespaceAndPath("pixelpanel", "minimap_" + getId().replace("-", ""));
            Minecraft.getInstance().getTextureManager().register(textureId, mapTexture);
            textureDirty = true;
        }
    }

    @Override
    public void render(GuiGraphicsExtractor context, float tickDelta, int screenWidth, int screenHeight) {
        Minecraft client = Minecraft.getInstance();
        Player player = client.player;
        if (player == null || client.level == null) return;

        ensureTextureInitialized();

        Font font = client.font;
        int size = Math.min(getWidth(), getHeight());
        float pixelsPerBlock = (float)(size - 4) / MAP_SIZE;

        double playerX = player.getX();
        double playerZ = player.getZ();

        // Decide whether to rebuild the texture.
        // Rebuild when: first time, OR player has moved REBUILD_DISTANCE blocks
        // from the current texture center.
        boolean needsRebuild = Double.isNaN(textureCenterX);
        if (!needsRebuild) {
            double dx = playerX - textureCenterX;
            double dz = playerZ - textureCenterZ;
            if (dx * dx + dz * dz >= REBUILD_DISTANCE * REBUILD_DISTANCE) {
                needsRebuild = true;
            }
        }

        if (needsRebuild) {
            int centerBlockX = (int) Math.floor(playerX);
            int centerBlockZ = (int) Math.floor(playerZ);
            updateMapImage(client.level, centerBlockX, centerBlockZ);
            textureCenterX = centerBlockX;
            textureCenterZ = centerBlockZ;
        }

        if (textureDirty) {
            mapTexture.upload();
            textureDirty = false;
        }

        // Smooth offset: how far the player is from the texture center,
        // converted to screen pixels. This is continuous — no discontinuity
        // when the texture rebuilds because the center shift cancels out.
        float smoothOffsetX = (float)(-(playerX - textureCenterX) * pixelsPerBlock);
        float smoothOffsetZ = (float)(-(playerZ - textureCenterZ) * pixelsPerBlock);

        // Border + background
        RenderUtils.drawRect(context, 0, 0, size, size, 0xFF000000);
        RenderUtils.drawRect(context, 1, 1, size - 2, size - 2, 0xFF121220);

        // Render map texture with smooth offset
        int drawOffset = 2;
        int drawSize = size - 4;

        context.pose().pushMatrix();
        context.pose().translate(smoothOffsetX, smoothOffsetZ);
        context.blit(RenderPipelines.GUI_TEXTURED, textureId,
                drawOffset, drawOffset, 0.0f, 0.0f,
                drawSize, drawSize, drawSize, drawSize);
        context.pose().popMatrix();

        // Re-draw border to clip scroll overflow
        context.fill(0, 0, size, 2, 0xFF000000);
        context.fill(0, size - 2, size, size, 0xFF000000);
        context.fill(0, 0, 2, size, 0xFF000000);
        context.fill(size - 2, 0, size, size, 0xFF000000);

        // Player head at center
        int centerX = size / 2;
        int centerY = size / 2;
        int headSize = Math.max(6, size / 14);

        Identifier skinTexture = getPlayerSkinTexture(client, player);
        if (skinTexture != null) {
            // Head UV (8,8)-(16,16) on 64x64 skin
            context.blit(RenderPipelines.GUI_TEXTURED, skinTexture,
                    centerX - headSize / 2, centerY - headSize / 2,
                    8.0f, 8.0f,
                    headSize, headSize, 64, 64);
            // Hat overlay UV (40,8)-(48,16)
            context.blit(RenderPipelines.GUI_TEXTURED, skinTexture,
                    centerX - headSize / 2, centerY - headSize / 2,
                    40.0f, 8.0f,
                    headSize, headSize, 64, 64);
        } else {
            context.fill(centerX - 2, centerY - 2, centerX + 2, centerY + 2, 0xFFFFFFFF);
            context.fill(centerX - 1, centerY - 1, centerX + 1, centerY + 1, 0xFFFF0000);
        }

        // Cardinal labels
        context.text(font, "N", centerX - 2, 3, 0xFFFFFFFF, true);
        context.text(font, "S", centerX - 2, size - font.lineHeight - 2, 0xFFFFFFFF, true);
        context.text(font, "W", 3, centerY - font.lineHeight / 2, 0xFFFFFFFF, true);
        context.text(font, "E", size - font.width("E") - 3, centerY - font.lineHeight / 2, 0xFFFFFFFF, true);

        RenderUtils.drawRectOutline(context, 0, 0, size, size, 0xFF444444);
    }

    private Identifier getPlayerSkinTexture(Minecraft client, Player player) {
        try {
            ClientPacketListener connection = client.getConnection();
            if (connection == null) return null;
            PlayerInfo info = connection.getPlayerInfo(player.getUUID());
            if (info == null) return null;
            PlayerSkin skin = info.getSkin();
            if (skin == null || skin.body() == null) return null;
            return skin.body().texturePath();
        } catch (Exception e) {
            return null;
        }
    }

    private void updateMapImage(Level world, int centerX, int centerZ) {
        int halfMap = MAP_SIZE / 2;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        for (int dx = 0; dx < MAP_SIZE; dx++) {
            for (int dz = 0; dz < MAP_SIZE; dz++) {
                int worldX = centerX - halfMap + dx;
                int worldZ = centerZ - halfMap + dz;

                int topY = world.getHeight(Heightmap.Types.MOTION_BLOCKING, worldX, worldZ) - 1;

                if (topY < world.getMinY()) {
                    mapImage.setPixelABGR(dx, dz, toABGR(0x12, 0x12, 0x20));
                    continue;
                }

                pos.set(worldX, topY, worldZ);
                BlockState state = world.getBlockState(pos);
                MapColor mapColor = state.getMapColor(world, pos);

                if (mapColor == MapColor.NONE) {
                    mapImage.setPixelABGR(dx, dz, toABGR(0x12, 0x12, 0x20));
                    continue;
                }

                int col = mapColor.col;
                int r = (col >> 16) & 0xFF;
                int g = (col >> 8) & 0xFF;
                int b = col & 0xFF;

                // Reduce brightness
                r = (int)(r * BRIGHTNESS_SCALE);
                g = (int)(g * BRIGHTNESS_SCALE);
                b = (int)(b * BRIGHTNESS_SCALE);

                // Depth shading
                int neighborY = world.getHeight(Heightmap.Types.MOTION_BLOCKING, worldX, worldZ - 1) - 1;
                if (neighborY > topY) {
                    r = r * 170 / 255;
                    g = g * 170 / 255;
                    b = b * 170 / 255;
                } else if (neighborY < topY) {
                    r = Math.min(255, r * 240 / 200);
                    g = Math.min(255, g * 240 / 200);
                    b = Math.min(255, b * 240 / 200);
                }

                mapImage.setPixelABGR(dx, dz, toABGR(r, g, b));
            }
        }
        textureDirty = true;
    }

    private static int toABGR(int r, int g, int b) {
        return 0xFF000000 | (b << 16) | (g << 8) | r;
    }

    @Override
    public void close() {
        if (mapTexture != null) {
            mapTexture.close();
            mapTexture = null;
            mapImage = null;
        }
        if (textureId != null) {
            Minecraft.getInstance().getTextureManager().release(textureId);
            textureId = null;
        }
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
