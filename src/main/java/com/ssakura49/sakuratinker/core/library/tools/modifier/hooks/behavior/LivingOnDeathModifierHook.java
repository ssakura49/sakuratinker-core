package com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.behavior;

import com.ssakura49.sakuratinker.core.utils.SlotType;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.module.ModuleHook;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

public interface LivingOnDeathModifierHook {
    default SlotType getValidSlots() {
        return SlotType.ANY;
    }

    void onDeath(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, LivingEntity victim, boolean hasAttacker);

    record AllMerger(Collection<LivingOnDeathModifierHook> modules) implements LivingOnDeathModifierHook {
        @Override
        public SlotType getValidSlots() {
            return LivingOnDeathModifierHook.super.getValidSlots();
        }

        @Override
        public void onDeath(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, LivingEntity victim, boolean hasAttacker) {
            for (LivingOnDeathModifierHook module : modules) {
                if (module.getValidSlots().matches(slotType)) {
                    module.onDeath(tool, modifier, context, slotType, source, victim, hasAttacker);
                }
            }
        }
    }

    static boolean hasAttacker(DamageSource source) {
        return source.getEntity() != null;
    }

    static void handleDeath(ModuleHook<LivingOnDeathModifierHook> hook, EquipmentContext context, DamageSource source, LivingEntity victim) {
        for (EquipmentSlot slotType : EquipmentSlot.values()) {
            IToolStackView toolStack = context.getToolInSlot(slotType);

            if (toolStack != null && !toolStack.isBroken()) {
                for (ModifierEntry entry : toolStack.getModifierList()) {
                    entry.getHook(hook).onDeath(toolStack, entry, context, slotType, source, victim, hasAttacker(source));
                }
            }
        }
    }

}
