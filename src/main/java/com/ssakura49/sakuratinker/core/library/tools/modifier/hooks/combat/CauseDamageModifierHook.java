package com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.combat;

import java.util.Collection;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public interface CauseDamageModifierHook {
   float onCauseDamage(IToolStackView var1, ModifierEntry var2, LivingHurtEvent var3, LivingEntity var4, LivingEntity var5, float var6, float var7);

   public static record AllMerger(Collection<CauseDamageModifierHook> modules) implements CauseDamageModifierHook {
      public float onCauseDamage(IToolStackView tool, ModifierEntry modifier, LivingHurtEvent event, LivingEntity attacker, LivingEntity target, float baseDamage, float currentDamage) {
         for(CauseDamageModifierHook module : this.modules) {
            currentDamage = module.onCauseDamage(tool, modifier, event, attacker, target, baseDamage, currentDamage);
         }

         return currentDamage;
      }
   }
}
