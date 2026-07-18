package com.ssakura49.sakuratinker.core.content.tier;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public class InfinityTiers implements Tier {
   public static Tier instance = new InfinityTiers();

   private InfinityTiers() {
   }

   public int getUses() {
      return 999;
   }

   public float getSpeed() {
      return 99.0F;
   }

   public float getAttackDamageBonus() {
      return 9999.0F;
   }

   public int getLevel() {
      return 99;
   }

   public int getEnchantmentValue() {
      return 99;
   }

   public @NotNull Ingredient getRepairIngredient() {
      return Ingredient.EMPTY;
   }
}
