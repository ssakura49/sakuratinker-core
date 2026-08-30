package com.ssakura49.sakuratinker.core.library.registry;

import com.google.common.collect.ImmutableSet;
import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.core.data.condition.CompatConfigCondition;
import com.ssakura49.sakuratinker.core.data.condition.GeneralMaterialConfigCondition;
import com.ssakura49.sakuratinker.core.init.STCrCondition;
import com.ssakura49.sakuratinker.core.library.registry.record.MaterialModifiersObject;
import com.ssakura49.sakuratinker.core.library.registry.record.MaterialStatsObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.common.crafting.conditions.AndCondition;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;
import slimeknights.mantle.recipe.condition.TagFilledCondition;
import slimeknights.mantle.recipe.data.AbstractRecipeBuilder;
import slimeknights.mantle.recipe.data.IRecipeHelper;
import slimeknights.mantle.recipe.helper.FluidOutput;
import slimeknights.mantle.recipe.helper.ItemOutput;
import slimeknights.mantle.recipe.ingredient.FluidIngredient;
import slimeknights.mantle.registration.deferred.FluidDeferredRegister;
import slimeknights.mantle.registration.object.FluidObject;
import slimeknights.mantle.registration.object.ItemObject;
import slimeknights.tconstruct.common.registration.CastItemObject;
import slimeknights.tconstruct.library.client.data.spritetransformer.IColorMapping;
import slimeknights.tconstruct.library.client.data.spritetransformer.ISpriteTransformer;
import slimeknights.tconstruct.library.client.data.spritetransformer.RecolorSpriteTransformer;
import slimeknights.tconstruct.library.client.materials.MaterialRenderInfo;
import slimeknights.tconstruct.library.materials.definition.MaterialId;
import slimeknights.tconstruct.library.materials.stats.IMaterialStats;
import slimeknights.tconstruct.library.materials.stats.MaterialStatType;
import slimeknights.tconstruct.library.materials.stats.MaterialStatsId;
import slimeknights.tconstruct.library.modifiers.ModifierEntry;
import slimeknights.tconstruct.library.recipe.alloying.AlloyRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.ItemCastingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.casting.material.MaterialFluidRecipeBuilder;
import slimeknights.tconstruct.library.recipe.material.MaterialRecipeBuilder;
import slimeknights.tconstruct.library.recipe.melting.MaterialMeltingRecipeBuilder;
import slimeknights.tconstruct.library.recipe.melting.MeltingRecipeBuilder;
import slimeknights.tconstruct.smeltery.TinkerSmeltery;
import slimeknights.tconstruct.tools.data.sprite.TinkerPartSpriteProvider;
import slimeknights.tconstruct.tools.stats.GripMaterialStats;
import slimeknights.tconstruct.tools.stats.HandleMaterialStats;
import slimeknights.tconstruct.tools.stats.HeadMaterialStats;
import slimeknights.tconstruct.tools.stats.LimbMaterialStats;
import slimeknights.tconstruct.tools.stats.PlatingMaterialStats;
import slimeknights.tconstruct.tools.stats.StatlessMaterialStats;

import static slimeknights.tconstruct.fluids.block.BurningLiquidBlock.createBurning;

public class SimpleMaterialObject {
   public SimpleMaterialObject(String name, String nameSpace, @Nullable FluidObject<? extends ForgeFlowingFluid> fluidObject, boolean isUnit,
                               @Nullable ItemObject<? extends Item> itemObject, MaterialInfo materialInfo, ICondition condition,
                               boolean isSimpleModel, MaterialRenderInfo renderInfo, MaterialSpriteInfo spriteInfo) {
      this.name = name;
      this.nameSpace = nameSpace;
      this.fluidObject = fluidObject;
      this.itemObject = itemObject;
      this.materialInfo = materialInfo;
      this.condition = condition;
      this.isUnit = isUnit;
      this.isSimpleModel = isSimpleModel;
      this.renderInfo = renderInfo;
      this.spriteInfo = spriteInfo;
   }

   @Getter
   private final String name;
   @Getter
   private final String nameSpace;
   @Getter
   private final boolean isSimpleModel;

   //常量
   public final ResourceLocation baseFolder(){
      return ResourceLocation.fromNamespaceAndPath(this.nameSpace,"materials/");
   }
   public ResourceLocation getFolder(){
      return ResourceLocation.tryParse(baseFolder()+name+"/"+name);
   }
   public MaterialId getMaterialId(){
      return new MaterialId(SakuraTinker.getResource(name));
   }

   //注册物
   @Getter
   private final @Nullable FluidObject<? extends ForgeFlowingFluid> fluidObject;
   @Getter
   private boolean isFluidTextureCustom = false;
   @Getter
   private final @Nullable ItemObject<? extends Item> itemObject;

   //配方相关
   @Getter
   private @Nullable ICondition condition;
   @Getter
   private final boolean isUnit;
   @Getter
   private MaterialRecipeInfo recipeInfo;

   //材料相关
   @Getter
   private final MaterialInfo materialInfo;
   @Getter
   private final MaterialRenderInfo renderInfo;
   @Getter
   private final MaterialSpriteInfo spriteInfo;



