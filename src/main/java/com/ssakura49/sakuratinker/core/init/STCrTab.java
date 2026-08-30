package com.ssakura49.sakuratinker.core.init;

import com.ssakura49.sakura_library.api.client.creativetab.SideBarCategory;
import com.ssakura49.sakura_library.api.client.creativetab.TabSideBarManager;
import java.util.List;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import static com.ssakura49.sakuratinker.SakuraTinker.MODID;

public class STCrTab {
   public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS;
   public static final RegistryObject<CreativeModeTab> CORE_TAB;

   static {
      CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
      CORE_TAB = CREATIVE_MODE_TABS.register("stcr_tab", () -> CreativeModeTab.builder()
              .title(Component.translatable("itemGroup.sakuratinker.stcr_tab"))
              .icon(() -> STCrItem.BOOK.get().getDefaultInstance())
              .displayItems((parameters, output) -> {
                  for(RegistryObject<Item> object : STCrItem.getListMisc(MODID)) {
                      if (object.isPresent()) {
                          output.accept(object.get());
                      }
                  }
                  ResourceLocation currentTabId = ResourceLocation.fromNamespaceAndPath(MODID, "stcr_tab");
                  List<SideBarCategory> categories = TabSideBarManager.getCategoriesById(currentTabId);
                  var provider = parameters.holders();
                  categories.forEach(cat -> {
                      cat.populate(provider);
                      cat.getDisplayItems().forEach(output::accept);
                  });
         }).build());
   }
}
