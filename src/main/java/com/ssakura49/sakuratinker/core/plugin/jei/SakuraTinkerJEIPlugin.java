package com.ssakura49.sakuratinker.core.plugin.jei;

import com.ssakura49.sakuratinker.core.content.recipes.SoulSakuraSealRecipe;
import java.util.List;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import slimeknights.mantle.recipe.helper.RecipeHelper;
import slimeknights.tconstruct.library.recipe.TinkerRecipeTypes;

@JeiPlugin
public class SakuraTinkerJEIPlugin implements IModPlugin {
   public @NotNull ResourceLocation getPluginUid() {
      return ResourceLocation.fromNamespaceAndPath("sakuratinker", "jei_plugin");
   }

   public void registerCategories(IRecipeCategoryRegistration registration) {
      registration.addRecipeCategories(new IRecipeCategory[]{new SealRecipeCategory(registration.getJeiHelpers().getGuiHelper())});
   }

   public void registerRecipes(@NotNull IRecipeRegistration registration) {
      Level level = Minecraft.getInstance().level;
      if (level != null) {
         RegistryAccess access = level.registryAccess();
         RecipeManager manager = level.getRecipeManager();
         List<SoulSakuraSealRecipe> recipes = RecipeHelper.getJEIRecipes(access, manager, TinkerRecipeTypes.TINKER_STATION.get(), SoulSakuraSealRecipe.class);
         registration.addRecipes(SealRecipeCategory.SEAL, recipes);
      }

   }
}
