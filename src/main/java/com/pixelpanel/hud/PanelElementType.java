package com.pixelpanel.hud;

import com.pixelpanel.hud.elements.*;

import java.util.function.Supplier;

public enum PanelElementType {
    COORDINATES("pixelpanel.element.coordinates", CoordinatesElement::new, false),
    FPS_COUNTER("pixelpanel.element.fps_counter", FpsCounterElement::new, false),
    TOOL_DURABILITY("pixelpanel.element.tool_durability", ToolDurabilityElement::new, false),
    ARMOR_DURABILITY("pixelpanel.element.armor_durability", ArmorDurabilityElement::new, false),
    COMPASS("pixelpanel.element.compass", CompassElement::new, false),
    TIME_OF_DAY("pixelpanel.element.time_of_day", TimeOfDayElement::new, false),
    DEBUG_INFO("pixelpanel.element.debug_info", DebugInfoElement::new, false),
    MINIMAP("pixelpanel.element.minimap", MinimapElement::new, false);

    private final String translationKey;
    private final Supplier<PanelElement> factory;
    private final boolean allowMultiple;

    PanelElementType(String translationKey, Supplier<PanelElement> factory, boolean allowMultiple) {
        this.translationKey = translationKey;
        this.factory = factory;
        this.allowMultiple = allowMultiple;
    }

    public String getTranslationKey() { return translationKey; }
    public PanelElement create() { return factory.get(); }
    public boolean isAllowMultiple() { return allowMultiple; }
}
