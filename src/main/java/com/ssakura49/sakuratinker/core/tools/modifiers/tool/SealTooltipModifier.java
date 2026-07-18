package com.ssakura49.sakuratinker.core.tools.modifiers.tool;

import com.ssakura49.sakuratinker.core.content.recipes.SoulSakuraSealRecipe;
import com.ssakura49.sakuratinker.core.tools.modifiers.base.BaseModifier;
import com.ssakura49.sakuratinker.core.utils.component.DynamicComponentUtil;
import java.util.List;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;
import slimeknights.mantle.client.TooltipKey;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

public class SealTooltipModifier extends BaseModifier {
   public boolean isNoLevels() {
      return true;
   }

   public boolean shouldDisplay(boolean advanced) {
      return false;
   }

   public void addTooltip(IToolStackView tool, ModifierEntry entry, @Nullable Player player, List<Component> tooltip, TooltipKey keys, TooltipFlag flag) {
      ModDataNBT persistentData = tool.getPersistentData();
      if (persistentData.contains(SoulSakuraSealRecipe.SEAL_TOOLTIP_KEY, 99) && persistentData.getBoolean(SoulSakuraSealRecipe.SEAL_TOOLTIP_KEY)) {
         ListTag modifiersTag = (ListTag)persistentData.get(SoulSakuraSealRecipe.SEAL_MODIFIER_LIST, (compound, key) -> compound.getList(key, 8));
         if (!modifiersTag.isEmpty()) {
            StringBuilder modifiersBuilder = new StringBuilder();

            for(int i = 0; i < modifiersTag.size(); ++i) {
               if (i > 0) {
                  modifiersBuilder.append(", ");
               }

               modifiersBuilder.append(modifiersTag.getString(i));
            }

            int[] colors = new int[]{16733525, 16755200, 5635925, 5592575};
            Component colorfulText = DynamicComponentUtil.ScrollColorfulText.getColorfulText("已刻印: ", modifiersBuilder.toString(), colors, 60, 50, false);
            tooltip.add(colorfulText);
         }
      }

   }
}
