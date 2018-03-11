package com.cardinalblue.android.utils;

import android.graphics.RectF;

import com.piccollage.util.BuildConfig;
import com.piccollage.util.Utils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class UtilsTest {


    @Test
    public void testGCD() {
        assertEquals(13, Utils.getGCD(13, 13));
        assertEquals(1, Utils.getGCD(37, 600));
        assertEquals(1, Utils.getGCD(600, 37));
        assertEquals(20, Utils.getGCD(100, 20));
        assertEquals(20, Utils.getGCD(20, 100));
        assertEquals(18913, Utils.getGCD(624129, 2061517));
        assertEquals(-18913, Utils.getGCD(-624129, -2061517));

        try {
            Utils.getLCM(-3, 2);
        } catch (IllegalArgumentException e) {
            // PASS.
        } catch (Throwable e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testLCM() {
        assertEquals(1, Utils.getLCM(1, 1));
        assertEquals(476, Utils.getLCM(28, 34));
        assertEquals(775, Utils.getLCM(155, 25));
        assertEquals(-775, Utils.getLCM(-155, -25));

        try {
            Utils.getLCM(155, -25);
        } catch (IllegalArgumentException e) {
            // PASS.
        } catch (Throwable e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testBestFps() {
        assertEquals(1, Utils.getBestFps(Arrays.asList(1, 1, 1)));
        assertEquals(6, Utils.getBestFps(Arrays.asList(1, 2, 3)));
        assertEquals(15, Utils.getBestFps(Arrays.asList(1, 15, 3)));
        assertEquals(Utils.MAX_FPS, Utils.getBestFps(Arrays.asList(1, 15, 45)));

        // Empty input.
        try {
            Utils.getBestFps(null);
            fail();
        } catch (IllegalArgumentException e) {
            // PASS.
        } catch (Throwable e) {
            fail();
        }
    }

    @Test
    public void testCalculateTargetAttr() {
        RectF r1 = new RectF(0, 0, 100, 100); // it was center
        RectF r2 = new RectF(0, 0, 100, 200);
        float[] result = Utils.calculateTargetScaleAndOffsetY(r1, r2);
        assertEquals(2, result.length);
        assertEquals(1f, result[0], 0.0001);
        assertEquals(50, result[1], 0.0001);
    }

    @Test
    public void testCalculateTargetAttr00() {
        RectF r1 = new RectF(0, 0, 100, 100);
        RectF r2 = new RectF(0, 500, 100, 600);
        float[] result = Utils.calculateTargetScaleAndOffsetY(r1, r2);
        assertEquals(2, result.length);
        assertEquals(1f, result[0], 0.0001);
        assertEquals(500, result[1], 0.0001);
    }

    @Test
    public void testCalculateTargetAttr01() {
        RectF r1 = new RectF(0, 0, 200, 200);
        RectF r2 = new RectF(0, 0, 100, 100);
        float[] result = Utils.calculateTargetScaleAndOffsetY(r1, r2);
        assertEquals(2, result.length);
        assertEquals(0.5f, result[0], 0.0001);
        assertEquals(-50, result[1], 0.0001);
    }

    @Test
    public void testCalculateTargetAttr02() {
        RectF r1 = new RectF(0, 0, 200, 200);
        RectF r2 = new RectF(0, 0, 100, 100);
        float[] result = Utils.calculateTargetScaleAndOffsetY(r1, r2);
        assertEquals(2, result.length);
        assertEquals(0.5f, result[0], 0.0001);
        assertEquals(-50, result[1], 0.0001);
    }


    @Test
    public void testCalculateTargetAttr1() {
        RectF r1 = new RectF(0, 200, 100, 300);
        RectF r2 = new RectF(0, 100, 100, 500);
        float[] result = Utils.calculateTargetScaleAndOffsetY(r1, r2);
        assertEquals(2, result.length);
        assertEquals(1f, result[0], 0.0001);
        assertEquals(50, result[1], 0.0001);
    }

    @Test
    public void testCalculateTargetAttr2() {
        RectF r1 = new RectF(0, 0, 200, 300);
        RectF r2 = new RectF(0, 0, 200, 200);
        float[] result = Utils.calculateTargetScaleAndOffsetY(r1, r2);
        assertEquals(2, result.length);
        assertEquals(0.66666, result[0], 0.0001);
        assertEquals(-50, result[1], 0.0001);
    }

    @Test
    public void testCalculateTargetAttr3() {
        RectF r1 = new RectF(0, 0, 200, 300);
        RectF r2 = new RectF(0, 300, 200, 500);
        float[] result = Utils.calculateTargetScaleAndOffsetY(r1, r2);
        assertEquals(2, result.length);
        assertEquals(0.66666, result[0], 0.0001);
        assertEquals(250, result[1], 0.0001);
    }
}
