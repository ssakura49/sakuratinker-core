package com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.combat;

import java.util.Collection;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public interface HolderDamageTakeModifierHook {
   default void onHolderTakeDamage(IToolStackView tool, ModifierEntry entry, LivingDamageEvent event, LivingEntity entity, DamageSource source) {
   }

   public static record AllMerge(Collection<HolderDamageTakeModifierHook> modules) implements HolderDamageTakeModifierHook {
      public void onHolderTakeDamage(IToolStackView tool, ModifierEntry entry, LivingDamageEvent event, LivingEntity entity, DamageSource source) {
         for(HolderDamageTakeModifierHook module : this.modules) {
            module.onHolderTakeDamage(tool, entry, event, entity, source);
         }

      }
   }
}
