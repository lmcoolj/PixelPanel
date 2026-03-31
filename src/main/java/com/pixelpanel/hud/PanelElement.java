package com.pixelpanel.hud;

import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class PanelElement {
    private final String id;
    private final PanelElementType type;
    private float anchorX;
    private float anchorY;
    private int width;
    private int height;
    private float scale;
    private boolean visible;
    private boolean showBackground;
    private final Map<String, Object> extraSettings;

    public PanelElement(PanelElementType type) {
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

    public abstract void render(GuiGraphicsExtractor context, float tickDelta, int screenWidth, int screenHeight);
    public abstract int getDefaultWidth();
    public abstract int getDefaultHeight();
    public abstract String getDisplayName();

    public boolean isResizable() { return true; }
    public int getMinWidth() { return getDefaultWidth(); }
    public int getMinHeight() { return getDefaultHeight(); }
    public int getMaxWidth() { return 0; }
    public int getMaxHeight() { return 0; }

    public int getScreenX(int screenWidth) {
        int x = (int) (anchorX * screenWidth);
        return Math.max(0, Math.min(x, screenWidth - (int)(width * scale)));
    }

    public int getScreenY(int screenHeight) {
        int y = (int) (anchorY * screenHeight);
        return Math.max(0, Math.min(y, screenHeight - (int)(height * scale)));
    }

    public int getScaledWidth() { return (int) (width * scale); }
    public int getScaledHeight() { return (int) (height * scale); }

    public boolean containsPoint(int mouseX, int mouseY, int screenWidth, int screenHeight) {
        int x = getScreenX(screenWidth);
        int y = getScreenY(screenHeight);
        return mouseX >= x && mouseX <= x + getScaledWidth()
                && mouseY >= y && mouseY <= y + getScaledHeight();
    }

    public String getId() { return id; }
    public PanelElementType getType() { return type; }
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

    /** Override in subclasses that hold GPU resources (textures, etc.) to release them. */
    public void close() {}
}
