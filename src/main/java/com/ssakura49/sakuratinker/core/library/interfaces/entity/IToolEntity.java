package com.ssakura49.sakuratinker.core.library.interfaces.entity;

import com.ssakura49.sakuratinker.STCrConfig;
import java.util.function.Supplier;

public interface IToolEntity {
   Supplier<Boolean> TOOL_ENTITY_CAN_HURT_PLAYER = STCrConfig.COMMON.TOOL_ENTITY_CAN_HURT_PLAYER::get;
   Supplier<Boolean> TOOL_ENTITY_CAN_HURT_SELF = STCrConfig.COMMON.TOOL_ENTITY_CAN_HURT_SELF::get;

   default boolean shouldPreventPlayerDamage() {
      return (Boolean)TOOL_ENTITY_CAN_HURT_PLAYER.get();
   }

   default boolean shouldPreventSelfDamage() {
      return (Boolean)TOOL_ENTITY_CAN_HURT_SELF.get();
   }
}