   public void assembleRecipes(Consumer<FinishedRecipe> consumer, IRecipeHelper helper){
      if (this.recipeInfo==null) return;
      Consumer<FinishedRecipe> withCondition = helper.withCondition(consumer,condition);
      if (recipeInfo.hasMolten()&&recipeInfo.hasItem()){
         var con = recipeInfo.hasItemTag()? helper.withCondition(consumer,new AndCondition(condition,new TagFilledCondition<>(recipeInfo.getItemPair().getA().get()))):withCondition;
         MeltingRecipeBuilder.melting(recipeInfo.getItemIng(), recipeInfo.getFluidOutput(),recipeInfo.getMeltTemp(),1f)
                 .save(con,ResourceLocation.parse(getFolder()+"_melting_"+recipeInfo.getFluidAmount()+"mb"));
         recipeInfo.getCastItemOptional().ifPresentOrElse(castItemObject -> {

            var recipeBuilder = recipeInfo.isBasin ? ItemCastingRecipeBuilder.basinRecipe(recipeInfo.getItemOutput()) :
                    ItemCastingRecipeBuilder.tableRecipe(recipeInfo.getItemOutput());
            recipeBuilder.setCast(castItemObject.getMultiUseTag(), false)
                    .setFluid(recipeInfo.getFluidIng()).setCoolingTime(recipeInfo.meltTemp, recipeInfo.fluidAmount)
                    .save(con, ResourceLocation.parse(getFolder() + "_casting_multi_" + recipeInfo.getFluidAmount() + "mb"));

            recipeBuilder = recipeInfo.isBasin ? ItemCastingRecipeBuilder.basinRecipe(recipeInfo.getItemOutput()) :
                    ItemCastingRecipeBuilder.tableRecipe(recipeInfo.getItemOutput());
            recipeBuilder.setCast(castItemObject.getSingleUseTag(), true)
                    .setFluid(recipeInfo.getFluidIng()).setCoolingTime(recipeInfo.meltTemp, recipeInfo.fluidAmount)
                    .save(con, ResourceLocation.parse(getFolder() + "_casting_single_" + recipeInfo.getFluidAmount() + "mb"));
         },()->{
            var recipeBuilder = recipeInfo.isBasin?ItemCastingRecipeBuilder.basinRecipe(recipeInfo.getItemOutput()):
                    ItemCastingRecipeBuilder.tableRecipe(recipeInfo.getItemOutput());
            recipeBuilder.setFluid(recipeInfo.getFluidIng()).setCoolingTime(recipeInfo.meltTemp, recipeInfo.fluidAmount)
                    .save(con, ResourceLocation.parse(getFolder()+"_casting_none_"+recipeInfo.getFluidAmount()+"mb"));
         });
         if (isUnit) {
            MaterialMeltingRecipeBuilder.material(getMaterialId(), recipeInfo.getMeltTemp(), recipeInfo.getFluidOutput())
                    .save(withCondition, ResourceLocation.parse(getFolder() + "_material_melting"));
            MaterialFluidRecipeBuilder.material(getMaterialId()).setFluid(recipeInfo.getFluidIng()).setTemperature(recipeInfo.getMeltTemp())
                    .save(withCondition, ResourceLocation.parse(getFolder() + "_material_fluid"));
         }
      }
      if (recipeInfo.hasItem()) {
         var builder = MaterialRecipeBuilder.materialRecipe(getMaterialId()).setIngredient(recipeInfo.getItemIng())
                 .setNeeded(recipeInfo.getRequiredValue()).setValue(recipeInfo.getMaterialValue());
         if (recipeInfo.hasLeftOver()) {
            builder.setLeftover(recipeInfo.getLeftoverOutput());
         }
         builder.save(withCondition,
                 ResourceLocation.parse(getFolder() + "_material_" + recipeInfo.getMaterialValue() + recipeInfo.getRequiredValue()));
         int id = 1;
         for (var materialRecipe:recipeInfo.extraMaterials.keySet()){
            Consumer<FinishedRecipe> con;
            if (recipeInfo.extraMaterials.get(materialRecipe)!=null)
               con = helper.withCondition(consumer,new AndCondition(condition,recipeInfo.extraMaterials.get(materialRecipe)));
            else con = withCondition;
            materialRecipe.save(con,
                    ResourceLocation.parse(getFolder() + "_material_extra_" + id));
            id++;
         }
      }
      int id = 1;
      for (var alloyRecipe:recipeInfo.alloyRecipes.keySet()){
         Consumer<FinishedRecipe> con;
         if (recipeInfo.alloyRecipes.get(alloyRecipe)!=null)
            con = helper.withCondition(consumer,new AndCondition(condition,recipeInfo.alloyRecipes.get(alloyRecipe)));
         else con = withCondition;
         alloyRecipe.save(con, ResourceLocation.parse(getFolder() + "_material_alloy_"+id));
         id++;
      }
   }

   protected void mergeCondition(ICondition condition){
      if (this.condition==null) this.condition = condition;
      else this.condition = new AndCondition(this.condition,condition);
   }
   protected void setConditions(ICondition... conditions){
      this.condition = new AndCondition(conditions);
   }

   public MaterialRecipeInfo.MaterialRecipeInfoBuilder addRecipeInfo(){
      return new MaterialRecipeInfo.MaterialRecipeInfoBuilder(this);
   }

