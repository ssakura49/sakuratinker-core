package com.ssakura49.sakuratinker.core.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;

public class Slaughter extends SwordItem {
   public Slaughter(Item.Properties pProperties) {
      super(Tiers.NETHERITE, 100, 1.5F, pProperties);
   }

   public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
      if (entity.removalReason == null && !(entity instanceof LightningBolt)) {
         if (!(entity instanceof Player)) {
         }

         return false;
      } else {
         return false;
      }
   }
}
