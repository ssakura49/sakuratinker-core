package com.ssakura49.sakuratinker.core.client.component;

import com.ssakura49.sakura_library.util.java.function.ExeCallable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum LoreStyle {
   NONE((mutableComponent) -> mutableComponent),
   ATTRIBUTE_PREFIX((mutableComponent) -> Component.literal("- ").withStyle(ChatFormatting.DARK_PURPLE).append(mutableComponent));

   private final ExeCallable<MutableComponent> delegate;

   public ExeCallable<MutableComponent> getDelegate() {
      return this.delegate;
   }

   private LoreStyle(ExeCallable<MutableComponent> delegate) {
      this.delegate = delegate;
   }

   // $FF: synthetic method
   private static LoreStyle[] $values() {
      return new LoreStyle[]{NONE, ATTRIBUTE_PREFIX};
   }
}
