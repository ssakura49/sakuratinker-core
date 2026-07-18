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

public class CompatConfigCondition implements ICondition {
   private final String modId;
   private final boolean isOriginal;
   public static final IConditionSerializer<CompatConfigCondition> SERIALIZER = new CompatSerializer();
   public static final ResourceLocation ID = SakuraTinker.getResource("compat_enabled");

   public CompatConfigCondition(String modId, boolean isOriginal) {
      this.modId = modId;
      this.isOriginal = isOriginal;
   }

   public ResourceLocation getID() {
      return ID;
   }

   public boolean test(ICondition.IContext context) {
      return this.isOriginal ? !(STCrConfig.COMMON.LIST_BLACKLIST_COMPAT_ORIGINAL.get()).contains(this.modId) && ((List)STCrConfig.COMMON.LIST_WHITELIST_COMPAT_ORIGINAL.get()).isEmpty() || ((List)STCrConfig.COMMON.LIST_WHITELIST_COMPAT_ORIGINAL.get()).contains(this.modId) : !((List)STCrConfig.COMMON.LIST_BLACKLIST_COMPAT.get()).contains(this.modId) && ((List)STCrConfig.COMMON.LIST_WHITELIST_COMPAT.get()).isEmpty() || ((List)STCrConfig.COMMON.LIST_WHITELIST_COMPAT.get()).contains(this.modId);
   }

   private static class CompatSerializer implements Serializer<CompatConfigCondition>, IConditionSerializer<CompatConfigCondition> {
      public ResourceLocation getID() {
         return CompatConfigCondition.ID;
      }

      @Override
      public void write(JsonObject json, CompatConfigCondition value) {
         json.addProperty("mod_id", value.modId);
         json.addProperty("is_original", value.isOriginal);
      }

      @Override
      public CompatConfigCondition read(JsonObject json) {
         String modId = GsonHelper.getAsString(json, "mod_id");
         boolean isOriginal = GsonHelper.getAsBoolean(json, "is_original");
         return new CompatConfigCondition(modId, isOriginal);
      }

      public void serialize(@NotNull JsonObject json, @NotNull CompatConfigCondition condition, @NotNull JsonSerializationContext context) {
         this.write(json, condition);
      }

      public @NotNull CompatConfigCondition deserialize(@NotNull JsonObject json, @NotNull JsonDeserializationContext context) {
         return this.read(json);
      }
   }
}
