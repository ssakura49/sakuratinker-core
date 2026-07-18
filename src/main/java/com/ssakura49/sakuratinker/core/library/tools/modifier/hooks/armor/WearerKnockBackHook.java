package com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.armor;

import java.util.Collection;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.context.EquipmentContext;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public interface WearerKnockBackHook {
   default void onKnockBack(IToolStackView armor, ModifierEntry entry, LivingKnockBackEvent event, EquipmentContext context) {
   }

   public static record AllMerger(Collection<WearerKnockBackHook> modules) implements WearerKnockBackHook {
      public void onKnockBack(IToolStackView armor, ModifierEntry entry, LivingKnockBackEvent event, EquipmentContext context) {
         for(WearerKnockBackHook module : this.modules) {
            module.onKnockBack(armor, entry, event, context);
         }

      }
   }
}
