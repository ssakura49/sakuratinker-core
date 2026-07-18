package com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.click;

import com.ssakura49.sakuratinker.core.init.STCrKeys;
import java.util.Collection;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public interface KeyPressModifierHook {
   default void onKeyPress(IToolStackView tool, ModifierEntry modifier, Player player, String key) {
   }

   default @Nullable String getKeyId(IToolStackView tool, ModifierEntry modifier) {
      return null;
   }

   default String getKeyDisplay(IToolStackView tool, ModifierEntry modifier) {
      String id = this.getKeyId(tool, modifier);
      if (id == null) {
         return "???";
      } else {
         KeyMapping mapping = STCrKeys.getMapping(id);
         return mapping != null ? mapping.getTranslatedKeyMessage().getString() : "???";
      }
   }

   public static record AllMerger(Collection<KeyPressModifierHook> modules) implements KeyPressModifierHook {
      public void onKeyPress(IToolStackView tool, ModifierEntry modifier, Player player, String key) {
         for(KeyPressModifierHook module : this.modules) {
            module.onKeyPress(tool, modifier, player, key);
         }

      }
   }
}
