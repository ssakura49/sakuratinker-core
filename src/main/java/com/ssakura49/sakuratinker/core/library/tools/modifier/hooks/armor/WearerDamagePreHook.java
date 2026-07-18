package com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.armor;

import com.ssakura49.sakuratinker.core.library.context.AttackedContent;
import java.util.Collection;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public interface WearerDamagePreHook {
   default void onDamagePre(IToolStackView armor, ModifierEntry entry, LivingHurtEvent event, AttackedContent data) {
   }

   public static record AllMerger(Collection<WearerDamagePreHook> modules) implements WearerDamagePreHook {
      public void onDamagePre(IToolStackView armor, ModifierEntry entry, LivingHurtEvent event, AttackedContent data) {
         for(WearerDamagePreHook module : this.modules) {
            module.onDamagePre(armor, entry, event, data);
         }

      }
   }
}
