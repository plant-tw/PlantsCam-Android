package com.cardinalblue.utils;

/**
 * Created by prada on 15/4/23.
 */
public class YUVUtils {
    static {
        System.loadLibrary("yuv-to-rgb");
    }

    public static native void yuv2rgb(int[] rgb, byte[] yuv, int width, int height);
}
