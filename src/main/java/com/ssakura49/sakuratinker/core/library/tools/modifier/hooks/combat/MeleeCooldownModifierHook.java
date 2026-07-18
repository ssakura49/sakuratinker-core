package com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.combat;

import com.ssakura49.sakura_library.event.entity.player.AttackSpeedModifyEvent;
import java.util.Collection;
import net.minecraft.world.entity.player.Player;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public interface MeleeCooldownModifierHook {
   default void modifyAttackCooldown(IToolStackView tool, ModifierEntry entry, Player player, AttackSpeedModifyEvent event) {
   }

   public static record AllMerger(Collection<MeleeCooldownModifierHook> modules) implements MeleeCooldownModifierHook {
      public void modifyAttackCooldown(IToolStackView tool, ModifierEntry entry, Player player, AttackSpeedModifyEvent event) {
         for(MeleeCooldownModifierHook module : this.modules) {
            module.modifyAttackCooldown(tool, entry, player, event);
         }

      }
   }
}
