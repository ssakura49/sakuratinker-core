package com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.display;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import org.joml.Math;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

public interface CustomBarDisplayModifierHook {
    /** ID，用于区分不同的Bar */
    String getBarId(IToolStackView tool, ModifierEntry entry, int barsShown);

    boolean shouldShowBar(IToolStackView tool, ModifierEntry entry, int barsShown);

    /** 进度条填充比例，范围 0.0F - 1.0F */
    float getBarProgress(IToolStackView tool, ModifierEntry entry, int barsShown);

    /** 进度条填充颜色 (ARGB 格式，例如 0xFF00FF00) */
    int getBarColor(IToolStackView tool, ModifierEntry entry, int barsShown);

    /** 进度条尺寸 (Width, Height)，默认 13x1 像素 */
    default Vec2 getBarSize(IToolStackView tool, ModifierEntry entry, int barsShown) {
        return new Vec2(13, 1);
    }

    /** 进度条在 16x16 槽位中的相对坐标 (X, Y)，默认根据已有数量自动向上堆叠 */
    default Vec2 getBarPos(IToolStackView tool, ModifierEntry entry, int barsShown) {
        return new Vec2(2, 13 - (barsShown * 2));
    }

    /** 是否绘制阴影 */
    default boolean showShadow(IToolStackView tool, ModifierEntry entry, int barsShown) {
        return true;
    }
    default Vec2 getShadowSize(IToolStackView tool, ModifierEntry entry, int barsShown) {
        Vec2 barSize = getBarSize(tool, entry, barsShown);
        return new Vec2(barSize.x, barSize.y + 1);
    }

    /** 阴影相对偏移 */
    default Vec2 getShadowOffset(IToolStackView tool, ModifierEntry entry, int barsShown) {
        return new Vec2(0, 0);
    }

    default RenderType getRenderType(IToolStackView tool, ModifierEntry entry, int barsShown) {
        return RenderType.guiOverlay();
    }

    enum EffectType {
        NONE,            // 无特效（普通矩形）
        BREATHING,       // 呼吸灯/脉冲
        RAINBOW,         // 彩虹渐变
        SWEEPING_GLINT,  // 跑马灯/流光
        TEXTURE_FLOW     // 纹理流动
    }
    /** 获取当前 Bar 启用的主特效类型 */
    default EffectType getEffectType(IToolStackView tool, ModifierEntry entry, int barsShown) {
        return EffectType.NONE;
    }
    /** 呼吸灯/流光特效的速度（默认 1.0F） */
    default float getEffectSpeed(IToolStackView tool, ModifierEntry entry, int barsShown) {
        return 1.0F;
    }
    default ResourceLocation getCustomTexture(IToolStackView tool, ModifierEntry entry, int barsShown) {
        return null;
    }
    /** 纹理在 U/V 方向上的流动速度 (X=U速, Y=V速) */
    default Vec2 getTextureFlowSpeed(IToolStackView tool, ModifierEntry entry, int barsShown) {
        return new Vec2(0.1F, 0.0F);
    }

    record AllMerger(Collection<CustomBarDisplayModifierHook> modules) implements CustomBarDisplayModifierHook {

        private CustomBarDisplayModifierHook findNthActive(IToolStackView tool, ModifierEntry entry, int barsShown) {
            int activeCount = 0;
            for (CustomBarDisplayModifierHook module : this.modules) {
                if (module.shouldShowBar(tool, entry, activeCount)) {
                    if (activeCount == barsShown) {
                        return module;
                    }
                    activeCount++;
                }
            }
            return null;
        }

        @Override
        public boolean shouldShowBar(IToolStackView tool, ModifierEntry entry, int barsShown) {
            return findNthActive(tool, entry, barsShown) != null;
        }

        @Override
        public String getBarId(IToolStackView tool, ModifierEntry entry, int barsShown) {
            CustomBarDisplayModifierHook active = findNthActive(tool, entry, barsShown);
            return active != null ? active.getBarId(tool, entry, barsShown) : "";
        }

