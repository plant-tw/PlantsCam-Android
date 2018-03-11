package com.piccollage.util;

import java.util.HashMap;
import java.util.Map;

public class AppHelper {
    private static final Map<Class<?>, Object> mInstanceMap;

    static {
        mInstanceMap = new HashMap<>();
    }

    static public void put(Class<?> clazz, Object instance) {
        // TODO: check `instance` is belongs to `clazz`
        mInstanceMap.put(clazz, instance);
    }

    @SuppressWarnings("unchecked")
    static public <T> T get(Class<?> clazz) {
        return (T) mInstanceMap.get(clazz);
    }

}
