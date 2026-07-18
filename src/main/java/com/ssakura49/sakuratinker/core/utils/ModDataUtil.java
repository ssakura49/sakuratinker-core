package com.ssakura49.sakuratinker.core.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

public class ModDataUtil {
   public static long getLong(ModDataNBT data, ResourceLocation key) {
      return !data.contains(key, 4) ? 0L : (Long)data.get(key, CompoundTag::getLong);
   }

   public static void putLong(ModDataNBT data, ResourceLocation key, long value) {
      CompoundTag tag = data.getCopy();
      tag.putLong(key.toString(), value);
      data.copyFrom(tag);
   }

   public static @Nullable String getString(ModDataNBT data, ResourceLocation key) {
      CompoundTag tag = data.getCopy();
      return tag.contains(key.toString()) ? tag.getString(key.toString()) : null;
   }

   public static void putString(ModDataNBT data, ResourceLocation key, String value) {
      CompoundTag tag = data.getCopy();
      tag.putString(key.toString(), value);
      data.copyFrom(tag);
   }
}
