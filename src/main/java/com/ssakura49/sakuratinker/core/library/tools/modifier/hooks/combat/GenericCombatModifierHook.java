package com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.combat;

import com.ssakura49.sakura_library.event.entity.LivingDamageCalculationEvent;
import com.ssakura49.sakuratinker.core.library.events.TinkerToolCriticalEvent;
import java.util.Collection;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public interface GenericCombatModifierHook {
   default void onMeleeCriticalHit(IToolStackView tool, ModifierEntry entry, TinkerToolCriticalEvent event, LivingEntity attacker, LivingEntity target) {
   }

   default void onKillLivingTarget(IToolStackView tool, ModifierEntry entry, LivingDeathEvent event, LivingEntity attacker, LivingEntity target) {
   }

   record AllMerger(Collection<GenericCombatModifierHook> modules) implements GenericCombatModifierHook {
      public void onMeleeCriticalHit(IToolStackView tool, ModifierEntry entry, TinkerToolCriticalEvent event, LivingEntity attacker, LivingEntity target) {
         for(GenericCombatModifierHook module : this.modules) {
            module.onMeleeCriticalHit(tool, entry, event, attacker, target);
         }

      }

      public void onKillLivingTarget(IToolStackView tool, ModifierEntry entry, LivingDeathEvent event, LivingEntity attacker, LivingEntity target) {
         for(GenericCombatModifierHook module : this.modules) {
            module.onKillLivingTarget(tool, entry, event, attacker, target);
         }

      }
   }
}
