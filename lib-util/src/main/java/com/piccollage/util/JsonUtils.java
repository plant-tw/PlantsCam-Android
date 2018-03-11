package com.piccollage.util;

import android.text.TextUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonUtils {
    public static boolean isDataValid(JsonObject json, String key) {
        return !TextUtils.isEmpty(key) && json.has(key) && !json.get(key).isJsonNull();
    }

    public static String getStringSafely(JsonObject json, String key, String defaultValue) {
        return isDataValid(json, key) ? json.get(key).getAsString() : defaultValue;
    }

    public static int getIntSafely(JsonObject json, String key, int defaultValue) {
        return isDataValid(json, key) ? json.get(key).getAsInt() : defaultValue;
    }

    public static boolean getBooleanSafely(JsonObject json, String key, boolean defaultValue) {
        return isDataValid(json, key) ? json.get(key).getAsBoolean() : defaultValue;
    }

    public static float getFloatSafely(JsonObject json, String key, float defaultValue) {
        return isDataValid(json, key) ? json.get(key).getAsFloat() : defaultValue;
    }

    public static double getDoubleSafely(JsonObject json, String key, double defaultValue) {
        return isDataValid(json, key) ? json.get(key).getAsDouble() : defaultValue;
    }

    public static float[] getFloatArraySafely(JsonObject json, String key, float[] defaultValue) {
        if (!isDataValid(json, key)) {
            return defaultValue;
        }

        JsonArray array = json.getAsJsonArray(key);
        if (array.size() < 0) {
            return defaultValue;
        }

        float[] result = new float[array.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = array.get(i).getAsFloat();
        }

        return result;
    }

    public static JsonElement getJsonElement(JsonObject json, String key) {
        return isDataValid(json, key) ? json.get(key) : null;
    }

    public static Map<String, String> toMap(JSONObject object) throws JSONException {
        Map<String, String> map = new HashMap<>();
        Iterator<String> keysItr = object.keys();
        while(keysItr.hasNext()) {
            String key = keysItr.next();
            map.put(key, object.getString(key));
        }
        return map;
    }
}
