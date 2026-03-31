package com.pixelpanel.gui;

import com.pixelpanel.config.ConfigManager;
import com.pixelpanel.hud.PanelElement;
import com.pixelpanel.hud.PanelElementRegistry;
import com.pixelpanel.util.RenderUtils;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class HudEditScreen extends Screen {
    private final PanelElementRegistry registry;
    private final ConfigManager configManager;
    private PanelElement selectedElement = null;
    private ElementPickerWidget pickerWidget = null;

    private enum DragState { IDLE, DRAGGING, RESIZING }
    private DragState dragState = DragState.IDLE;
    private int dragOffsetX, dragOffsetY;
    private ResizeHandle activeHandle = null;

    private enum ResizeHandle { TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT }
    private static final int HANDLE_SIZE = 6;
    private static final int TOOLBAR_HEIGHT = 32;
    private static final int BOTTOM_BAR_HEIGHT = 18;

    public HudEditScreen(PanelElementRegistry registry, ConfigManager configManager) {
        super(Component.translatable("pixelpanel.editor.title"));
        this.registry = registry;
        this.configManager = configManager;
    }

    private int getEditorY(PanelElement element) {
        int gameY = element.getScreenY(height - TOOLBAR_HEIGHT - BOTTOM_BAR_HEIGHT);
        return gameY + TOOLBAR_HEIGHT;
    }

    private float editorYToAnchor(int editorY) {
        int gameY = editorY - TOOLBAR_HEIGHT;
        int usableHeight = height - TOOLBAR_HEIGHT - BOTTOM_BAR_HEIGHT;
        return Math.max(0, Math.min(1, (float) gameY / usableHeight));
    }

    @Override
    protected void init() {
        int buttonHeight = 20;
        int spacing = 4;
        int startX = 6;
        int y = 6;

        addRenderableWidget(Button.builder(Component.literal("\u2795 Add"), button -> {
            togglePicker();
        }).bounds(startX, y, 55, buttonHeight).build());

        startX += 55 + spacing;

        addRenderableWidget(Button.builder(Component.literal("\u2716 Remove"), button -> {
            if (selectedElement != null) {
                registry.remove(selectedElement.getId());
                selectedElement = null;
                configManager.save();
            }
        }).bounds(startX, y, 65, buttonHeight).build());

        startX += 65 + spacing;

        addRenderableWidget(Button.builder(Component.literal("\uD83D\uDC41 Visible"), button -> {
            if (selectedElement != null) {
                selectedElement.setVisible(!selectedElement.isVisible());
                configManager.markDirty();
            }
        }).bounds(startX, y, 65, buttonHeight).build());

        startX += 65 + spacing;

        addRenderableWidget(Button.builder(Component.translatable("pixelpanel.editor.toggle_bg"), button -> {
            if (selectedElement != null) {
                selectedElement.setShowBackground(!selectedElement.showBackground());
                configManager.markDirty();
            }
        }).bounds(startX, y, 65, buttonHeight).build());

        startX += 65 + spacing;

        addRenderableWidget(Button.builder(Component.literal("\u21BA Reset"), button -> {
            if (selectedElement != null) {
                selectedElement.setWidth(selectedElement.getDefaultWidth());
                selectedElement.setHeight(selectedElement.getDefaultHeight());
                selectedElement.setScale(1.0f);
                selectedElement.setAnchorX(0.5f - (float) selectedElement.getScaledWidth() / (2 * width));
                selectedElement.setAnchorY(0.5f - (float) selectedElement.getScaledHeight() / (2 * height));
                configManager.markDirty();
            }
        }).bounds(startX, y, 60, buttonHeight).build());

        addRenderableWidget(Button.builder(Component.literal("\u2714 Done"), button -> {
            onClose();
        }).bounds(width - 60 - 6, y, 60, buttonHeight).build());
    }

    private void togglePicker() {
        if (pickerWidget != null) {
            pickerWidget = null;
        } else {
            pickerWidget = new ElementPickerWidget(
                    4, TOOLBAR_HEIGHT + 2, 170, height - TOOLBAR_HEIGHT - BOTTOM_BAR_HEIGHT - 4,
                    registry, configManager, width);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        // Background overlay
        context.fill(0, 0, width, height, 0x40000000);

        // Toolbar — dark purple
        context.fill(0, 0, width, TOOLBAR_HEIGHT, 0xD0100818);
        context.fill(0, TOOLBAR_HEIGHT - 1, width, TOOLBAR_HEIGHT, 0xFF6633AA);

        // Bottom bar
        context.fill(0, height - BOTTOM_BAR_HEIGHT, width, height, 0xD0100818);
        context.fill(0, height - BOTTOM_BAR_HEIGHT, width, height - BOTTOM_BAR_HEIGHT + 1, 0xFF6633AA);

        for (PanelElement element : registry.getAll()) {
            int x = element.getScreenX(width);
            int y = getEditorY(element);

            context.pose().pushMatrix();
            context.pose().translate(x, y);
            context.pose().scale(element.getScale(), element.getScale());

            if (element.isVisible()) {
                element.render(context, delta, width, height);
            } else {
                RenderUtils.drawRect(context, 0, 0, element.getWidth(), element.getHeight(), 0x40444444);
            }

            context.pose().popMatrix();

            int scaledW = element.getScaledWidth();
            int scaledH = element.getScaledHeight();

            if (element == selectedElement) {
                RenderUtils.drawRectOutline(context, x - 1, y - 1, scaledW + 2, scaledH + 2, 0xFFBB88FF);

                if (element.isResizable()) {
                    drawHandle(context, x - HANDLE_SIZE / 2, y - HANDLE_SIZE / 2);
                    drawHandle(context, x + scaledW - HANDLE_SIZE / 2, y - HANDLE_SIZE / 2);
                    drawHandle(context, x - HANDLE_SIZE / 2, y + scaledH - HANDLE_SIZE / 2);
                    drawHandle(context, x + scaledW - HANDLE_SIZE / 2, y + scaledH - HANDLE_SIZE / 2);
                }

                String name = element.getDisplayName();
                context.text(font, name, x, y - 11, 0xFFBB88FF, true);
            } else {
                RenderUtils.drawDashedRectOutline(context, x - 1, y - 1, scaledW + 2, scaledH + 2, 0x60BB88FF, 4);
            }
        }

        if (pickerWidget != null) {
            pickerWidget.render(context, font, mouseX, mouseY);
        }

        super.extractRenderState(context, mouseX, mouseY, delta);

        // Bottom bar help text
        context.text(font, "Click to select  |  Drag to move  |  Corners to resize  |  Scroll to scale",
                6, height - BOTTOM_BAR_HEIGHT + 4, 0x90BB88FF, false);
    }

    private void drawHandle(GuiGraphicsExtractor context, int x, int y) {
        context.fill(x, y, x + HANDLE_SIZE, y + HANDLE_SIZE, 0xFFFFFFFF);
        context.fill(x + 1, y + 1, x + HANDLE_SIZE - 1, y + HANDLE_SIZE - 1, 0xFFBB88FF);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean isOverWidget) {
        if (super.mouseClicked(event, isOverWidget)) return true;

        int mx = (int) event.x();
        int my = (int) event.y();

        if (my < TOOLBAR_HEIGHT || my > height - BOTTOM_BAR_HEIGHT) return false;

        if (pickerWidget != null && pickerWidget.mouseClicked(mx, my)) {
            return true;
        }

        if (selectedElement != null && selectedElement.isResizable()) {
            ResizeHandle handle = getHoveredHandle(mx, my, selectedElement);
            if (handle != null) {
                dragState = DragState.RESIZING;
                activeHandle = handle;
                return true;
            }
        }

        var elements = registry.getAll();
        for (int i = elements.size() - 1; i >= 0; i--) {
            PanelElement element = elements.get(i);
            int ex = element.getScreenX(width);
            int ey = getEditorY(element);
            int ew = element.getScaledWidth();
            int eh = element.getScaledHeight();
            if (mx >= ex && mx <= ex + ew && my >= ey && my <= ey + eh) {
                selectedElement = element;
                dragState = DragState.DRAGGING;
                dragOffsetX = mx - ex;
                dragOffsetY = my - ey;
                return true;
            }
        }

        selectedElement = null;
        return false;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        if (super.mouseDragged(event, deltaX, deltaY)) return true;

        int mx = (int) event.x();
        int my = (int) event.y();

        if (selectedElement == null) return false;

        if (dragState == DragState.DRAGGING) {
            float newAnchorX = (float)(mx - dragOffsetX) / width;
            int newEditorY = Math.max(TOOLBAR_HEIGHT, Math.min(height - BOTTOM_BAR_HEIGHT - selectedElement.getScaledHeight(), my - dragOffsetY));
            float newAnchorY = editorYToAnchor(newEditorY);
            selectedElement.setAnchorX(Math.max(0, Math.min(1, newAnchorX)));
            selectedElement.setAnchorY(Math.max(0, Math.min(1, newAnchorY)));
            configManager.markDirty();
            return true;
        }

        if (dragState == DragState.RESIZING && activeHandle != null) {
            int elemX = selectedElement.getScreenX(width);
            int elemY = getEditorY(selectedElement);
            int scaledW = selectedElement.getScaledWidth();
            int scaledH = selectedElement.getScaledHeight();

            switch (activeHandle) {
                case BOTTOM_RIGHT:
                    selectedElement.setWidth((int)((mx - elemX) / selectedElement.getScale()));
                    selectedElement.setHeight((int)((my - elemY) / selectedElement.getScale()));
                    break;
                case BOTTOM_LEFT:
                    selectedElement.setWidth((int)((elemX + scaledW - mx) / selectedElement.getScale()));
                    selectedElement.setAnchorX((float) mx / width);
                    selectedElement.setHeight((int)((my - elemY) / selectedElement.getScale()));
                    break;
                case TOP_RIGHT:
                    selectedElement.setWidth((int)((mx - elemX) / selectedElement.getScale()));
                    selectedElement.setHeight((int)((elemY + scaledH - my) / selectedElement.getScale()));
                    selectedElement.setAnchorY(editorYToAnchor(my));
                    break;
                case TOP_LEFT:
                    selectedElement.setWidth((int)((elemX + scaledW - mx) / selectedElement.getScale()));
                    selectedElement.setHeight((int)((elemY + scaledH - my) / selectedElement.getScale()));
                    selectedElement.setAnchorX((float) mx / width);
                    selectedElement.setAnchorY(editorYToAnchor(my));
                    break;
            }
            configManager.markDirty();
            return true;
        }

        return false;
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (dragState != DragState.IDLE) {
            dragState = DragState.IDLE;
            activeHandle = null;
            configManager.saveIfDirty();
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (pickerWidget != null && pickerWidget.mouseScrolled((int) mouseX, (int) mouseY, verticalAmount)) {
            return true;
        }

        if (selectedElement != null) {
            float newScale = selectedElement.getScale() + (float)(verticalAmount * 0.1);
            selectedElement.setScale(newScale);
            configManager.markDirty();
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    private ResizeHandle getHoveredHandle(int mx, int my, PanelElement element) {
        int x = element.getScreenX(width);
        int y = getEditorY(element);
        int w = element.getScaledWidth();
        int h = element.getScaledHeight();

        if (isInHandle(mx, my, x, y)) return ResizeHandle.TOP_LEFT;
        if (isInHandle(mx, my, x + w, y)) return ResizeHandle.TOP_RIGHT;
        if (isInHandle(mx, my, x, y + h)) return ResizeHandle.BOTTOM_LEFT;
        if (isInHandle(mx, my, x + w, y + h)) return ResizeHandle.BOTTOM_RIGHT;
        return null;
    }

    private boolean isInHandle(int mx, int my, int hx, int hy) {
        int halfSize = HANDLE_SIZE / 2;
        return mx >= hx - halfSize && mx <= hx + halfSize
                && my >= hy - halfSize && my <= hy + halfSize;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void onClose() {
        configManager.save();
        super.onClose();
    }
}
