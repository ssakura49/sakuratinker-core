package com.ssakura49.sakuratinker.core.init;

import com.google.common.collect.ImmutableMultimap;
import com.ssakura49.sakuratinker.core.item.FoxCurio;
import com.ssakura49.sakuratinker.core.item.Slaughter;
import com.ssakura49.sakuratinker.core.item.SoulSakuraBook;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class STCrItem {
   public static final DeferredRegister<Item> ITEMS= DeferredRegister.create(ForgeRegistries.ITEMS, "sakuratinker");;
   protected static Map<String, List<RegistryObject<Item>>> LIST_MISC= new HashMap<>();
   protected static Map<String, List<RegistryObject<Item>>> LIST_MATERIAL= new HashMap<>();
   protected static Map<String, List<RegistryObject<Item>>> LIST_TOOL= new HashMap<>();
   protected static Map<String, List<RegistryObject<BlockItem>>> LIST_SIMPLE_BLOCK= new HashMap<>();
   protected static Map<String, List<RegistryObject<BlockItem>>> LIST_MISC_BLOCK= new HashMap<>();
   protected static Map<String, List<RegistryObject<Item>>> LIST_MATERIAL_ITEM_MODEL= new HashMap<>();
   protected static Map<String, List<RegistryObject<Item>>> LIST_MISC_ITEM_MODEL= new HashMap<>();

   public static final RegistryObject<Item> BOOK = registerMixc("sakuratinker", ITEMS, "soul_sakura_book", () -> new SoulSakuraBook(new Item.Properties()), true);

   public static final RegistryObject<Item> FOX_MASK = registerMixc("sakuratinker", ITEMS, "fox_mask", () -> new FoxCurio(new Item.Properties(), "fox_mask", ImmutableMultimap.of()), false);

   public static final RegistryObject<Item> SLAUGHTER = registerMixc("sakuratinker", ITEMS, "slaughter", () -> new Slaughter((new Item.Properties()).stacksTo(1)), false);


   public static List<RegistryObject<Item>> getListMisc(String modId) {
      return List.copyOf(LIST_MISC.getOrDefault(modId, new ArrayList<>()));
   }

   public static List<RegistryObject<Item>> getListMaterial(String modId) {
      return List.copyOf(LIST_MATERIAL.getOrDefault(modId, new ArrayList<>()));
   }

   public static List<RegistryObject<Item>> getListTool(String modId) {
      return List.copyOf(LIST_TOOL.getOrDefault(modId, new ArrayList<>()));
   }

   public static List<RegistryObject<Item>> getListMaterialItemModel(String modId) {
      return List.copyOf(LIST_MATERIAL_ITEM_MODEL.getOrDefault(modId, new ArrayList<>()));
   }

   public static List<RegistryObject<Item>> getListMiscItemModel(String modId) {
      return List.copyOf(LIST_MISC_ITEM_MODEL.getOrDefault(modId, new ArrayList<>()));
   }

   public static List<RegistryObject<BlockItem>> getListMiscBlock(String modId) {
      return List.copyOf(LIST_MISC_BLOCK.getOrDefault(modId, new ArrayList<>()));
   }

   public static List<RegistryObject<Item>> getListSimpleMaterialModel(String modId) {
      return List.copyOf(LIST_MATERIAL_ITEM_MODEL.getOrDefault(modId, new ArrayList<>()));
   }

   public static List<RegistryObject<Item>> getListSimpleMiscModel(String modId) {
      return List.copyOf(LIST_MISC_ITEM_MODEL.getOrDefault(modId, new ArrayList<>()));
   }

   public static List<RegistryObject<BlockItem>> getListSimpleBlock(String modId) {
      return List.copyOf(LIST_SIMPLE_BLOCK.getOrDefault(modId, new ArrayList<>()));
   }

   public static <T> void putOrCreateList(Map<String, List<RegistryObject<T>>> map, String modId, RegistryObject<T> object) {
      List<RegistryObject<T>> list = map.getOrDefault(modId, new ArrayList<>());
      list.add(object);
      map.put(modId, list);
   }

   public static RegistryObject<Item> registerMixc(String modId, DeferredRegister<Item> register, String name, Supplier<? extends Item> sup, boolean simpleModel) {
      RegistryObject<Item> object = register.register(name, sup);
      putOrCreateList(LIST_MISC, modId, object);
      if (simpleModel) {
         putOrCreateList(LIST_MISC_ITEM_MODEL, modId, object);
      }

      return object;
   }

   public static RegistryObject<Item> registerMaterial(String modId, DeferredRegister<Item> register, String name, Supplier<? extends Item> sup, boolean simpleModel) {
      RegistryObject<Item> object = register.register(name, sup);
      putOrCreateList(LIST_MATERIAL, modId, object);
      if (simpleModel) {
         putOrCreateList(LIST_MATERIAL_ITEM_MODEL, modId, object);
      }

      return object;
   }

   public static RegistryObject<Item> registerToolOrPart(String modId, DeferredRegister<Item> register, String name, Supplier<? extends Item> sup) {
      RegistryObject<Item> object = register.register(name, sup);
      putOrCreateList(LIST_TOOL, modId, object);
      return object;
   }

   public static RegistryObject<BlockItem> registerSimpleBlockItem(String modId, DeferredRegister<Item> register, RegistryObject<? extends Block> block) {
      RegistryObject<BlockItem> object = register.register(block.getId().getPath(), () -> new BlockItem((Block)block.get(), new Item.Properties()));
      putOrCreateList(LIST_SIMPLE_BLOCK, modId, object);
      return object;
   }

   public static RegistryObject<BlockItem> registerBlockItem(String modId, DeferredRegister<Item> register, RegistryObject<? extends Block> block) {
      RegistryObject<BlockItem> object = register.register(block.getId().getPath(), () -> new BlockItem((Block)block.get(), new Item.Properties()));
      putOrCreateList(LIST_MISC_BLOCK, modId, object);
      return object;
   }

}