   public SimpleMaterialObject setFluidCustomTexture(){
      this.isFluidTextureCustom = true;
      return this;
   }


   //材料相关
   public static class MaterialInfo{
      @Getter
      private final boolean excludeFromTool;
      @Getter
      private final int tier;
      @Getter
      private final boolean isHidden;
      @Getter
      private final int sortOrder;
      @Getter
      private final boolean craftable;
      @Getter
      private final MaterialStatsObject stats;
      @Getter
      private final MaterialModifiersObject modifiers;
      @Getter
      private final boolean isOriginal;
      @Getter
      private final String compatModId;

      public MaterialInfo(boolean excludeFromTool, int tier, boolean isHidden, int sortOrder, boolean craftable, MaterialStatsObject stats, MaterialModifiersObject modifiers, boolean isOriginal, String compatModId) {
         this.excludeFromTool = excludeFromTool;
         this.tier = tier;
         this.isHidden = isHidden;
         this.sortOrder = sortOrder;
         this.craftable = craftable;
         this.stats = stats;
         this.modifiers = modifiers;
         this.isOriginal = isOriginal;
         this.compatModId = compatModId;
      }
      public static class MaterialInfoBuilder{
         Builder parent;
         int tier;
         boolean isHidden;
         int sortOrder;
         boolean craftable;
         MaterialStatsObject stats;
         MaterialModifiersObject modifiers;
         boolean isOriginal;
         String compatModId;
         boolean excludeFromTool = false;
         public MaterialInfoBuilder(int tier,boolean isHidden,int sortOrder,boolean craftable,Builder parent){
            this.tier = tier;
            this.isHidden = isHidden;
            this.sortOrder = sortOrder;
            this.craftable = craftable;
            this.parent = parent;
         }
         public MaterialInfoBuilder setOriginal(){
            this.isOriginal = true;
            return this;
         }
         public MaterialInfoBuilder setExcludeFromAncients(){
            this.excludeFromTool = true;
            return this;
         }
         public MaterialInfoBuilder setCompatModId(String id){
            this.compatModId = id;
            return this;
         }
         public MaterialStatsObjectBuilder buildStats(){
            return new MaterialStatsObjectBuilder(this);
         }
         public MaterialModifiersObjectBuilder buildModifiers(){
            return new MaterialModifiersObjectBuilder(this);
         }
         public Builder build(){
            parent.materialInfo = new MaterialInfo(this.excludeFromTool,this.tier,this.isHidden,this.sortOrder,this.craftable,
                    this.stats,this.modifiers,this.isOriginal,this.compatModId);
            return parent;
         }
         public static class MaterialStatsObjectBuilder {
            IMaterialStats[] stats;
            @Nullable PlatingMaterialStats.Builder armorBuilder;
            final MaterialInfoBuilder parent;
            boolean allowShield = false;
            protected MaterialStatsObjectBuilder(MaterialInfoBuilder parent) {
               this.parent = parent;
            }
            public MaterialStatsObjectBuilder setStats(IMaterialStats... stats) {
               this.stats = stats;
               return this;
            }
            public MaterialStatsObjectBuilder setAllowShield() {
               this.allowShield = true;
               return this;
            }
            public MaterialStatsObjectBuilder setArmorBuilder(PlatingMaterialStats.Builder builder) {
               this.armorBuilder = builder;
               return this;
            }
            public MaterialInfoBuilder build() {
               this.parent.stats = new MaterialStatsObject(this.stats, this.armorBuilder, this.allowShield);
               return this.parent;
            }
         }
      }
      public static class MaterialModifiersObjectBuilder{
         final MaterialInfoBuilder parent;
         List<Pair<@Nullable MaterialStatsId, ModifierEntry[]>> list = new ArrayList<>();
         public MaterialModifiersObjectBuilder(MaterialInfoBuilder parent){
            this.parent = parent;
         }
         public MaterialModifiersObjectBuilder buildDefault(ModifierEntry... entries){
            list.add(new Pair<>(null,entries));
            return this;
         }
         public MaterialModifiersObjectBuilder buildPerStat(MaterialStatsId id,ModifierEntry... entries){
            list.add(new Pair<>(id,entries));
            return this;
         }
         public MaterialInfoBuilder build(){
            this.parent.modifiers = new MaterialModifiersObject(List.copyOf(list));
            return this.parent;
         }
      }
   }
   public static class MaterialSpriteInfo{
      @Getter
      private final ResourceLocation texture;
      @Getter
      private final String[] fallbacks;
      @Getter
      private final Set<MaterialStatsId> supportedStats;
      @Getter
      @Nullable
      private final ISpriteTransformer transformer;
      @Getter
      private boolean variant = false;

      public MaterialSpriteInfo(ResourceLocation texture, String[] fallbacks, Set<MaterialStatsId> supportedStats, @Nullable ISpriteTransformer transformer,boolean variant) {
         this.texture = texture;
         this.fallbacks = fallbacks;
         this.supportedStats = supportedStats;
         this.transformer = transformer;
         this.variant = variant;
      }
   }
   public static class MaterialSpriteInfoBuilder {
      private static final String[] EMPTY_STRING_ARRAY = new String[0];
      private final ResourceLocation texture;
      private String[] fallbacks = EMPTY_STRING_ARRAY;
      private final ImmutableSet.Builder<MaterialStatsId> statTypes = ImmutableSet.builder();
      private final Builder parent;
      @Nullable
      private ISpriteTransformer transformer;
      @Setter
      private boolean variant = false;

