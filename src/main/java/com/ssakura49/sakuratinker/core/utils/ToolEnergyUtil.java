package com.ssakura49.sakuratinker.core.utils;

import slimeknights.tconstruct.library.tools.capability.ToolEnergyCapability;
import slimeknights.tconstruct.library.tools.nbt.IToolStackView;

public class ToolEnergyUtil {
   public static int receiveEnergy(IToolStackView tool, int maxReceive, boolean simulate) {
      if (maxReceive <= 0) {
         return 0;
      } else {
         int current = ToolEnergyCapability.getEnergy(tool);
         int filled = Math.min(ToolEnergyCapability.getMaxEnergy(tool) - current, maxReceive);
         if (!simulate) {
            ToolEnergyCapability.setEnergy(tool, current + filled);
         }

         return filled;
      }
   }

   public static int extractEnergy(IToolStackView tool, int maxExtract, boolean simulate) {
      if (maxExtract <= 0) {
         return 0;
      } else {
         int current = ToolEnergyCapability.getEnergy(tool);
         if (current <= 0) {
            return 0;
         } else {
            int drained = Math.min(current, maxExtract);
            if (!simulate) {
               ToolEnergyCapability.setEnergy(tool, current - drained);
            }

            return drained;
         }
      }
   }
}
