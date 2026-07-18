package com.ssakura49.sakuratinker.core.library.tools.modifier.hooks.builder;

import com.ssakura49.sakuratinker.core.library.context.ReplaceContext;
import java.util.Collection;

public interface ReplaceMaterialModifierHook {
   boolean replaceItem(ReplaceContext var1, int var2, boolean var3);

   public static record AllMerger(Collection<ReplaceMaterialModifierHook> modules) implements ReplaceMaterialModifierHook {
      public boolean replaceItem(ReplaceContext context, int inputIndex, boolean secondary) {
         for(ReplaceMaterialModifierHook module : this.modules) {
            boolean result = module.replaceItem(context, inputIndex, secondary);
            if (result) {
               return true;
            }
         }

         return false;
      }
   }
}
