package com.ssakura49.sakuratinker.core.data.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.ssakura49.sakuratinker.STCrConfig;
import com.ssakura49.sakuratinker.SakuraTinker;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import org.jetbrains.annotations.NotNull;

public class GeneralMaterialConfigCondition implements ICondition {
   private final String name;
   public static final IConditionSerializer<GeneralMaterialConfigCondition> SERIALIZER = new CompatSerializer();
   public static final ResourceLocation ID = SakuraTinker.getResource("general_material_enabled");

   public GeneralMaterialConfigCondition(String name) {
      this.name = name;
   }

   public ResourceLocation getID() {
      return ID;
   }

   public boolean test(ICondition.IContext context) {
      return !(STCrConfig.COMMON.LIST_BLACKLIST_GENERAL.get()).contains(this.name) && ((List)STCrConfig.COMMON.LIST_WHITELIST_GENERAL.get()).isEmpty() || ((List)STCrConfig.COMMON.LIST_WHITELIST_GENERAL.get()).contains(this.name);
   }

   private static class CompatSerializer implements Serializer<GeneralMaterialConfigCondition>, IConditionSerializer<GeneralMaterialConfigCondition> {
      public ResourceLocation getID() {
         return GeneralMaterialConfigCondition.ID;
      }

      public void write(JsonObject json, GeneralMaterialConfigCondition value) {
         json.addProperty("name", value.name);
      }

      public GeneralMaterialConfigCondition read(JsonObject json) {
         String modId = GsonHelper.getAsString(json, "name");
         return new GeneralMaterialConfigCondition(modId);
      }

      public void serialize(@NotNull JsonObject json, @NotNull GeneralMaterialConfigCondition condition, @NotNull JsonSerializationContext context) {
         this.write(json, condition);
      }

      public @NotNull GeneralMaterialConfigCondition deserialize(@NotNull JsonObject json, @NotNull JsonDeserializationContext context) {
         return this.read(json);
      }
   }
}
