package com.ssakura49.sakuratinker.core.data.provider;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class STCrItemModelProvider extends ItemModelProvider {
   public static final String PARENT_SIMPLE_ITEM = "item/generated";
   public static final String PARENT_BUCKET_FLUID = "forge:item/bucket_drip";

   public STCrItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
      super(output, "sakuratinker", existingFileHelper);
   }

   public void generateItemModel(RegistryObject<Item> object, String typePath) {
      this.withExistingParent(object.getId().getPath(), "item/generated").texture("layer0", this.getItemLocation(object.getId().getPath(), typePath));
   }

   public ResourceLocation getItemLocation(String path, String typePath) {
      return ResourceLocation.fromNamespaceAndPath("sakuratinker", "item/" + typePath + "/" + path);
   }

   protected void registerModels() {
   }
}
