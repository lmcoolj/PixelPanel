# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build the mod (produces build/libs/pixelpanel-<version>.jar)
JAVA_HOME="C:/Users/Larus/AppData/Local/Programs/Microsoft/jdk-25.0.2.10-hotspot" ./gradlew build

# Clean and rebuild
JAVA_HOME="C:/Users/Larus/AppData/Local/Programs/Microsoft/jdk-25.0.2.10-hotspot" ./gradlew clean build
```

Requires Java 25. No test suite exists. No linter configured.

## Target Platform

- Minecraft 26.2 (unobfuscated — no Yarn/Intermediary mappings, uses Mojang official names directly)
- Fabric Loader 0.19.3, Fabric API 0.152.2+26.2
- Fabric Loom 1.17-SNAPSHOT (Gradle 9.5.1 wrapper)
- Client-side only mod

## Architecture

PixelPanel is a customizable HUD mod. Players add/move/resize/configure information overlays via an in-game editor (H key).

### Core Flow

`PixelPanelClient` (entry point) creates a `PanelElementRegistry` and `ConfigManager`, registers a `HudRenderer` with Fabric's HUD system, and listens for the editor keybind.

**Rendering**: `HudRenderer` implements Fabric's `HudElement` interface. Each frame it iterates the registry, applies matrix transforms (translate + scale), and calls `element.render()` on each visible `PanelElement`. Skips rendering when the edit screen is open.

**Editing**: `HudEditScreen` extends `Screen` and provides drag-to-move, corner-resize handles, scroll-to-scale, and a toolbar. `ElementPickerWidget` is a sidebar for adding new elements.

**Config**: `ConfigManager` serializes element state to `<config-dir>/pixelpanel.json` via Gson. Uses a dirty flag with 500ms debounced saving. `PanelElementConfig` is the DTO.

### Key Abstractions

- **`PanelElement`** — abstract base class. Anchor-based positioning (0.0–1.0 normalized coords for resolution independence). Subclasses implement `render()`, `getDefaultWidth/Height()`, `getDisplayName()`. Supports min/max size constraints and optional resizability.
- **`PanelElementType`** — enum factory. Each variant has a translation key, `Supplier<PanelElement>` factory, and `allowMultiple` flag.
- **`PanelElementRegistry`** — list-based container for active elements with add/remove/query by ID or type.

### Minecraft 26.2 API Notes

These are non-obvious API names that differ from older Minecraft versions:

- The current screen moved off `Minecraft` into the reorganized `Gui` class (26.2): get it with `client.gui.screen()` (not the old `client.screen` field) and set it with `client.gui.setScreen(...)` (not `client.setScreen(...)`). The in-game HUD is now a separate `Hud` reachable via `client.gui.hud`.
- Rendering context is `GuiGraphicsExtractor` (not `GuiGraphics` or `DrawContext`)
- Screen override is `extractRenderState()` (not `render()`)
- Mouse events use `MouseButtonEvent` record: `mouseClicked(MouseButtonEvent, boolean)`, `mouseDragged(MouseButtonEvent, double, double)`, `mouseReleased(MouseButtonEvent)`
- `HudElement`/`HudElementRegistry` in `net.fabricmc.fabric.api.client.rendering.v1.hud` (replaces old `HudRenderCallback`)
- `KeyMappingHelper` in `net.fabricmc.fabric.api.client.keymapping.v1` (not `KeyBindingHelper`)
- `Identifier.fromNamespaceAndPath()` (not `Identifier.of()` or `new ResourceLocation()`)
- `ResourceKey.identifier()` (not `location()`)
- `Level.getOverworldClockTime()` (not `getDayTime()`)
- `Level.getMinY()` (not `getMinBuildHeight()`)
- `HudRenderer` uses `DeltaTracker` (not `RenderTickCounter`)

### Adding a New HUD Element

1. Create a class in `hud/elements/` extending `PanelElement`
2. Add a variant to `PanelElementType` enum with its factory supplier
3. Add translation keys in `assets/pixelpanel/lang/en_us.json`
