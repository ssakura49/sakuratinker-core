package com.ssakura49.sakuratinker.core.init;

import com.ssakura49.sakuratinker.core.content.recipes.SoulSakuraSealRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.recipe.helper.SimpleRecipeSerializer;
import slimeknights.mantle.registration.deferred.SynchronizedDeferredRegister;

public class STCrRecipe {
   public static final SynchronizedDeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = SynchronizedDeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, "sakuratinker");
   public static final RegistryObject<RecipeSerializer<SoulSakuraSealRecipe>> SOUL_SAKURA_SEAL_RECIPE =
           RECIPE_SERIALIZERS.register("soul_sakura_seal", () ->
                   new SimpleRecipeSerializer<>(SoulSakuraSealRecipe::new));
}
