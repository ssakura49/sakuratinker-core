package com.ssakura49.sakuratinker.core.library.tools.modules;

import lombok.Getter;
import slimeknights.mantle.data.loadable.primitive.EnumLoadable;
import slimeknights.tconstruct.library.tools.stat.FloatToolStat;
import slimeknights.tconstruct.library.tools.stat.INumericToolStat;
import slimeknights.tconstruct.library.tools.stat.ModifierStatsBuilder;

@Getter
public enum StatOperation {
   ADDITION("add") {
      public <T extends Number> void apply(ModifierStatsBuilder builder, INumericToolStat<T> stat, float value) {
         stat.add(builder, (double)value);
      }
   },
   SUBTRACTION("subtract") {
      public <T extends Number> void apply(ModifierStatsBuilder builder, INumericToolStat<T> stat, float value) {
         stat.add(builder, (double)(-value));
      }
   },
   MULTIPLY_BASE("multiply_base") {
      public <T extends Number> void apply(ModifierStatsBuilder builder, INumericToolStat<T> stat, float value) {
         stat.multiply(builder, (double)(1.0F + value));
      }
   },
   MULTIPLY_CONDITIONAL("multiply_conditional") {
      public <T extends Number> void apply(ModifierStatsBuilder builder, INumericToolStat<T> stat, float value) {
         builder.multiplier(stat, (double)(1.0F + value));
      }
   },
   MULTIPLY_ALL("multiply_all") {
      public <T extends Number> void apply(ModifierStatsBuilder builder, INumericToolStat<T> stat, float value) {
         stat.multiplyAll(builder, (double)(1.0F + value));
      }
   },
   PERCENT_OF_BASE("percent_of_base") {
      public <T extends Number> void apply(ModifierStatsBuilder builder, INumericToolStat<T> stat, float value) {
         if (stat instanceof FloatToolStat floatStat) {
            float baseValue = floatStat.getDefaultValue();
            floatStat.add(builder, (double)(baseValue * value));
         }

      }
   };

   private final String serializedName;
   public static final EnumLoadable<StatOperation> LOADER = new EnumLoadable<>(StatOperation.class);

   private StatOperation(String serializedName) {
      this.serializedName = serializedName;
   }

    public abstract <T extends Number> void apply(ModifierStatsBuilder var1, INumericToolStat<T> var2, float var3);

}
