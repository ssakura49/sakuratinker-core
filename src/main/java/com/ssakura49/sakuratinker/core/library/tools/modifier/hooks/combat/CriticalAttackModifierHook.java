package com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.combat;

import java.util.Collection;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public interface CriticalAttackModifierHook {
   boolean setCritical(IToolStackView var1, ModifierEntry var2, LivingEntity var3, InteractionHand var4, Entity var5, EquipmentSlot var6, boolean var7, boolean var8, boolean var9);

   public static record FirstMerger(Collection<CriticalAttackModifierHook> modules) implements CriticalAttackModifierHook {
      public boolean setCritical(IToolStackView tool, ModifierEntry entry, LivingEntity attacker, InteractionHand hand, Entity target, EquipmentSlot sourceSlot, boolean isFullyCharged, boolean isExtraAttack, boolean isCritical) {
         for(CriticalAttackModifierHook module : this.modules) {
            isCritical = module.setCritical(tool, entry, attacker, hand, target, sourceSlot, isFullyCharged, isExtraAttack, isCritical);
            if (isCritical) {
               return isCritical;
            }
         }

         return isCritical;
      }
   }
}
