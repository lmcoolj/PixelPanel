package com.pixelpanel.hud;

import net.minecraft.client.gui.DrawContext;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class HudElement {
    private final String id;
    private final HudElementType type;
    private float anchorX;
    private float anchorY;
    private int width;
    private int height;
    private float scale;
    private boolean visible;
    private boolean showBackground;
    private final Map<String, Object> extraSettings;

    public HudElement(HudElementType type) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
        this.anchorX = 0.01f;
        this.anchorY = 0.01f;
        this.width = getDefaultWidth();
        this.height = getDefaultHeight();
        this.scale = 1.0f;
        this.visible = true;
        this.showBackground = true;
        this.extraSettings = new HashMap<>();
    }

    public abstract void render(DrawContext context, float tickDelta, int screenWidth, int screenHeight);

    public abstract int getDefaultWidth();

    public abstract int getDefaultHeight();

    public abstract String getDisplayName();

    public boolean isResizable() {
        return true;
    }

    /** Minimum width this element can be resized to (defaults to default width). */
    public int getMinWidth() { return getDefaultWidth(); }

    /** Minimum height this element can be resized to (defaults to default height). */
    public int getMinHeight() { return getDefaultHeight(); }

    /** Maximum width this element can be resized to (0 = unlimited). */
    public int getMaxWidth() { return 0; }

    /** Maximum height this element can be resized to (0 = unlimited). */
    public int getMaxHeight() { return 0; }

    public int getScreenX(int screenWidth) {
        int x = (int) (anchorX * screenWidth);
        return Math.max(0, Math.min(x, screenWidth - (int)(width * scale)));
    }

    public int getScreenY(int screenHeight) {
        int y = (int) (anchorY * screenHeight);
        return Math.max(0, Math.min(y, screenHeight - (int)(height * scale)));
    }

    public int getScaledWidth() {
        return (int) (width * scale);
    }

    public int getScaledHeight() {
        return (int) (height * scale);
    }

    public boolean containsPoint(int mouseX, int mouseY, int screenWidth, int screenHeight) {
        int x = getScreenX(screenWidth);
        int y = getScreenY(screenHeight);
        return mouseX >= x && mouseX <= x + getScaledWidth()
                && mouseY >= y && mouseY <= y + getScaledHeight();
    }

    // Getters and setters
    public String getId() { return id; }
    public HudElementType getType() { return type; }

    public float getAnchorX() { return anchorX; }
    public void setAnchorX(float anchorX) { this.anchorX = anchorX; }

    public float getAnchorY() { return anchorY; }
    public void setAnchorY(float anchorY) { this.anchorY = anchorY; }

    public int getWidth() { return width; }
    public void setWidth(int width) {
        int min = Math.max(16, getMinWidth());
        int max = getMaxWidth();
        this.width = max > 0 ? Math.max(min, Math.min(max, width)) : Math.max(min, width);
    }

    public int getHeight() { return height; }
    public void setHeight(int height) {
        int min = Math.max(16, getMinHeight());
        int max = getMaxHeight();
        this.height = max > 0 ? Math.max(min, Math.min(max, height)) : Math.max(min, height);
    }

    public float getScale() { return scale; }
    public void setScale(float scale) { this.scale = Math.max(0.25f, Math.min(4.0f, scale)); }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public boolean showBackground() { return showBackground; }
    public void setShowBackground(boolean showBackground) { this.showBackground = showBackground; }

    public Map<String, Object> getExtraSettings() { return extraSettings; }
}
