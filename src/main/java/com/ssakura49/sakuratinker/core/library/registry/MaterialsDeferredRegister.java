package com.ssakura49.sakuratinker.core.library.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

import lombok.Getter;
import net.minecraftforge.eventbus.api.IEventBus;
import slimeknights.mantle.registration.deferred.FluidDeferredRegister;
import slimeknights.mantle.registration.deferred.ItemDeferredRegister;

public class MaterialsDeferredRegister {
   @Getter
   protected final ItemDeferredRegister itemRegister;
   @Getter
   protected final FluidDeferredRegister fluidRegister;
   protected final Map<String, MaterialDefinition> entryMap = new ConcurrentHashMap<>();
   public final String modId;

   public MaterialsDeferredRegister(String modId) {
      this.itemRegister = new ItemDeferredRegister(modId);
      this.fluidRegister = new FluidDeferredRegister(modId);
      this.modId = modId;
   }

   public void register(IEventBus modEventBus) {
      this.itemRegister.register(modEventBus);
      this.fluidRegister.register(modEventBus);
   }

   public MaterialDefinition.Builder buildMaterial(String name) {
      return new MaterialDefinition.Builder(this.modId, name, this);
   }

   public MaterialDefinition register(String name, Consumer<MaterialDefinition.Builder> consumer) {
      MaterialDefinition.Builder builder = this.buildMaterial(name);
      consumer.accept(builder);
      return builder.build();
   }

   public void putMaterial(String name, MaterialDefinition object) {
      this.entryMap.put(name, object);
   }

    public void forEachMaterial(Consumer<MaterialDefinition> action) {
      this.entryMap.values().forEach(object -> {
         if (object.getMaterialInfo() != null && object.isUnit()) {
            action.accept(object);
         }
      });
   }

   public Map<String, MaterialDefinition> getMaterialMap() {
      return entryMap;
   }
}
