package com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.armor;

import com.ssakura49.sakuratinker.core.library.context.AttackedContent;
import java.util.Collection;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public interface WearerDamageTakeHook {
   default void onTakeDamagePre(IToolStackView armor, ModifierEntry entry, LivingHurtEvent event, AttackedContent data) {
   }

   default void onTakeDamagePost(IToolStackView armor, ModifierEntry entry, LivingDamageEvent event, AttackedContent data) {
   }

   public static record AllMerger(Collection<WearerDamageTakeHook> modules) implements WearerDamageTakeHook {
      public void onTakeDamagePre(IToolStackView armor, ModifierEntry entry, LivingHurtEvent event, AttackedContent data) {
         for(WearerDamageTakeHook module : this.modules) {
            module.onTakeDamagePre(armor, entry, event, data);
         }

      }

      public void onTakeDamagePost(IToolStackView armor, ModifierEntry entry, LivingDamageEvent event, AttackedContent data) {
         for(WearerDamageTakeHook module : this.modules) {
            module.onTakeDamagePost(armor, entry, event, data);
         }

      }
   }
}
