package com.ssakura49.sakuratinker.core.utils;

import com.ssakura49.sakuratinker.STConfig;
import java.util.List;

import com.ssakura49.sakuratinker.STCrConfig;
import net.minecraft.resources.ResourceLocation;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.modifiers.ModifierId;

public class ModifierConfigHelper {
   public static boolean isBlacklisted(ModifierEntry entry) {
      List<? extends String> list = STCrConfig.COMMON.MODIFIER_BLACKLIST.get();
      ResourceLocation id = entry.getId();

      for(String s : list) {
         if (id.equals(ResourceLocation.parse(s))) {
            return true;
         }
      }

      return false;
   }

   public static boolean isBlacklisted(ModifierId modifierId) {
      List<? extends String> list = STCrConfig.COMMON.MODIFIER_BLACKLIST.get();
      ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modifierId.getNamespace(), modifierId.getPath());

      for(String s : list) {
         if (id.equals(ResourceLocation.parse(s))) {
            return true;
         }
      }

      return false;
   }
}
