package com.pixelpanel.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pixelpanel.hud.PanelElement;
import com.pixelpanel.hud.PanelElementRegistry;
import com.pixelpanel.hud.PanelElementType;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("PixelPanel");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final PanelElementRegistry registry;
    private final Path configPath;
    private long lastSaveTime = 0;
    private boolean dirty = false;

    public ConfigManager(PanelElementRegistry registry) {
        this.registry = registry;
        this.configPath = FabricLoader.getInstance().getConfigDir().resolve("pixelpanel.json");
    }

    public void load() {
        if (!Files.exists(configPath)) {
            createDefaults();
            save();
            return;
        }

        try {
            String json = Files.readString(configPath);
            PixelPanelConfig config = GSON.fromJson(json, PixelPanelConfig.class);

            if (config == null || config.elements == null) {
                createDefaults();
                save();
                return;
            }

            registry.clear();
            for (PanelElementConfig elementConfig : config.elements) {
                try {
                    PanelElement element = elementConfig.type.create();
                    applyConfig(element, elementConfig);
                    registry.add(element);
                } catch (Exception e) {
                    LOGGER.warn("Failed to load HUD element: {}", elementConfig.type, e);
                }
            }

            LOGGER.info("Loaded {} HUD elements from config", registry.getAll().size());
        } catch (IOException e) {
            LOGGER.error("Failed to read PixelPanel config", e);
            createDefaults();
        }
    }

    public void save() {
        PixelPanelConfig config = new PixelPanelConfig();

        for (PanelElement element : registry.getAll()) {
            PanelElementConfig ec = new PanelElementConfig();
            ec.id = element.getId();
            ec.type = element.getType();
            ec.anchorX = element.getAnchorX();
            ec.anchorY = element.getAnchorY();
            ec.width = element.getWidth();
            ec.height = element.getHeight();
            ec.scale = element.getScale();
            ec.visible = element.isVisible();
            ec.showBackground = element.showBackground();
            ec.extraSettings = element.getExtraSettings();
            config.elements.add(ec);
        }

        try {
            Files.createDirectories(configPath.getParent());
            Files.writeString(configPath, GSON.toJson(config));
            lastSaveTime = System.currentTimeMillis();
            dirty = false;
        } catch (IOException e) {
            LOGGER.error("Failed to save PixelPanel config", e);
        }
    }

    public void markDirty() { dirty = true; }

    public void saveIfDirty() {
        if (dirty && System.currentTimeMillis() - lastSaveTime > 500) {
            save();
        }
    }

    private void createDefaults() {
        registry.clear();

        PanelElement coords = PanelElementType.COORDINATES.create();
        coords.setAnchorX(0.01f);
        coords.setAnchorY(0.0f);
        registry.add(coords);

        PanelElement fps = PanelElementType.FPS_COUNTER.create();
        fps.setAnchorX(0.88f);
        fps.setAnchorY(0.0f);
        registry.add(fps);

        PanelElement compass = PanelElementType.COMPASS.create();
        compass.setAnchorX(0.42f);
        compass.setAnchorY(0.0f);
        registry.add(compass);
    }

    private void applyConfig(PanelElement element, PanelElementConfig config) {
        element.setAnchorX(config.anchorX);
        element.setAnchorY(config.anchorY);
        element.setWidth(config.width);
        element.setHeight(config.height);
        element.setScale(config.scale);
        element.setVisible(config.visible);
        element.setShowBackground(config.showBackground);
        if (config.extraSettings != null) {
            element.getExtraSettings().putAll(config.extraSettings);
        }
    }
}