      public MaterialSpriteInfoBuilder(ResourceLocation texture, Builder parent) {
         this.texture = texture;
         this.parent = parent;
      }

      public MaterialSpriteInfoBuilder fallbacks(String... fallbacks) {
         this.fallbacks = fallbacks;
         return this;
      }

      public MaterialSpriteInfoBuilder colorMapper(IColorMapping mapping) {
         return this.setTransformer(new RecolorSpriteTransformer(mapping));
      }
      public MaterialSpriteInfoBuilder setTransformer(ISpriteTransformer transformer) {
         this.transformer = transformer;
         return this;
      }

      public MaterialSpriteInfoBuilder variant() {
         this.setVariant(true);
         return this;
      }

      public MaterialSpriteInfoBuilder statType(MaterialStatsId statsId) {
         statTypes.add(statsId);
         return this;
      }

      public MaterialSpriteInfoBuilder statType(MaterialStatsId... statsId) {
         statTypes.add(statsId);
         return this;
      }

      public MaterialSpriteInfoBuilder statType(IMaterialStats... stats) {
         for (IMaterialStats stat : stats) {
            statTypes.add(stat.getIdentifier());
         }
         return this;
      }

      public MaterialSpriteInfoBuilder statType(MaterialStatType<?>... stats) {
         for (MaterialStatType<?> stat : stats) {
            statTypes.add(stat.getId());
         }
         return this;
      }

      public MaterialSpriteInfoBuilder statType(List<? extends MaterialStatType<?>> stats) {
         for (MaterialStatType<?> stat : stats) {
            statTypes.add(stat.getId());
         }
         return this;
      }

      public MaterialSpriteInfoBuilder repairKit() {
         return statType(StatlessMaterialStats.REPAIR_KIT.getIdentifier());
      }

      public MaterialSpriteInfoBuilder meleeHarvest() {
         statType(HeadMaterialStats.ID);
         statType(HandleMaterialStats.ID);
         statType(StatlessMaterialStats.BINDING.getIdentifier());
         repairKit();
         return this;
      }

      public MaterialSpriteInfoBuilder ranged() {
         statType(LimbMaterialStats.ID);
         statType(GripMaterialStats.ID);
         repairKit();
         return this;
      }

      public MaterialSpriteInfoBuilder maille() {
         statType(StatlessMaterialStats.MAILLE.getIdentifier());
         statType(TinkerPartSpriteProvider.ARMOR_MAILLE);
         return this;
      }

      public MaterialSpriteInfoBuilder cuirass() {
         statType(StatlessMaterialStats.CUIRASS.getIdentifier());
         statType(TinkerPartSpriteProvider.ARMOR_CUIRASS);
         repairKit(); // used by traveler's gear
         return this;
      }

      public MaterialSpriteInfoBuilder plating() {
         statType(TinkerPartSpriteProvider.ARMOR_PLATING);
         for (MaterialStatType<?> type : PlatingMaterialStats.TYPES) {
            statType(type.getId());
         }
         repairKit();
         return this;
      }

      public MaterialSpriteInfoBuilder armor() {
         plating();
         maille();
         return this;
      }

      public MaterialSpriteInfoBuilder shieldCore() {
         statType(StatlessMaterialStats.SHIELD_CORE);
         repairKit(); // used by traveler's shields
         return this;
      }

      public MaterialSpriteInfoBuilder arrowHead() {
         statType(StatlessMaterialStats.ARROW_HEAD);
         return this;
      }

      public MaterialSpriteInfoBuilder arrowShaft() {
         statType(StatlessMaterialStats.ARROW_SHAFT);
         return this;
      }

      public MaterialSpriteInfoBuilder fletching() {
         statType(StatlessMaterialStats.FLETCHING);
         return this;
      }

      public Builder build() {
         if (transformer == null) {
            throw new IllegalStateException("Material must have a transformer for a sprite provider");
         }
         Set<MaterialStatsId> supportedStats = this.statTypes.build();
         if (supportedStats.isEmpty()) {
            throw new IllegalStateException("Material must support at least one stat type");
         }
         this.parent.spriteInfo = new MaterialSpriteInfo(this.texture,this.fallbacks,supportedStats,this.transformer,this.variant);
         return this.parent;
      }
   }

   //材料配方相关
   public static class MaterialRecipeInfo{
      @Getter
      private final Pair<Optional<TagKey<Item>>, ItemLike> itemPair;
      @Getter
      private final Pair<Optional<TagKey<Fluid>>,Supplier<Fluid>> fluidPair;
      @Getter
      private final Optional<CastItemObject> castItemOptional;
      @Getter
      private final Pair<Optional<TagKey<Item>>, ItemLike> leftOverPair;
      @Getter
      private final int meltTemp;
      @Getter
      private final int fluidAmount;
      @Getter
      private final int materialValue;
      @Getter
      private final int requiredValue;
      @Getter
      private final Map<AlloyRecipeBuilder,ICondition> alloyRecipes = new HashMap<>();
      @Getter
      private final Map<AbstractRecipeBuilder<?>,ICondition> extraMaterials = new HashMap<>();
      @Getter
      private final boolean isBasin;

