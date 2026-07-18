package com.ssakura49.sakuratinker.core.utils.component;

public class RomanNumberalUtil {
   private static final int[] VALUES = new int[]{1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
   private static final String[] SYMBOLS = new String[]{"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};

   public static String toRoman(int number) {
      if (number > 0 && number <= 3999) {
         StringBuilder sb = new StringBuilder();
         int remain = number;

         for(int i = 0; i < VALUES.length; ++i) {
            while(remain >= VALUES[i]) {
               remain -= VALUES[i];
               sb.append(SYMBOLS[i]);
            }
         }

         return sb.toString();
      } else {
         throw new IllegalArgumentException("罗马数字只支持 1-3999: " + number);
      }
   }

   public static int fromRoman(String roman) {
      int i = 0;
      int result = 0;

      for(int j = 0; j < VALUES.length; ++j) {
         for(String symbol = SYMBOLS[j]; roman.startsWith(symbol, i); i += symbol.length()) {
            result += VALUES[j];
         }
      }

      return result;
   }
}
