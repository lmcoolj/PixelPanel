package com.pixelpanel.util;

import net.minecraft.client.gui.DrawContext;

public class RenderUtils {

    public static void drawRect(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + height, color);
    }

    public static void drawRectOutline(DrawContext context, int x, int y, int width, int height, int color) {
        // Top
        context.fill(x, y, x + width, y + 1, color);
        // Bottom
        context.fill(x, y + height - 1, x + width, y + height, color);
        // Left
        context.fill(x, y, x + 1, y + height, color);
        // Right
        context.fill(x + width - 1, y, x + width, y + height, color);
    }

    public static void drawDashedRectOutline(DrawContext context, int x, int y, int width, int height, int color, int dashLength) {
        // Top edge
        for (int i = 0; i < width; i += dashLength * 2) {
            int end = Math.min(i + dashLength, width);
            context.fill(x + i, y, x + end, y + 1, color);
        }
        // Bottom edge
        for (int i = 0; i < width; i += dashLength * 2) {
            int end = Math.min(i + dashLength, width);
            context.fill(x + i, y + height - 1, x + end, y + height, color);
        }
        // Left edge
        for (int i = 0; i < height; i += dashLength * 2) {
            int end = Math.min(i + dashLength, height);
            context.fill(x, y + i, x + 1, y + end, color);
        }
        // Right edge
        for (int i = 0; i < height; i += dashLength * 2) {
            int end = Math.min(i + dashLength, height);
            context.fill(x + width - 1, y + i, x + width, y + end, color);
        }
    }

    public static int lerpColor(int color1, int color2, float t) {
        t = Math.max(0, Math.min(1, t));
        int a1 = (color1 >> 24) & 0xFF, r1 = (color1 >> 16) & 0xFF, g1 = (color1 >> 8) & 0xFF, b1 = color1 & 0xFF;
        int a2 = (color2 >> 24) & 0xFF, r2 = (color2 >> 16) & 0xFF, g2 = (color2 >> 8) & 0xFF, b2 = color2 & 0xFF;
        int a = (int)(a1 + (a2 - a1) * t);
        int r = (int)(r1 + (r2 - r1) * t);
        int g = (int)(g1 + (g2 - g1) * t);
        int b = (int)(b1 + (b2 - b1) * t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static int getDurabilityColor(float ratio) {
        if (ratio > 0.6f) {
            return 0xFF55FF55; // Green
        } else if (ratio > 0.3f) {
            return 0xFFFFFF55; // Yellow
        } else {
            return 0xFFFF5555; // Red
        }
    }
}
