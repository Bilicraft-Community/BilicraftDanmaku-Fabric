package com.bilicraft.danmaku.utils;

import java.util.regex.Pattern;

public class PatternUtils {
    private static final Pattern patternControlCode = Pattern.compile("(?i)\\u00A7[0-9A-FK-OR]");

    public static String stripControlCodes(String str)
    {
        return patternControlCode.matcher(str).replaceAll("");
    }
}
