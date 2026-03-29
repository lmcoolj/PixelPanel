package com.pixelpanel.config;

import com.pixelpanel.hud.HudElementType;

import java.util.HashMap;
import java.util.Map;

public class HudElementConfig {
    public String id;
    public HudElementType type;
    public float anchorX;
    public float anchorY;
    public int width;
    public int height;
    public float scale;
    public boolean visible;
    public boolean showBackground;
    public Map<String, Object> extraSettings;

    public HudElementConfig() {
        this.extraSettings = new HashMap<>();
        this.scale = 1.0f;
        this.visible = true;
        this.showBackground = true;
    }
}
