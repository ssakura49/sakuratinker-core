package com.ssakura49.sakuratinker.core.utils;

public class ColorUtil {
    public static final int LIGHT_RED = 0xff7e75;
    public static final int RED = 0xff0000;
    public static final int ORANGE = 0xffaa7f;
    public static final int YELLOW = 0xffff00;
    public static final int LIGHT_YELLOW = 0xffff7f;
    public static final int PINK = 0xffaaff;
    public static final int DARK_PURPLE = 0x3e4289;
    public static final int DARK_BLUE = 0x3e4289;
    private ColorUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    /**
     * 将 RGB 转换为 ARGB (Alpha 默认为 255，即不透明)
     *
     * @param rgb RGB 颜色值 (例如 0xFF0000)
     * @return ARGB 颜色值 (例如 0xFFFF0000)
     */
    public static int rgbToArgb(int rgb) {
        return 0xFF000000 | rgb;
    }
    /**
     * 将 RGB 转换为 ARGB，指定 Alpha 透明度
     * @param rgb   RGB 颜色值
     * @param alpha 透明度 (0-255)
     * @return ARGB 颜色值
     */
    public static int rgbToArgb(int rgb, int alpha) {
        return ((alpha & 0xFF) << 24) | (rgb & 0x00FFFFFF);
    }
    /**
     * 从 ARGB 中提取 RGB
     * @param argb ARGB 颜色值
     * @return RGB 颜色值
     */
    public static int argbToRgb(int argb) {
        return argb & 0x00FFFFFF;
    }
    /**
     * 获取 Red 分量 (0-255)
     */
    public static int getRed(int color) {
        return (color >> 16) & 0xFF;
    }
    /**
     * 获取 Green 分量 (0-255)
     */
    public static int getGreen(int color) {
        return (color >> 8) & 0xFF;
    }
    /**
     * 获取 Blue 分量 (0-255)
     */
    public static int getBlue(int color) {
        return color & 0xFF;
    }
    /**
     * 获取 Alpha 分量 (0-255)
     * 如果传入的是普通 RGB，默认返回 255
     */
    public static int getAlpha(int color) {
        return (color >> 24) & 0xFF;
    }
    /**
     * 根据分量合成颜色 (RGB)
     */
    public static int rgb(int r, int g, int b) {
        return ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }
    /**
     * 根据分量合成颜色 (ARGB)
     */
    public static int argb(int a, int r, int g, int b) {
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    /**
     * 将 Hex 字符串解析为颜色 Int
     * 支持格式:
     * - "#RRGGBB" (返回 RGB)
     * - "0xRRGGBB" (返回 RGB)
     * - "#AARRGGBB" (返回 ARGB)
     * - "0xAARRGGBB" (返回 ARGB)
     * @param hex 颜色字符串
     * @return 颜色 Int 值
     */
    public static int hexToColor(String hex) {
        if (hex == null || hex.isEmpty()) {
            throw new IllegalArgumentException("Hex string cannot be null or empty");
        }
        String cleanHex = hex.replace("0x", "").replace("#", "").trim();

        if (cleanHex.length() == 3) {
            cleanHex = "" + cleanHex.charAt(0) + cleanHex.charAt(0) + cleanHex.charAt(1) + cleanHex.charAt(1) + cleanHex.charAt(2) + cleanHex.charAt(2);
        }
        if (cleanHex.length() != 6 && cleanHex.length() != 8) {
            throw new IllegalArgumentException("Invalid hex color length: " + hex + ". Must be 6 (RGB) or 8 (ARGB) characters.");
        }
        long longColor = Long.parseLong(cleanHex, 16);
        if (cleanHex.length() == 8) {
            // ARGB
            return (int) longColor;
        } else {
            // RGB -> ARGB
            return (int) longColor & 0x00FFFFFF;
        }
    }

    /**
     * 将颜色转换为 Hex 字符串 (格式 #RRGGBB)
     */
    public static String toRgbHex(int color) {
        return String.format("#%06X", color & 0x00FFFFFF);
    }

    /**
     * 将颜色转换为 Hex 字符串 (格式 #AARRGGBB)
     */
    public static String toArgbHex(int color) {
        return String.format("#%08X", color);
    }

    /**
     * 混合两个颜色
     * @param c1 起始颜色
     * @param c2 结束颜色
     * @param ratio 比例 (0.0 ~ 1.0)
     * @return 混合后的颜色
     */
    public static int blend(int c1, int c2, double ratio) {
        ratio = Math.max(0, Math.min(1, ratio)); // Clamp 0-1
        int r1 = getRed(c1), g1 = getGreen(c1), b1 = getBlue(c1), a1 = getAlpha(c1);
        int r2 = getRed(c2), g2 = getGreen(c2), b2 = getBlue(c2), a2 = getAlpha(c2);
        int r = (int) (r1 + (r2 - r1) * ratio);
        int g = (int) (g1 + (g2 - g1) * ratio);
        int b = (int) (b1 + (b2 - b1) * ratio);
        int a = (int) (a1 + (a2 - a1) * ratio);
        return argb(a, r, g, b);
    }
}
