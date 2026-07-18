package com.ssakura49.sakuratinker.core.library.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraftforge.eventbus.api.IEventBus;
import slimeknights.mantle.registration.deferred.FluidDeferredRegister;
import slimeknights.mantle.registration.deferred.ItemDeferredRegister;

public class SimpleMaterialWrappedRegister {
   protected final ItemDeferredRegister itemRegister;
   protected final FluidDeferredRegister fluidRegister;
   protected final Map<String, SimpleMaterialObject> entryMap = new ConcurrentHashMap();
   public final String modId;

   public SimpleMaterialWrappedRegister(String modId) {
      this.itemRegister = new ItemDeferredRegister(modId);
      this.fluidRegister = new FluidDeferredRegister(modId);
      this.modId = modId;
   }

   public void register(IEventBus modEventBus) {
      this.itemRegister.register(modEventBus);
      this.fluidRegister.register(modEventBus);
   }

   public SimpleMaterialObject.Builder buildMaterial(String name) {
      return new SimpleMaterialObject.Builder(this.modId, name, this);
   }
}
