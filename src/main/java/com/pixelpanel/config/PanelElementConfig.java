package com.pixelpanel.config;

import com.pixelpanel.hud.PanelElementType;

import java.util.HashMap;
import java.util.Map;

public class PanelElementConfig {
    public String id;
    public PanelElementType type;
    public float anchorX;
    public float anchorY;
    public int width;
    public int height;
    public float scale;
    public boolean visible;
    public boolean showBackground;
    public Map<String, Object> extraSettings;

    public PanelElementConfig() {
        this.extraSettings = new HashMap<>();
        this.scale = 1.0f;
        this.visible = true;
        this.showBackground = true;
    }
}
