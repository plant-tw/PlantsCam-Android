package com.piccollage.util;

public class MathUtils {

    @SuppressWarnings("unused")
    public static boolean almostEqual(double a,
                                      double b,
                                      double eps) {
        return Math.abs(a - b) < eps;
    }

    public static boolean almostEqual(float a,
                                      float b,
                                      float eps) {
        return Math.abs(a - b) < eps;
    }
}
