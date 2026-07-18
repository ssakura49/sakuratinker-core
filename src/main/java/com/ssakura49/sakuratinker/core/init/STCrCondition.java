package com.ssakura49.sakuratinker.core.init;

import com.ssakura49.sakuratinker.STCrConfig;
import com.ssakura49.sakuratinker.core.data.condition.ConfigCondition;

public class STCrCondition {
   public static final ConfigCondition ALLOW_ORIGINAL_MATERIALS = new ConfigCondition("allow_original_materials", STCrConfig.COMMON.ALLOW_ORIGINAL_MATERIALS);

   public static void init() {
      ConfigCondition.add(ALLOW_ORIGINAL_MATERIALS);
   }
}
