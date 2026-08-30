package com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.range;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;
import slimeknights.tconstruct.library.tools.nbt.ModifierNBT;

import java.util.Collection;

public interface ArrowHitModifierHook {
    /**
     *
     * @param persistentData 传入箭矢的NBT信息
     * @param entry ModifierEntry实例
     * @param modifiers 传入箭矢的工具词条NBT,因为箭矢无法定位工具,所有需要这样来确保箭矢有效果
     * @param arrow 箭矢实体
     * @param attacker 攻击者
     * @param target 被攻击者
     * @param damageDealt 原箭矢实际造成的伤害结果
     */
    default void afterArrowHit(ModDataNBT persistentData, ModifierEntry entry, ModifierNBT modifiers, AbstractArrow arrow, @Nullable LivingEntity attacker, @NotNull LivingEntity target, float damageDealt) {
    }

    record AllMerger(Collection<ArrowHitModifierHook> modules) implements ArrowHitModifierHook {
        public AllMerger(Collection<ArrowHitModifierHook> modules) {
            this.modules = modules;
        }

        public void afterArrowHit(ModDataNBT persistentData, ModifierEntry entry, ModifierNBT modifiers, AbstractArrow arrow, @Nullable LivingEntity attacker, @NotNull LivingEntity target, float damageDealt) {
            for(ArrowHitModifierHook module:this.modules) {
                module.afterArrowHit(persistentData, entry,modifiers, arrow,attacker,target, damageDealt);
            }
        }

        public Collection<ArrowHitModifierHook> modules() {
            return this.modules;
        }
    }
}
