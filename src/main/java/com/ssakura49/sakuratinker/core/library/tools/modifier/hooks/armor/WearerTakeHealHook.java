package com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.armor;

import java.util.Collection;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public interface WearerTakeHealHook {
   default void onTakeHeal(IToolStackView armor, ModifierEntry entry, LivingHealEvent event, EquipmentContext context) {
   }

   public static record AllMerger(Collection<WearerTakeHealHook> modules) implements WearerTakeHealHook {
      public void onTakeHeal(IToolStackView armor, ModifierEntry entry, LivingHealEvent event, EquipmentContext context) {
         for(WearerTakeHealHook module : this.modules) {
            module.onTakeHeal(armor, entry, event, context);
         }

      }
   }
}
