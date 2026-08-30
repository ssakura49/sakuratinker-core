package com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.behavior;

import com.ssakura49.sakuratinker.core.utils.SlotType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

public interface LivingOnPreventDeathModifierHook {
    /**
     * 生效的槽位类型。
     * 默认任何槽位(ANY)；词条可以重写此方法，例如返回SlotType.MAINHAND (仅主手) 或 SlotType.ARMOR (仅防具)。
     */
    default SlotType getValidSlots() {
        return SlotType.ANY;
    }
    /**
     * 当手上工具有此词条,实体将要死亡时候会运行,运行成功会阻止目标死亡.
     * <br>
     * <ul>如果你想要在实体真正死亡时候触发效果,可以用这个
     *   <li>{@link LivingOnDeathModifierHook}: 在实体死亡时候不触发免死但是可以正常执行其他,可以用于施加nbt或者亡语之类的,请注意,只有实体真正死了才会触发这个,如果通过{@link LivingOnPreventDeathModifierHook}中的方法免死了不会执行</li>
     * </ul>
     * @param livingEntity 被作用的实体
     * @param tool         使用的工具
     * @param modifier     词条实例
     * @param context      触发这个语句的实体对应的装备的context
     * @param slotType     槽位种类
     * @param source       导致这次死亡的伤害来源
     * @return 阻止此次死亡后返还的生命值量,如果为0则不会触发免死,请记得一定要给方法来阻止一直免死,除非你很极端
     */
    float onHoldingPreventDeath(LivingEntity livingEntity, IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source);

    default boolean canIgnorePassInvul() {
        return false;
    }

    record FirstMerger(Collection<LivingOnPreventDeathModifierHook> modules) implements LivingOnPreventDeathModifierHook {
        @Override
        public SlotType getValidSlots() {
            return SlotType.ANY;
        }

        @Override
        public float onHoldingPreventDeath(LivingEntity livingEntity, IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source) {
            for (LivingOnPreventDeathModifierHook module : modules) {
                if (module.getValidSlots().matches(slotType)) {
                    float remainHealth = module.onHoldingPreventDeath(livingEntity, tool, modifier, context, slotType, source);
                    if (remainHealth > 0) {
                        return remainHealth;
                    }
                }
            }
            return 0;
        }

        @Override
        public boolean canIgnorePassInvul() {
            for (LivingOnPreventDeathModifierHook module : modules) {
                if (module.canIgnorePassInvul()) {
                    return true;
                }
            }
            return false;
        }
    }

    static float onHoldingPreventDeath(ModuleHook<LivingOnPreventDeathModifierHook> hook, EquipmentContext context, DamageSource source, LivingEntity livingEntity) {
        for (EquipmentSlot slotType : EquipmentSlot.values()) {
            IToolStackView toolStack = context.getToolInSlot(slotType);

            if (toolStack != null && !toolStack.isBroken()) {
                for (ModifierEntry entry : toolStack.getModifierList()) {
                    LivingOnPreventDeathModifierHook preventHook = entry.getHook(hook);
                    if (preventHook.getValidSlots().matches(slotType)) {
                        float healthRemain = preventHook.onHoldingPreventDeath(livingEntity, toolStack, entry, context, slotType, source);
                        if (healthRemain > 0) {
                            return healthRemain;
                        }
                    }
                }
            }
        }
        return 0;
    }

    static boolean canIgnorePassInvul(ModuleHook<LivingOnPreventDeathModifierHook> hook, EquipmentContext context) {
        for (EquipmentSlot slotType : EquipmentSlot.values()) {
            IToolStackView toolStack = context.getToolInSlot(slotType);
            if (toolStack != null && !toolStack.isBroken()) {
                for (ModifierEntry entry : toolStack.getModifierList()) {
                    LivingOnPreventDeathModifierHook preventHook = entry.getHook(hook);
                    if (preventHook.getValidSlots().matches(slotType) && preventHook.canIgnorePassInvul()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
