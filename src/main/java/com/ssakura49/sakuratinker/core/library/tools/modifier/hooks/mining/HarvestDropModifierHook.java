package com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.mining;

import net.minecraft.world.level.block.state.BlockState;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

public interface HarvestDropModifierHook {
    /**
     * 判断当前工具在此方块破坏时是否能触发掉落
     * @param tool    工具实例
     * @param entry   修饰物条目（包含等级）
     * @param state   被采掘的方块状态
     * @param current 当前判定结果
     * @return 修改后的判定结果
     */
    boolean isEffectiveForDrop(IToolStackView tool, ModifierEntry entry, BlockState state, boolean current);

    record AllMerger(Collection<HarvestDropModifierHook> modules) implements HarvestDropModifierHook {
        @Override
        public boolean isEffectiveForDrop(IToolStackView tool, ModifierEntry entry, BlockState state, boolean current) {
            boolean result = current;
            for (HarvestDropModifierHook module : this.modules) {
                result = module.isEffectiveForDrop(tool, entry, state, result);
                if (result) {
                    return true;
                }
            }
            return result;
        }
    }
}
