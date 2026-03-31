package com.pixelpanel.hud.elements;

import com.pixelpanel.hud.PanelElement;
import com.pixelpanel.hud.PanelElementType;
import com.pixelpanel.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;

public class StatusEffectsElement extends PanelElement {

    public StatusEffectsElement() {
        super(PanelElementType.STATUS_EFFECTS);
    }

    @Override
    public void render(GuiGraphicsExtractor context, float tickDelta, int screenWidth, int screenHeight) {
        Minecraft client = Minecraft.getInstance();
        Player player = client.player;
        if (player == null) return;

        Font font = client.font;
        Collection<MobEffectInstance> effects = player.getActiveEffects();

        if (showBackground()) {
            RenderUtils.drawRect(context, 0, 0, getWidth(), getHeight(), 0x80000000);
        }

        if (effects.isEmpty()) {
            context.text(font, "No Effects", 4, 4, 0xFF666666, true);
            return;
        }

        int yPos = 2;
        int entryHeight = 20;

        for (MobEffectInstance effect : effects) {
            if (yPos + entryHeight > getHeight()) break;

            // Render effect icon
            final int iconY = yPos;
            effect.getEffect().unwrapKey().ifPresent(key -> {
                Identifier spriteId = Identifier.fromNamespaceAndPath(
                        key.identifier().getNamespace(),
                        "mob_effect/" + key.identifier().getPath());
                try {
                    context.blitSprite(RenderPipelines.GUI_TEXTURED, spriteId, 2, iconY, 16, 16);
                } catch (Exception ignored) {
                    // Sprite might not exist for modded effects
                }
            });

            // Effect name with amplifier
            String name = effect.getEffect().value().getDisplayName().getString();
            int amplifier = effect.getAmplifier();
            if (amplifier > 0) {
                name += " " + toRoman(amplifier + 1);
            }

            // Duration
            int duration = effect.getDuration();
            String durationStr;
            if (duration == MobEffectInstance.INFINITE_DURATION) {
                durationStr = "\u221E";
            } else {
                int totalSeconds = duration / 20;
                int minutes = totalSeconds / 60;
                int seconds = totalSeconds % 60;
                durationStr = String.format("%d:%02d", minutes, seconds);
            }

            // Color from effect
            int color = effect.getEffect().value().getColor();
            if (color == 0) color = 0xBBBBBB;
            color = 0xFF000000 | color;

            int textY = yPos + (entryHeight - font.lineHeight) / 2;
            context.text(font, name, 21, textY, color, true);
            context.text(font, durationStr, getWidth() - font.width(durationStr) - 4, textY, 0xFFAAAAAA, true);

            // Subtle separator line
            if (yPos > 2) {
                context.fill(2, yPos - 1, getWidth() - 2, yPos, 0x20FFFFFF);
            }

            yPos += entryHeight;
        }
    }

    private String toRoman(int num) {
        return switch (num) {
            case 1 -> "I";
            case 2 -> "II";
            case 3 -> "III";
            case 4 -> "IV";
            case 5 -> "V";
            default -> String.valueOf(num);
        };
    }

    @Override
    public int getDefaultWidth() { return 150; }
    @Override
    public int getDefaultHeight() { return 64; }
    @Override
    public String getDisplayName() { return "Status Effects"; }
    @Override
    public boolean isResizable() { return true; }
    @Override
    public int getMinWidth() { return 110; }
    @Override
    public int getMinHeight() { return 22; }
    @Override
    public int getMaxWidth() { return 220; }
    @Override
    public int getMaxHeight() { return 220; }
}
