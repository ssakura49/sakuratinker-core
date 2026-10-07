package com.ssakura49.sakuratinker.core.init;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Type;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;

public class STCrKeys {
   public static final String CATEGORY = "key.categories.sakuratinker";
   private static final Map<String, KeyMapping> ID_TO_MAPPING = new HashMap<>();

   public static void registerAll(RegisterKeyMappingsEvent event) {
      for(Keys key : STCrKeys.Keys.values()) {
         KeyMapping mapping = new KeyMapping(key.getFullId(), KeyConflictContext.IN_GAME, Type.KEYSYM, key.getDefaultKey(), "key.categories.sakuratinker");
         event.register(mapping);
         ID_TO_MAPPING.put(key.getFullId(), mapping);
      }

   }

   public static void registerKey(String id, int defaultKey, String category) {
      ID_TO_MAPPING.put(id, new KeyMapping(
              id,
              KeyConflictContext.IN_GAME,
              InputConstants.Type.KEYSYM,
              defaultKey,
              category
      ));
   }

   @Nullable
   public static KeyMapping getMapping(String id) {
      return (KeyMapping)ID_TO_MAPPING.get(id);
   }

   public static enum Keys {
      SUMMON("summon", 71),
      TREASURE_TOGGLE_LOOT("treasure_toggle_loot", 76),
      BLOOD_BURN("blood_burn", 74),
      SELECTED_SLOT("selected_slot", 75);

      private final String name;
      private final int defaultKey;
      private final String fullId;

      private Keys(String name, int defaultKey) {
         this.name = name;
         this.defaultKey = defaultKey;
         this.fullId = "key.sakuratinker." + name;
      }

      public String getName() {
         return this.name;
      }

      public int getDefaultKey() {
         return this.defaultKey;
      }

      public String getFullId() {
         return this.fullId;
      }

   }
}
