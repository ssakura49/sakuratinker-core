package com.ssakura49.sakuratinker.core.utils;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import slimeknights.tconstruct.library.tools.nbt.ModDataNBT;

import java.util.UUID;

public class ModDataNBTExtra extends ModDataNBT {
   public static void putLong(ModDataNBT data, ResourceLocation key, long value) {
      data.put(key, LongTag.valueOf(value));
   }
   public static long getLong(ModDataNBT data, ResourceLocation key) {
      return data.get(key, CompoundTag::getLong);
   }

   public static void putDouble(ModDataNBT data, ResourceLocation key, double value) {
      data.putString(key, Double.toString(value));
   }

   public static double getDouble(ModDataNBT data, ResourceLocation key) {
      return data.get(key, (tag, k) -> {
         if (tag.contains(k, Tag.TAG_STRING)) {
            try {
               return Double.parseDouble(tag.getString(k));
            } catch (NumberFormatException e) {
               return 0.0;
            }
         }
         if (tag.contains(k, Tag.TAG_LONG)) {
            return Double.longBitsToDouble(tag.getLong(k));
         }
         return 0.0;
      });
   }


    public static void putDoubleRaw(ModDataNBT data, ResourceLocation key, double value) {
        data.put(key, LongTag.valueOf(Double.doubleToRawLongBits(value)));
    }
    public static double getDoubleRaw(ModDataNBT data, ResourceLocation key) {
        return Double.longBitsToDouble(data.get(key, CompoundTag::getLong));
    }

   public static void putUUID(ModDataNBT data, ResourceLocation key, UUID uuid) {
      data.put(key, NbtUtils.createUUID(uuid));
   }

   public static @NotNull UUID getUUID(ModDataNBT data, ResourceLocation key) {
      return data.get(key, CompoundTag::getUUID);
   }

   public static void remove(ModDataNBT data, ResourceLocation key) {
      data.remove(key);
   }
}
