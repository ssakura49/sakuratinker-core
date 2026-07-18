package com.ssakura49.sakuratinker.core.tools.modifiers.base;

import java.util.Arrays;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public interface IConfigurableModifier {
   Object[] getDescArgs();

   void refreshConfig();

   static List<Component> createDescriptions(String transKey, IConfigurableModifier modifier) {
      return Arrays.asList(Component.translatable(transKey + ".flavor").withStyle(ChatFormatting.ITALIC), Component.translatable(transKey + ".description", modifier.getDescArgs()).withStyle(ChatFormatting.GRAY));
   }
}
