package com.ssakura49.sakuratinker.core.data.condition;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import com.ssakura49.sakuratinker.SakuraTinker;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class ConfigCondition implements ICondition {
   public static final Map<String, ConfigCondition> CONDITIONS = new HashMap();
   public static final ResourceLocation ID = SakuraTinker.getResource("config");
   public static final IConditionSerializer<ConfigCondition> SERIALIZER = new ConfigSerializer();
   private final String configName;
   private final Supplier<Boolean> config;

   public ConfigCondition(String configName, Supplier<Boolean> config) {
      this.configName = configName;
      this.config = config;
   }

   public ResourceLocation getID() {
      return ID;
   }

   public boolean test(ICondition.IContext context) {
      return (Boolean)this.config.get();
   }

   public static ConfigCondition add(String prop, Supplier<Boolean> supplier) {
      ConfigCondition conf = new ConfigCondition(prop, supplier);
      CONDITIONS.put(prop.toLowerCase(Locale.ROOT), conf);
      return conf;
   }

   public static void add(ConfigCondition condition) {
      CONDITIONS.put(condition.configName.toLowerCase(Locale.ROOT), condition);
   }

   private static class ConfigSerializer implements Serializer<ConfigCondition>, IConditionSerializer<ConfigCondition> {
      public ResourceLocation getID() {
         return ConfigCondition.ID;
      }

      public void write(JsonObject json, ConfigCondition value) {
         json.addProperty("prop", value.configName);
      }

      public ConfigCondition read(JsonObject json) {
         String prop = GsonHelper.getAsString(json, "prop");
         ConfigCondition config = (ConfigCondition)ConfigCondition.CONDITIONS.get(prop.toLowerCase(Locale.ROOT));
         if (config == null) {
            throw new JsonSyntaxException("Invalid property name '" + prop + "'");
         } else {
            return config;
         }
      }

      public void serialize(JsonObject json, ConfigCondition condition, JsonSerializationContext context) {
         this.write(json, condition);
      }

      public ConfigCondition deserialize(JsonObject json, JsonDeserializationContext context) {
         return this.read(json);
      }
   }
}
