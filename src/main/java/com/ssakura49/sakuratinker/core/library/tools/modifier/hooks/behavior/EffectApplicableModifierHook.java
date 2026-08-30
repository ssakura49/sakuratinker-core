package com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.behavior;

import com.ssakura49.sakuratinker.core.utils.EffectResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

public interface EffectApplicableModifierHook {
    /**
     * 检查并处理即将施加的药水效果
     * @param tool 当前装备
     * @param entry 词条信息（等级等）
     * @param slot 当前槽位
     * @param instance 当前尝试施加的药水实例
     * @param entity 目标实体
     * @return EffectResult (PASS: 放行, BLOCK: 拦截, MODIFY: 修改后放行)
     */
    EffectResult onApplyEffect(IToolStackView tool, ModifierEntry entry, EquipmentSlot slot, MobEffectInstance instance, LivingEntity entity);

    record AllMerger(Collection<EffectApplicableModifierHook> modules) implements EffectApplicableModifierHook {
        public EffectResult onApplyEffect(IToolStackView tool, ModifierEntry entry, EquipmentSlot slot, MobEffectInstance instance, LivingEntity entity) {
            MobEffectInstance currentInstance = instance;
            boolean wasModified = false;

            for (EffectApplicableModifierHook module : this.modules) {
                EffectResult result = module.onApplyEffect(tool, entry, slot, currentInstance, entity);
                if (result.getAction() == EffectResult.Action.BLOCK) {
                    return EffectResult.block();
                }
                if (result.getAction() == EffectResult.Action.MODIFY && result.getModifiedEffect() != null) {
                    currentInstance = result.getModifiedEffect();
                    wasModified = true;
                }
            }
            return wasModified ? EffectResult.modify(currentInstance) : EffectResult.pass();
        }
    }

}
