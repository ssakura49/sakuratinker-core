package com.ssakura49.sakuratinker.core.tools.modifiers.base;

import com.ssakura49.sakuratinker.SakuraTinker;
import java.util.List;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.modifiers.Modifier;
import slimeknights.tconstruct.library.modifiers.ModifierManager;

public abstract class DynamicConfigDescModifier extends BaseModifier implements IConfigurableModifier {
   public @NotNull List<Component> getDescriptionList() {
      if (this.descriptionList == null) {
         this.descriptionList = IConfigurableModifier.createDescriptions(this.getTranslationKey(), this);
      }

      return this.descriptionList;
   }

   public static void refreshAllModifierConfigSpecs() {
      SakuraTinker.LOGGER.info("Reloading and syncing dynamic modifier configurations.");
      ModifierManager.INSTANCE.getAllValues().filter((modifier) -> modifier instanceof IConfigurableModifier).map((modifier) -> (IConfigurableModifier)modifier).forEach((configurable) -> {
         configurable.refreshConfig();
         Modifier mod = (Modifier)configurable;
         SakuraTinker.LOGGER.info("Successfully reloaded config for modifier: {}", mod.getId());
      });
   }
}
