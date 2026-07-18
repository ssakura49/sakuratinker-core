package com.ssakura49.sakuratinker.core.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import slimeknights.mantle.item.LecternBookItem;

public class SoulSakuraBook extends LecternBookItem {
   public SoulSakuraBook(Item.Properties properties) {
      super(properties);
   }

   public void openLecternScreenClient(BlockPos pos, ItemStack book) {
   }
}
