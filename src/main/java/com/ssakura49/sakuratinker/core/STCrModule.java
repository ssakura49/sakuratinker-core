package com.ssakura49.sakuratinker.core;

import com.ssakura49.sakura_library.api.client.creativetab.SideBarCategory;
import com.ssakura49.sakura_library.api.client.creativetab.TabSideBarManager;
import com.ssakura49.sakuratinker.STCrConfig;
import com.ssakura49.sakuratinker.SakuraTinker;
import com.ssakura49.sakuratinker.core.data.condition.CompatConfigCondition;
import com.ssakura49.sakuratinker.core.data.condition.ConfigCondition;
import com.ssakura49.sakuratinker.core.data.condition.GeneralMaterialConfigCondition;
import com.ssakura49.sakuratinker.core.init.STCrCondition;
import com.ssakura49.sakuratinker.core.init.STCrItem;
import com.ssakura49.sakuratinker.core.init.STCrParticleTypes;
import com.ssakura49.sakuratinker.core.init.STCrRecipe;
import com.ssakura49.sakuratinker.core.init.STCrTab;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import slimeknights.mantle.registration.deferred.EntityTypeDeferredRegister;
import slimeknights.mantle.registration.deferred.FluidDeferredRegister;
import slimeknights.tconstruct.library.modifiers.util.ModifierDeferredRegister;

@EventBusSubscriber(
   bus = Bus.MOD,
   modid = "sakuratinker"
)
public class STCrModule {
   public static final DeferredRegister<Block> BLOCKS;
   public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES;
   public static final DeferredRegister<MobEffect> EFFECTS;
   public static final FluidDeferredRegister FLUIDS;
   public static final DeferredRegister<MenuType<?>> MENUS;
   public static final EntityTypeDeferredRegister ENTITIES;
   public static final ModifierDeferredRegister MODIFIERS;

   public static void register(IEventBus bus) {
      STCrItem.ITEMS.register(bus);
      STCrTab.CREATIVE_MODE_TABS.register(bus);
      BLOCKS.register(bus);
      BLOCK_ENTITIES.register(bus);
      EFFECTS.register(bus);
      STCrParticleTypes.PARTICLES.register(bus);
      FLUIDS.register(bus);
      MENUS.register(bus);
      MODIFIERS.register(bus);
      ENTITIES.register(bus);
      STCrRecipe.RECIPE_SERIALIZERS.register(bus);
      STCrConfig.init();
   }

   @SubscribeEvent
   public static void onCommonSetup(FMLCommonSetupEvent event) {
   }

   @SubscribeEvent
   public static void onClientSetup(FMLClientSetupEvent event) {
      event.enqueueWork(() -> {
         SideBarCategory baseCategory = SideBarCategory.builder().title(Component.translatable("sidebar.sakuratinker.base")).icon(() -> ((Item)STCrItem.BOOK.get()).getDefaultInstance()).displayItems((provider, output) -> {
            for(RegistryObject<Item> object : STCrItem.getListMisc("sakuratinker")) {
               if (object.isPresent()) {
                  output.accept(object.get());
               }
            }

         }).build();
         TabSideBarManager.registerCategory(STCrTab.CORE_TAB.getId(), baseCategory);
      });
   }

   @SubscribeEvent
   public static void onRegister(RegisterEvent event) {
      if (event.getRegistryKey() == Registries.RECIPE_SERIALIZER) {
         SakuraTinker.LOGGER.info("ST on RegisterEvent");
         CraftingHelper.register(ConfigCondition.SERIALIZER);
         CraftingHelper.register(CompatConfigCondition.SERIALIZER);
         CraftingHelper.register(GeneralMaterialConfigCondition.SERIALIZER);
         STCrCondition.init();
      }

   }

   static {
      BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, "sakuratinker");
      BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, "sakuratinker");
      EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, "sakuratinker");
      FLUIDS = new FluidDeferredRegister("sakuratinker");
      MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, "sakuratinker");
      ENTITIES = new EntityTypeDeferredRegister("sakuratinker");
      MODIFIERS = ModifierDeferredRegister.create("sakuratinker");
   }
}
