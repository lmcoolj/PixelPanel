package com.pixelpanel.hud.elements;

import com.pixelpanel.hud.PanelElement;
import com.pixelpanel.hud.PanelElementType;
import com.pixelpanel.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;

public class PingElement extends PanelElement {

    public PingElement() {
        super(PanelElementType.PING);
    }

    @Override
    public void render(GuiGraphicsExtractor context, float tickDelta, int screenWidth, int screenHeight) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        Font font = client.font;

        if (showBackground()) {
            RenderUtils.drawRect(context, 0, 0, getWidth(), getHeight(), 0x80000000);
        }

        ClientPacketListener connection = client.getConnection();
        if (connection == null || client.getCurrentServer() == null) {
            context.text(font, "Singleplayer", 4, 4, 0xFF888888, true);
            return;
        }

        PlayerInfo playerInfo = connection.getPlayerInfo(client.player.getUUID());
        if (playerInfo == null) {
            context.text(font, "-- ms", 4, 4, 0xFF888888, true);
            return;
        }

        int ping = playerInfo.getLatency();
        int color;
        if (ping < 50) {
            color = 0xFF55FF55; // Green
        } else if (ping < 100) {
            color = 0xFFFFFF55; // Yellow
        } else if (ping < 200) {
            color = 0xFFFFAA00; // Orange
        } else {
            color = 0xFFFF5555; // Red
        }

        String bars;
        if (ping < 50) bars = "\u2581\u2582\u2583\u2584";
        else if (ping < 100) bars = "\u2581\u2582\u2583";
        else if (ping < 200) bars = "\u2581\u2582";
        else bars = "\u2581";

        context.text(font, bars + " " + ping + "ms", 4, 4, color, true);
    }

    @Override
    public int getDefaultWidth() { return 70; }
    @Override
    public int getDefaultHeight() { return 18; }
    @Override
    public String getDisplayName() { return "Ping"; }
    @Override
    public boolean isResizable() { return false; }
}
