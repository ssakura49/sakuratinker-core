package com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.combat;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

public interface ArmorBypassModifierHook {
    /**
     * 计算最终的护甲穿透比例 (0.0F ~ 1.0F)
     *
     * @param tool 工具 Stack
     * @param entry 当前词条及其等级
     * @param source 伤害来源
     * @param attacker 攻击者
     * @param target 受击者
     * @param rawDamage 未经过护甲削减的原始伤害
     * @param vanillaArmorDamage 原版护甲计算后剩余的伤害
     * @param currentBypass 当前累积的护甲穿透比例
     * @return 修正后的护甲穿透比例
     */
    float modifyArmorBypass(
            IToolStackView tool, ModifierEntry entry,
            DamageSource source, LivingEntity attacker, LivingEntity target,
            float rawDamage, float vanillaArmorDamage, float currentBypass
    );

    record AllMerger(Collection<ArmorBypassModifierHook> modules) implements ArmorBypassModifierHook {
        @Override
        public float modifyArmorBypass(
                IToolStackView tool, ModifierEntry entry,
                DamageSource source, LivingEntity attacker, LivingEntity target,
                float rawDamage, float vanillaArmorDamage, float currentBypass) {
            float result = currentBypass;
            for (ArmorBypassModifierHook module : modules) {
                result = module.modifyArmorBypass(tool, entry, source, attacker, target, rawDamage, vanillaArmorDamage, result);
            }
            return result;
        }
    }
}
