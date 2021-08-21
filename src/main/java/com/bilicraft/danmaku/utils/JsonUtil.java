package com.bilicraft.danmaku.utils;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

public class JsonUtil {
    public static boolean isJson(String str) {
        try {
            new JsonParser().parse(str);
            return true;
        } catch (JsonParseException exception) {
            return false;
        }
    }
}