        @Override
        public float getBarProgress(IToolStackView tool, ModifierEntry entry, int barsShown) {
            CustomBarDisplayModifierHook active = findNthActive(tool, entry, barsShown);
            return active != null ? Math.clamp(active.getBarProgress(tool, entry, barsShown), 0.0F, 1.0F) : 0.0F;
        }

        @Override
        public int getBarColor(IToolStackView tool, ModifierEntry entry, int barsShown) {
            CustomBarDisplayModifierHook active = findNthActive(tool, entry, barsShown);
            return active != null ? active.getBarColor(tool, entry, barsShown) : 0xFFFFFFFF;
        }

        @Override
        public Vec2 getBarSize(IToolStackView tool, ModifierEntry entry, int barsShown) {
            CustomBarDisplayModifierHook active = findNthActive(tool, entry, barsShown);
            return active != null ? active.getBarSize(tool, entry, barsShown) : new Vec2(13, 1);
        }

        @Override
        public Vec2 getBarPos(IToolStackView tool, ModifierEntry entry, int barsShown) {
            CustomBarDisplayModifierHook active = findNthActive(tool, entry, barsShown);
            return active != null ? active.getBarPos(tool, entry, barsShown) : new Vec2(2, 13 - (barsShown * 2));
        }

        @Override
        public boolean showShadow(IToolStackView tool, ModifierEntry entry, int barsShown) {
            CustomBarDisplayModifierHook active = findNthActive(tool, entry, barsShown);
            return active != null && active.showShadow(tool, entry, barsShown);
        }

        @Override
        public Vec2 getShadowSize(IToolStackView tool, ModifierEntry entry, int barsShown) {
            CustomBarDisplayModifierHook active = findNthActive(tool, entry, barsShown);
            return active != null ? active.getShadowSize(tool, entry, barsShown) : new Vec2(13, 2);
        }

        @Override
        public Vec2 getShadowOffset(IToolStackView tool, ModifierEntry entry, int barsShown) {
            CustomBarDisplayModifierHook active = findNthActive(tool, entry, barsShown);
            return active != null ? active.getShadowOffset(tool, entry, barsShown) : new Vec2(0, 0);
        }

        @Override
        public RenderType getRenderType(IToolStackView tool, ModifierEntry entry, int barsShown) {
            CustomBarDisplayModifierHook active = findNthActive(tool, entry, barsShown);
            return active != null ? active.getRenderType(tool, entry, barsShown) : RenderType.guiOverlay();
        }

        @Override
        public EffectType getEffectType(IToolStackView tool, ModifierEntry entry, int barsShown) {
            CustomBarDisplayModifierHook active = findNthActive(tool, entry, barsShown);
            return active != null ? active.getEffectType(tool, entry, barsShown) : EffectType.NONE;
        }
        @Override
        public float getEffectSpeed(IToolStackView tool, ModifierEntry entry, int barsShown) {
            CustomBarDisplayModifierHook active = findNthActive(tool, entry, barsShown);
            return active != null ? active.getEffectSpeed(tool, entry, barsShown) : 1.0F;
        }
        @Override
        public ResourceLocation getCustomTexture(IToolStackView tool, ModifierEntry entry, int barsShown) {
            CustomBarDisplayModifierHook active = findNthActive(tool, entry, barsShown);
            return active != null ? active.getCustomTexture(tool, entry, barsShown) :  null;
        }
        @Override
        public Vec2 getTextureFlowSpeed(IToolStackView tool, ModifierEntry entry, int barsShown) {
            CustomBarDisplayModifierHook active = findNthActive(tool, entry, barsShown);
            return active != null ? active.getTextureFlowSpeed(tool, entry, barsShown) : new Vec2(0.1F, 0.0F);
        }
    }
}
