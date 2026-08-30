package com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.combat;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

public interface PostDamageModifierHook {

    /**
     * 修正最终伤害
     * @param tool 工具 Stack
     * @param entry 当前词条及其等级
     * @param source 伤害来源
     * @param attacker 攻击者
     * @param target 受击者
     * @param currentDamage 当前阶段算出的实际伤害
     * @return 修改后的最终实际伤害
     */
    float modifyPostDamage(
            IToolStackView tool, ModifierEntry entry,
            DamageSource source, LivingEntity attacker, LivingEntity target,
            float currentDamage
    );

    record AllMerger(Collection<PostDamageModifierHook> modules) implements PostDamageModifierHook {
        @Override
        public float modifyPostDamage(
                IToolStackView tool, ModifierEntry entry,
                DamageSource source, LivingEntity attacker, LivingEntity target,
                float currentDamage) {

            float result = currentDamage;
            for (PostDamageModifierHook module : modules) {
                result = module.modifyPostDamage(tool, entry, source, attacker, target, result);
            }
            return result;
        }
    }
}
