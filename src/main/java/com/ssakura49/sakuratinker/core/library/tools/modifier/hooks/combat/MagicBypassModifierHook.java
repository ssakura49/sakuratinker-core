package com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.combat;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/**
 * 用于在魔法/保护附魔计算阶段修改魔抗穿透比例
 */
public interface MagicBypassModifierHook {

    /**
     * 计算最终的魔法/保护穿透比例 (0.0F ~ 1.0F)
     *
     * @param tool 工具 Stack
     * @param entry 当前词条及其等级
     * @param source 伤害来源
     * @param attacker 攻击者
     * @param target 受击者
     * @param damageAfterArmor 经过护甲结算后的伤害
     * @param vanillaMagicDamage 原版魔抗/抗性计算后剩余的伤害
     * @param currentBypass 当前累积的魔抗穿透比例
     * @return 修正后的魔抗穿透比例
     */
    float modifyMagicBypass(
            IToolStackView tool, ModifierEntry entry,
            DamageSource source, LivingEntity attacker, LivingEntity target,
            float damageAfterArmor, float vanillaMagicDamage, float currentBypass
    );

    record AllMerger(Collection<MagicBypassModifierHook> modules) implements MagicBypassModifierHook {
        @Override
        public float modifyMagicBypass(
                IToolStackView tool, ModifierEntry entry,
                DamageSource source, LivingEntity attacker, LivingEntity target,
                float damageAfterArmor, float vanillaMagicDamage, float currentBypass) {

            float result = currentBypass;
            for (MagicBypassModifierHook module : modules) {
                result = module.modifyMagicBypass(tool, entry, source, attacker, target, damageAfterArmor, vanillaMagicDamage, result);
            }
            return result;
        }
    }
}
