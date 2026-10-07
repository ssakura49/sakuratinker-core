package com.ssakura49.sakuratinker.core.data.provider;

import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.core.init.STCrItem;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.data.recipe.ISmelteryRecipeHelper;
import slimeknights.tconstruct.shared.TinkerCommons;
import slimeknights.tconstruct.shared.TinkerMaterials;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;

import java.util.function.Consumer;

public class STCrRecipeProvider extends RecipeProvider implements ISmelteryRecipeHelper {
    public STCrRecipeProvider(PackOutput output) {
        super(output);
    }
    public static final ResourceLocation baseFolder = ResourceLocation.fromNamespaceAndPath(SakuraTinker.MODID,"core/");
    public static ResourceLocation namedFolder(String name){
        return ResourceLocation.tryParse(baseFolder+name+"/"+name);
    }
    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> consumer) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, STCrItem.BOOK.get())
                .unlockedBy("has_item",has(Items.BOOK))
                .requires(Items.BOOK).requires(TinkerMaterials.roseGold).requires(Items.CHERRY_LEAVES)
                .save(consumer,ResourceLocation.parse(baseFolder +"soul_sakura_book"));
    }

    @Override
    public @NotNull String getModId() {
        return SakuraTinker.MODID;
    }
}
