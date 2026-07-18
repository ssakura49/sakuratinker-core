package com.ssakura49.sakuratinker.core.utils.component;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class ComponentUtil {
   public static ResourceLocation getResource(String name) {
      return ResourceLocation.fromNamespaceAndPath("sakuratinker", name);
   }

   public static String makeTranslationKey(String base, @Nullable ResourceLocation name) {
      return Util.makeDescriptionId(base, name);
   }

   public static Component makeTranslation(String base, @Nullable ResourceLocation name, Object... arguments) {
      return Component.translatable(makeTranslationKey(base, name), arguments);
   }

   public static String makeTranslationKey(String base, String name) {
      return makeTranslationKey(base, getResource(name));
   }

   public static MutableComponent makeTranslation(String base, String name) {
      return Component.translatable(makeTranslationKey(base, name));
   }

   public static MutableComponent makeTranslation(String base, String name, Object... arguments) {
      return Component.translatable(makeTranslationKey(base, name), arguments);
   }

   public static void colorfulTranslatable(List<Component> list, String translatableText, int color) {
      list.add(Component.translatable(translatableText).withStyle((style) -> style.withColor(color)));
   }

   public static void colorfulLiteral(List<Component> list, String literal, int color) {
      list.add(Component.translatable(literal).withStyle((style) -> style.withColor(color)));
   }
}
