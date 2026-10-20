package com.ssakura49.sakuratinker.core.client.gui.item;

import net.minecraft.client.renderer.RenderType;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.STHooks;
import com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.display.CustomBarDisplayModifierHook;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.client.IItemDecorator;
import slimeknights.tconstruct.common.TinkerTags;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.item.IModifiable;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ToolStack;
import slimeknights.tconstruct.library.tools.stat.ToolStats;

public class ToolCustomBarDecorator implements IItemDecorator {
    public static final ToolCustomBarDecorator INSTANCE = new ToolCustomBarDecorator();

    private static final int ROW_HEIGHT = 2;

    private static final int DURABILITY_BAR_Y = 13;

    private static final int MAX_BARS_PER_MODIFIER = 4;

    @Override
    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        if (stack.isEmpty() || !(stack.getItem() instanceof IModifiable)) {
            return false;
        }

        IToolStackView tool = ToolStack.from(stack);
        int globalIndex = 0;
        int rowOffset = hasDurabilityBar(tool) ? ROW_HEIGHT : 0;

        for (ModifierEntry entry : tool.getModifierList()) {
            CustomBarDisplayModifierHook hook = entry.getHook(STHooks.CUSTOM_BAR);
            for (int localIndex = 0; localIndex < MAX_BARS_PER_MODIFIER; localIndex++) {
                if (!hook.shouldShowBar(tool, entry, localIndex)) {
                    break;
                }
                renderBar(guiGraphics, tool, entry, hook, xOffset, yOffset, localIndex, globalIndex, rowOffset);
                globalIndex++;
            }
        }

        return false;
    }

    private static boolean hasDurabilityBar(IToolStackView tool) {
        return tool.getItem().asItem().canBeDepleted()
                && tool.hasTag(TinkerTags.Items.DURABILITY)
                && tool.getStats().getInt(ToolStats.DURABILITY) > 0;
    }

    private void renderBar(GuiGraphics guiGraphics, IToolStackView tool, ModifierEntry entry,
                           CustomBarDisplayModifierHook hook, int itemX, int itemY,
                           int localIndex, int globalIndex, int rowOffset) {

        Vec2 barPos = hook.getBarPos(tool, entry, localIndex);
        Vec2 barSize = hook.getBarSize(tool, entry, localIndex);

        int renderX = itemX + (int) barPos.x;
        int renderY = itemY + (int) barPos.y - ROW_HEIGHT * globalIndex - rowOffset;
        int totalWidth = (int) barSize.x;
        int totalHeight = (int) barSize.y;

        float progress = hook.getBarProgress(tool, entry, localIndex);
        int filledWidth = Math.round(totalWidth * progress);

        long time = System.currentTimeMillis();
        float speed = hook.getEffectSpeed(tool, entry, localIndex);

        RenderType renderType = hook.getRenderType(tool, entry, localIndex);

        if (hook.showShadow(tool, entry, localIndex)) {
            Vec2 shadowOffset = hook.getShadowOffset(tool, entry, localIndex);
            Vec2 shadowSize = hook.getShadowSize(tool, entry, localIndex);
            int shadowX = renderX + (int) shadowOffset.x;
            int shadowY = renderY + (int) shadowOffset.y;
            guiGraphics.fill(renderType, shadowX, shadowY, shadowX + (int) shadowSize.x, shadowY + (int) shadowSize.y, 0xFF000000);
        }

        int baseColor = hook.getBarColor(tool, entry, localIndex);
        if ((baseColor >>> 24) == 0) {
            baseColor |= 0xFF000000;
        }
        CustomBarDisplayModifierHook.EffectType effect = hook.getEffectType(tool, entry, localIndex);

        if (effect == CustomBarDisplayModifierHook.EffectType.RAINBOW) {
            float hue = ((time * 0.001F * speed) % 1.0F);
            baseColor = (0xFF << 24) | (Mth.hsvToRgb(hue, 0.85F, 1.0F) & 0xFFFFFF);
        } else if (effect == CustomBarDisplayModifierHook.EffectType.BREATHING) {
            float sin = (Mth.sin(time * 0.005F * speed) + 1.0F) / 2.0F; // 0.0 ~ 1.0
            int alpha = (int) (150 + sin * 105);
            baseColor = (alpha << 24) | (baseColor & 0xFFFFFF);
        }

        if (filledWidth > 0) {
            ResourceLocation texture = hook.getCustomTexture(tool, entry, localIndex);

            if (effect == CustomBarDisplayModifierHook.EffectType.TEXTURE_FLOW && texture != null) {
                Vec2 flowSpeed = hook.getTextureFlowSpeed(tool, entry, localIndex);
                float uOffset = (time * 0.001F * flowSpeed.x * speed) % 1.0F;
                float vOffset = (time * 0.001F * flowSpeed.y * speed) % 1.0F;
                guiGraphics.blit(texture, renderX, renderY, filledWidth, totalHeight,
                        uOffset, vOffset, filledWidth, totalHeight, 16, 16);
            } else {
                guiGraphics.fill(renderType, renderX, renderY, renderX + filledWidth, renderY + totalHeight, baseColor);
            }

            if (effect == CustomBarDisplayModifierHook.EffectType.SWEEPING_GLINT) {
                float glintProgress = ((time * 0.0015F * speed) % 1.5F) - 0.5F;
                int glintX = renderX + (int) (totalWidth * glintProgress);
                int glintWidth = 3;
                int drawGlintStart = Math.max(glintX, renderX);
                int drawGlintEnd = Math.min(glintX + glintWidth, renderX + filledWidth);

                if (drawGlintEnd > drawGlintStart) {
                    guiGraphics.fill(renderType, drawGlintStart, renderY, drawGlintEnd, renderY + totalHeight, 0x80FFFFFF);
                }
            }
        }

    }
}