      public MaterialRecipeInfo(Pair<Optional<TagKey<Item>>, ItemLike> itemPair, Pair<Optional<TagKey<Fluid>>, Supplier<Fluid>> fluidPair, Optional<CastItemObject> castItemOptional, Pair<Optional<TagKey<Item>>, ItemLike> leftOverPair, int temp, int amount, int value, int requiredValue, boolean isBasin) {
         this.itemPair = itemPair;
         this.fluidPair = fluidPair;
         this.castItemOptional = castItemOptional;
         this.leftOverPair = leftOverPair;
         meltTemp = temp;
         fluidAmount = amount;
         materialValue = value;
         this.requiredValue = requiredValue;
         this.isBasin = isBasin;
      }

      public Ingredient getItemIng(){
         return itemPair.getA().map(Ingredient::of).orElseGet(() -> Ingredient.of(itemPair.getB()));
      }
      public ItemOutput getItemOutput(){
         return itemPair.getA().map(ItemOutput::fromTag).orElseGet(() -> ItemOutput.fromItem(itemPair.getB()));
      }
      public Ingredient getLeftoverIng(){
         return leftOverPair.getA().map(Ingredient::of).orElseGet(() -> Ingredient.of(leftOverPair.getB()));
      }
      public ItemOutput getLeftoverOutput(){
         return leftOverPair.getA().map(ItemOutput::fromTag).orElseGet(() -> ItemOutput.fromItem(leftOverPair.getB()));
      }
      public FluidIngredient getFluidIng(){
         if (!hasMolten()) throw new NullPointerException("Tried Fetching fluid when a material don't have molten form");
         return fluidPair.getA().map(fluidTagKey -> (FluidIngredient) FluidIngredient.of(fluidTagKey,fluidAmount))
                 .orElseGet(()->FluidIngredient.of(new FluidStack(fluidPair.getB().get(),fluidAmount)));
      }
      public FluidIngredient getFluidIng(int amount){
         if (!hasMolten()) throw new NullPointerException("Tried Fetching fluid when a material don't have molten form");
         return fluidPair.getA().map(fluidTagKey -> (FluidIngredient) FluidIngredient.of(fluidTagKey,amount))
                 .orElseGet(()->FluidIngredient.of(new FluidStack(fluidPair.getB().get(),amount)));
      }
      public FluidOutput getFluidOutput(){
         if (!hasMolten()) throw new NullPointerException("Tried Fetching fluid when a material don't have molten form");
         return fluidPair.getA().map(fluidTagKey -> FluidOutput.fromTag(fluidTagKey,fluidAmount))
                 .orElseGet(() -> FluidOutput.fromFluid(fluidPair.getB().get(),fluidAmount));
      }
      public FluidOutput getFluidOutput(int amount){
         if (!hasMolten()) throw new NullPointerException("Tried Fetching fluid when a material don't have molten form");
         return fluidPair.getA().map(fluidTagKey -> FluidOutput.fromTag(fluidTagKey,amount))
                 .orElseGet(() -> FluidOutput.fromFluid(fluidPair.getB().get(),amount));
      }
      public boolean hasMolten(){
         return fluidPair.getA().isPresent()||fluidPair.getB().get()!=null;
      }
      public boolean hasItemTag(){
         return itemPair.getA().isPresent();
      }
      public boolean hasItem(){
         return itemPair.getA().isPresent()||itemPair.getB()!=null;
      }
      public boolean hasLeftOver(){
         return leftOverPair.getA().isPresent()||leftOverPair.getB()!=null;
      }
      public MaterialRecipeInfo addAlloyRecipes(AlloyRecipeBuilder... alloyRecipeBuilders){
         Arrays.stream(alloyRecipeBuilders).forEach(builder->this.alloyRecipes.put(builder,null));
         return this;
      }
      public MaterialRecipeInfo addExtraMaterials(MaterialRecipeBuilder... materialRecipeBuilders){
         Arrays.stream(materialRecipeBuilders).forEach(builder->this.extraMaterials.put(builder,null));
         return this;
      }
      public MaterialRecipeInfo addMetalNugget(@Nullable TagKey<Item> itemTag, @Nullable Supplier<Item> supplier, SimpleMaterialObject object,boolean alreadyHasMoltenRecipes){
         if (itemTag==null&&supplier==null) return this;
         var itemIng = itemTag==null?Ingredient.of(supplier.get()):Ingredient.of(itemTag);
         this.extraMaterials.put(
                 MaterialRecipeBuilder.materialRecipe(object.getMaterialId()).setNeeded(9)
                         .setValue(1).setIngredient(itemIng),itemTag!=null? new TagFilledCondition<>(itemTag):null
         );
         if (this.hasMolten()&&!alreadyHasMoltenRecipes){
            this.extraMaterials.put(
                    MeltingRecipeBuilder.melting(itemIng, getFluidOutput(10),getMeltTemp(),1f),
                    itemTag!=null? new TagFilledCondition<>(itemTag):null
            );
            this.extraMaterials.put(
                    ItemCastingRecipeBuilder.tableRecipe(itemTag==null?ItemOutput.fromItem(supplier.get()):ItemOutput.fromTag(itemTag))
                            .setCoolingTime(getMeltTemp(),10).setFluid(getFluidIng(10))
                            .setCast(TinkerSmeltery.nuggetCast.getMultiUseTag(),false),
                    itemTag!=null? new TagFilledCondition<>(itemTag):null
            );
            this.extraMaterials.put(
                    ItemCastingRecipeBuilder.tableRecipe(itemTag==null?ItemOutput.fromItem(supplier.get()):ItemOutput.fromTag(itemTag))
                            .setCoolingTime(getMeltTemp(),10).setFluid(getFluidIng(10))
                            .setCast(TinkerSmeltery.nuggetCast.getSingleUseTag(),true),
                    itemTag!=null? new TagFilledCondition<>(itemTag):null
            );
         }
         return this;
      }
      public MaterialRecipeInfo addMetalBlock(@Nullable TagKey<Item> itemTag, @Nullable Supplier<Item> supplier, SimpleMaterialObject object,boolean alreadyHasMoltenRecipes){
         if (itemTag==null&&supplier==null) return this;
         var itemIng = itemTag==null?Ingredient.of(supplier.get()):Ingredient.of(itemTag);
         this.extraMaterials.put(
                 MaterialRecipeBuilder.materialRecipe(object.getMaterialId()).setNeeded(1)
                         .setValue(9).setIngredient(itemIng).setLeftover(getItemOutput()),
                 itemTag!=null? new TagFilledCondition<>(itemTag):null
         );
         if (this.hasMolten()&&!alreadyHasMoltenRecipes){
            this.extraMaterials.put(
                    MeltingRecipeBuilder.melting(itemIng, getFluidOutput(810),getMeltTemp(),1f),
                    itemTag!=null? new TagFilledCondition<>(itemTag):null
            );
            this.extraMaterials.put(
                    ItemCastingRecipeBuilder.basinRecipe(itemTag==null?ItemOutput.fromItem(supplier.get()):ItemOutput.fromTag(itemTag))
                            .setCoolingTime(getMeltTemp(),810).setFluid(getFluidIng(810)),
                    itemTag!=null? new TagFilledCondition<>(itemTag):null
            );
         }
         return this;
      }
      public MaterialRecipeInfo addGeneralRecipes(String name){
         if (this.hasMolten()){
            this.extraMaterials.put(
                    MeltingRecipeBuilder.melting(Ingredient.of(forgeTag(IngredientForm.dust,name)),
                            fluidPair.getA().map(fluidTagKey -> FluidOutput.fromTag(fluidTagKey,90))
                                    .orElseGet(() -> FluidOutput.fromFluid(fluidPair.getB().get(),90))
                            ,getMeltTemp(),1f),
                    new TagFilledCondition<>(forgeTag(IngredientForm.dust,name))
            );
            this.extraMaterials.put(
                    MeltingRecipeBuilder.melting(Ingredient.of(forgeTag(IngredientForm.plate,name)),
                            fluidPair.getA().map(fluidTagKey -> FluidOutput.fromTag(fluidTagKey,90))
                                    .orElseGet(() -> FluidOutput.fromFluid(fluidPair.getB().get(),90))
                            ,getMeltTemp(),1f),
                    new TagFilledCondition<>(forgeTag(IngredientForm.plate,name))
            );
            this.extraMaterials.put(
                    MeltingRecipeBuilder.melting(Ingredient.of(forgeTag(IngredientForm.gear,name)),
                            fluidPair.getA().map(fluidTagKey -> FluidOutput.fromTag(fluidTagKey,90))
                                    .orElseGet(() -> FluidOutput.fromFluid(fluidPair.getB().get(),90))
                            ,getMeltTemp(),1f),
                    new TagFilledCondition<>(forgeTag(IngredientForm.gear,name))
            );

            this.extraMaterials.put(
                    ItemCastingRecipeBuilder.tableRecipe(ItemOutput.fromTag(forgeTag(IngredientForm.plate,name)))
                            .setCoolingTime(getMeltTemp(),90).setFluid(getFluidIng(90))
                            .setCast(castTag(IngredientForm.plate,true),false),
                    new TagFilledCondition<>(forgeTag(IngredientForm.plate,name))
            );
            this.extraMaterials.put(
                    ItemCastingRecipeBuilder.tableRecipe(ItemOutput.fromTag(forgeTag(IngredientForm.plate,name)))
                            .setCoolingTime(getMeltTemp(),90).setFluid(getFluidIng(90))
                            .setCast(castTag(IngredientForm.plate,false),true),
                    new TagFilledCondition<>(forgeTag(IngredientForm.plate,name))
            );
            this.extraMaterials.put(
                    ItemCastingRecipeBuilder.tableRecipe(ItemOutput.fromTag(forgeTag(IngredientForm.gear,name)))
                            .setCoolingTime(getMeltTemp(),90).setFluid(getFluidIng(360))
                            .setCast(castTag(IngredientForm.gear,true),false),
                    new TagFilledCondition<>(forgeTag(IngredientForm.gear,name))
            );
            this.extraMaterials.put(
                    ItemCastingRecipeBuilder.tableRecipe(ItemOutput.fromTag(forgeTag(IngredientForm.gear,name)))
                            .setCoolingTime(getMeltTemp(),90).setFluid(getFluidIng(360))
                            .setCast(castTag(IngredientForm.gear,false),true),
                    new TagFilledCondition<>(forgeTag(IngredientForm.gear,name))
            );
         }
         return this;
      }
      private static TagKey<Item> forgeTag(IngredientForm form,String name){
         return TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(),ResourceLocation.fromNamespaceAndPath("forge",form+"s/"+name));
      }
      private static TagKey<Item> castTag(IngredientForm form,boolean multi){
         return TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(),ResourceLocation.fromNamespaceAndPath("tconstruct","casts/"+(multi?"multi_use":"single_use")+"/"+form));
      }
      public static class MaterialRecipeInfoBuilder{
         public MaterialRecipeInfoBuilder(SimpleMaterialObject materialObject){
            this.simpleMaterialObject = materialObject;
            this.itemPair = new Pair<>(Optional.empty(),simpleMaterialObject.itemObject);
            this.fluidPair = new Pair<>(Optional.empty(),()->{
               if (simpleMaterialObject.fluidObject != null) return simpleMaterialObject.fluidObject.get();
               return null;
            });
            this.leftOverPair = new Pair<>(Optional.empty(),simpleMaterialObject.itemObject);
            this.castItemOptional = Optional.empty();
            this.materialValue = 1;
            this.requiredValue = 1;
         }
         SimpleMaterialObject simpleMaterialObject;
         Pair<Optional<TagKey<Item>>, ItemLike> itemPair;
         Pair<Optional<TagKey<Fluid>>,Supplier<Fluid>> fluidPair;
         Optional<CastItemObject> castItemOptional;
         Pair<Optional<TagKey<Item>>, ItemLike> leftOverPair;
         int meltTemp;
         int fluidAmount;
         int materialValue;
         int requiredValue;
         boolean isBasin;
         private FluidOutput getFluidOutput(){
            return fluidPair.getA().map(fluidTagKey -> FluidOutput.fromTag(fluidTagKey,fluidAmount))
                    .orElseGet(() -> FluidOutput.fromFluid(fluidPair.getB().get(),fluidAmount));
         }
         public MaterialRecipeInfoBuilder setItemIn(@Nullable TagKey<Item> tag,@Nullable ItemLike item){
            this.itemPair = new Pair<>(Optional.ofNullable(tag),item);
            return this;
         }
         public MaterialRecipeInfoBuilder setFluidIn(@Nullable TagKey<Fluid> tag,@Nullable Supplier<Fluid> fluid){
            this.fluidPair = new Pair<>(Optional.ofNullable(tag),fluid);
            return this;
         }
         public MaterialRecipeInfoBuilder setFluidIn(@Nullable TagKey<Fluid> tag,
                                                     @NotNull RegistryObject<? extends Fluid> fluid){
            this.fluidPair = new Pair<>(Optional.ofNullable(tag),()->fluid.orElse(null));
            return this;
         }
         public MaterialRecipeInfoBuilder setCast(Optional<CastItemObject> castItemOptional){
            this.castItemOptional = castItemOptional;
            return this;
         }
         public MaterialRecipeInfoBuilder setIngot(){
            this.castItemOptional =Optional.of(TinkerSmeltery.ingotCast);
            return this;
         }
         public MaterialRecipeInfoBuilder setGem(){
            this.castItemOptional =Optional.of(TinkerSmeltery.gemCast);
            return this;
         }
         public MaterialRecipeInfoBuilder setLeftover(@Nullable TagKey<Item> tag,@Nullable ItemLike item){
            this.leftOverPair = new Pair<>(Optional.ofNullable(tag),item);
            return this;
         }
         public MaterialRecipeInfoBuilder setTemp(int temp){
            this.meltTemp = temp;
            return this;
         }
         public MaterialRecipeInfoBuilder setFluidAmount(int amount){
            this.fluidAmount = amount;
            return this;
         }
         public MaterialRecipeInfoBuilder setMaterialValue(int value,int required){
            this.materialValue = value;
            this.requiredValue = required;
            return this;
         }
         public MaterialRecipeInfoBuilder setBasin(){
            this.isBasin = true;
            return this;
         }
         public SimpleMaterialObject build(){
            simpleMaterialObject.recipeInfo = new MaterialRecipeInfo(this.itemPair,this.fluidPair,
                    this.castItemOptional,this.leftOverPair,this.meltTemp,this.fluidAmount,this.materialValue,
                    this.requiredValue,isBasin);
            return simpleMaterialObject;
         }
      }
   }

   public static class Builder{
      SimpleMaterialWrappedRegister register;
      String name;
      String nameSpace;
      Supplier<? extends Item> itemSupplier;
      MaterialInfo materialInfo;
      boolean isUnit;
      @Getter
      boolean isOriginal;
      @Getter
      List<String> compatModIds = new ArrayList<>();
      List<ICondition> conditions = new ArrayList<>();
      String itemName;
      String fluidName;
      boolean isSimpleModel = true;
      MaterialRenderInfo renderInfo;
      MaterialSpriteInfo spriteInfo;
      Function<FluidDeferredRegister.Builder, FluidObject<? extends ForgeFlowingFluid>> fluidRegFunction;
      public Builder(String nameSpace,String name,SimpleMaterialWrappedRegister register){
         this.name = name;
         this.nameSpace = nameSpace;
         this.register = register;
         this.fluidName = "molten_"+name;
      }
      public Builder registerItem(Supplier<? extends Item> supplier){
         this.itemSupplier = supplier;
         return this;
      }
      public Builder setUnit(){
         this.isUnit = true;
         return this;
      }
      public Builder setItemIngot(){
         this.itemName = name+"_ingot";
         return this;
      }
      public Builder commonCompat(){
         this.conditions.add(new CompatConfigCondition(SakuraTinker.MODID,false));
         return this.setOriginal(false);
      }
      public Builder noCompat(){
         this.conditions.add(new CompatConfigCondition(SakuraTinker.MODID,true));
         return this.setOriginal(true);
      }
      public Builder addCompatModId(String modId,boolean isOriginal){
         this.compatModIds.add(modId);
         this.conditions.add(new ModLoadedCondition(modId));
         this.conditions.add(new CompatConfigCondition(modId,isOriginal));
         return this.setOriginal(isOriginal);
      }
      private Builder setOriginal(boolean b){
         this.isOriginal = b;
         conditions.add(STCrCondition.ALLOW_ORIGINAL_MATERIALS);
         return this;
      }
      public Builder setGeneral(String name){
         this.conditions.add(new GeneralMaterialConfigCondition(name));
         return this;
      }
      public Builder setItemName(String itemName){
         this.itemName = itemName;
         return this;
      }
      public Builder registerBurningFluid(int temp,boolean gas,int lightLevel,int burnTime,int damage){
         this.fluidRegFunction = builder -> {
            builder.type(hot(fluidName,temp,gas)).bucket().block(createBurning(MapColor.COLOR_GRAY,lightLevel,burnTime,damage)).commonTag();
            return gas?builder.invertedFlowing():builder.flowing();
         };
         return this;
      }
      public Builder registerFluid(int temp,boolean gas,Function<Supplier<? extends FlowingFluid>, LiquidBlock> blockFunction){
         this.fluidRegFunction = builder -> {
            builder.type(hot(fluidName,temp,gas)).bucket().block(blockFunction).commonTag();
            return gas?builder.invertedFlowing():builder.flowing();
         };
         return this;
      }
      public Builder setFluidName(String fluidName){
         this.fluidName = fluidName;
         return this;
      }
      public Builder addConditions(ICondition... conditions){
         this.conditions.addAll(Arrays.stream(conditions).toList());
         return this;
      }
      public Builder setCustomModel(){
         this.isSimpleModel = false;
         return this;
      }
      public MaterialInfo.MaterialInfoBuilder buildMaterial(int tier,boolean isHidden,int sortOrder,boolean craftable){
         return new MaterialInfo.MaterialInfoBuilder(tier,isHidden,sortOrder,craftable,this);
      }
      public MaterialSpriteInfoBuilder buildMaterialSprite(){
         return new MaterialSpriteInfoBuilder(new MaterialId(nameSpace,name),this);
      }
      public Builder setRenderInfo(MaterialRenderInfo info){
         this.renderInfo = info;
         return this;
      }
      public Builder setRenderInfo(Function<MaterialId,MaterialRenderInfo> function){
         this.renderInfo = function.apply(new MaterialId(nameSpace,name));
         return this;
      }
      public SimpleMaterialObject build(){
         SimpleMaterialObject object;
         if (this.itemName==null) this.itemName = name;
         if (this.fluidName==null) this.fluidName = name;
         AtomicBoolean isLoaded = new AtomicBoolean(true);
         compatModIds.forEach(s -> {
            if (isLoaded.get()&&!ModList.get().isLoaded(s)) isLoaded.set(false);
         });
         if (isLoaded.get()){
            ItemObject<Item> itemObj = null;
            if (itemSupplier!=null) itemObj = register.itemRegister.register(itemName,itemSupplier);
            FluidObject<? extends ForgeFlowingFluid> fluidObj = null;
            if (fluidRegFunction!=null) fluidObj = fluidRegFunction.apply(register.fluidRegister.register(fluidName));
            object = new SimpleMaterialObject(this.name, this.nameSpace, fluidObj,this.isUnit, itemObj, materialInfo,
                    new AndCondition(conditions.toArray(new ICondition[0])),this.isSimpleModel,this.renderInfo,this.spriteInfo);
            register.entryMap.put(name,object);
            return object;
         }
         object = new SimpleMaterialObject(this.name,this.nameSpace,null,this.isUnit,null,this.materialInfo,
                 new AndCondition(conditions.toArray(new ICondition[0])),this.isSimpleModel,this.renderInfo,this.spriteInfo);
         register.entryMap.put(name,object);
         return object;
      }
      protected static FluidType.Properties hot(String name, int Temp, boolean gas) {
         return FluidType.Properties.create().density(gas?-2000:2000).viscosity(10000).temperature(Temp)
                 .descriptionId("fluid."+SakuraTinker.MODID+"."+name)
                 .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                 .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                 .motionScale(0.0023333333333333335D)
                 .canSwim(false).canDrown(false)
                 .pathType(BlockPathTypes.LAVA).adjacentPathType(null);
      }
   }

   public static enum IngredientForm{
      nugget, ingot,storage_block, plate, dust, gear;
   }
}
