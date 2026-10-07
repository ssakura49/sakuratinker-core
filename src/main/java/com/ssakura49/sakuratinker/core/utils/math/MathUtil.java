package com.ssakura49.sakuratinker.core.utils.math;

public class MathUtil {
    public static String getUnitInt(int amount) {
        int a = (int)Math.log10((double)amount);
        int b = a / 3;
        return switch (b) {
            case 1 -> {
                Object[] var4 = new Object[]{(double) ((float) amount) / (double) 1000.0F};
                yield String.format("%.2f", var4) + " k";
            }
            case 2 -> {
                Object[] var3 = new Object[]{(double) ((float) amount) / (double) 1000000.0F};
                yield String.format("%.2f", var3) + " M";
            }
            case 3 -> {
                Object[] var10001 = new Object[]{(double) ((float) amount) / (double) 1.0E9F};
                yield String.format("%.2f", var10001) + " G";
            }
            default -> amount + " ";
        };
    }

    public static String getUnitFloat(double amount) {
        double bitRaw = Math.log10(Math.abs(amount));
        bitRaw += bitRaw <= (double)0.0F ? (double)-3.0F : (double)0.0F;
        bitRaw /= (double)3.0F;
        int bits = (int)bitRaw;
        String unit;
        if (bits > 5) {
            unit = " P";
            amount *= (double)1.0E-15F;
        } else if (bits < -5) {
            unit = " f";
            amount *= (double)1.0E15F;
        } else {
            String var10000;
            switch (bits) {
                case -4 -> var10000 = " p";
                case -3 -> var10000 = " n";
                case -2 -> var10000 = " μ";
                case -1 -> var10000 = " m";
                case 0 -> var10000 = " ";
                case 1 -> var10000 = " k";
                case 2 -> var10000 = " M";
                case 3 -> var10000 = " G";
                case 4 -> var10000 = " T";
                case 5 -> var10000 = " P";
                default -> var10000 = " f";
            }

            unit = var10000;
            amount *= (double)((float)Math.pow((double)1000.0F, (double)(-bits)));
        }

        String var9 = String.format("%.2f", amount);
        return var9 + unit;
    }

    public static String getUnitForFluid(int amount) {
        int a = (int)Math.log10((double)amount);
        int b = a / 3;
        return switch (b) {
            case 1 -> {
                Object[] var4 = new Object[]{(double) ((float) amount) / (double) 1000.0F};
                yield String.format("%.2f", var4) + " B";
            }
            case 2 -> {
                Object[] var3 = new Object[]{(double) ((float) amount) / (double) 1000000.0F};
                yield String.format("%.2f", var3) + " kB";
            }
            case 3 -> {
                Object[] var10001 = new Object[]{(double) ((float) amount) / (double) 1.0E9F};
                yield String.format("%.2f", var10001) + " MB";
            }
            default -> amount + " mB";
        };
    }

    public static double getDecimal(double number) {
        return number - (double)((int)number);
    }

    public static double limitsNumber(double number, int limit) {
        if (limit < 0) {
            limit = 0;
        }

        double scale = Math.pow((double)10.0F, (double)limit);
        return (double)Math.round(number * scale) / scale;
    }

    public static String getEnergyString(int amount) {
        return getUnitInt(amount) + "FE";
    }

    public static String toPercentage(double number, int limit) {
        double var10000 = limitsNumber(number, limit + 2);
        return var10000 * (double)100.0F + "%";
    }
}
