package com.piccollage.util;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.annotation.NonNull;

import com.piccollage.model.Size;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONException;
import org.json.JSONObject;

public class Utils {

    public static final int MAX_FPS = 30;

    private static final long LOW_RAM_THREASHOLD = 512 * 1024 * 1024; // 500 MB
    private static final float MIN_INTERNAL_MEMORY = 1 * 1024 * 1024;//1 MB

    public static String getClassName(Object o) {
        if (o == null) {
            return "";
        }
        Class c = o.getClass();
        if (c.isAnonymousClass()) {
            // For anonymous classes, getSimpleName returns empty string
            return c.getSuperclass().getSimpleName();
        }
        return c.getSimpleName();
    }

    /*
     * Return available internal memory in bytes
     */
    public static float getAvailableInternalMemory() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();

        return availableBlocks * blockSize;
    }

    public static boolean isInLowInternalMemory() {
        return getAvailableInternalMemory() <= MIN_INTERNAL_MEMORY;
    }

    public static boolean exceedDistance(float prevX, float prevY, float x, float y, float threshold) {
        float dx = Math.abs(x - prevX);
        float dy = Math.abs(y - prevY);
        return dx >= threshold || dy >= threshold;
    }

    public static String getLanguageCode(){
        String language = Locale.getDefault().getLanguage();
        if(language.equals("zh")) {
            return language+"-r" + Locale.getDefault().getCountry();
        } else {
            return language;
        }
    }

    public static boolean hitTail(final int total, final int index) {
        if (index < 0 || total < 0) return false;
        int diff = total - index;
        return diff > 0 && diff <= 10;
    }

    public static boolean isLowRamDevice(ActivityManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return manager.isLowRamDevice();
        } else {
            long totalMemory = getTotalMemory(manager);
            return totalMemory > 0 && totalMemory <= LOW_RAM_THREASHOLD;
        }
    }

    private static long getTotalMemory(ActivityManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            MemoryInfo memInfo = new MemoryInfo();
            manager.getMemoryInfo(memInfo);
            return memInfo.totalMem;
        } else {
            // copy from http://stackoverflow.com/questions/12551547/is-there-a-way-to-get-total-device-rami-need-it-for-an-optimization
            String str1 = "/proc/meminfo";
            String str2;
            String[] arrayOfString;
            long initial_memory;
            try {
                FileReader localFileReader = new FileReader(str1);
                BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);
                str2 = localBufferedReader.readLine();//meminfo
                arrayOfString = str2.split("\\s+");
                initial_memory = Integer.valueOf(arrayOfString[1]) * 1024;
                localBufferedReader.close();
                return initial_memory;
            } catch (IOException e) {
                return -1;
            }
        }
    }

    public static float randomBetween(float low, float high) {
        return low + (float) Math.random() * (high - low);
    }

    public static JSONObject convertBundleToJson(Bundle bundle) {
        JSONObject result = new JSONObject();
        if (bundle == null) {
            return result;
        }
        for (String k : bundle.keySet()) {
            try {
                result.put(k, bundle.getString(k));
            } catch (JSONException ignored) {}
        }
        return result;
    }

    public static byte[] hmacSha1(String key, String value) throws NoSuchAlgorithmException, InvalidKeyException {
        String type = "HmacSHA1";
        SecretKeySpec secret = new SecretKeySpec(key.getBytes(), type);
        Mac mac = Mac.getInstance(type);
        mac.init(secret);
        return mac.doFinal(value.getBytes());
    }

    public static byte[] rand(int size) {
        SecureRandom sr = new SecureRandom();
        byte[] output = new byte[size];
        sr.nextBytes(output);
        return output;
    }

    private static final SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'XXX'", Locale.US);
    static {
        sDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static Date stringToDate(String dateString) {
        Date date = null;
        try {
            date = sDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String dateToString(Date date) {
        return sDateFormat.format(date);
    }

    public static float normalizeRadian(float radian) {
        float resultRadian = radian;
        while (resultRadian < 0) resultRadian += 2 * Math.PI;
        while (resultRadian >= 2 * Math.PI) resultRadian -= 2 * Math.PI;
        return resultRadian;
    }

    /**
     * Return the greatest common divisor (GCD).
     * @param a The given number a.
     * @param b The given number b.
     * @return The greatest common divisor.
     */
    public static long getGCD(long a, long b) {
        if ((a > 0 && b < 0) ||
            (a < 0 && b > 0)) {
            throw new IllegalArgumentException("The given two numbers must have the same sign.");
        } else if (a == 0 || b == 0) {
            throw new IllegalArgumentException("The given number must not be a zero.");
        }

        long sign = a > 0 ? 1 : -1;

        // Turn a and b to positive numbers temporarily.
        a = Math.abs(a);
        b = Math.abs(b);

        while (b > 0)
        {
            long temp = b;
            b = a % b;
            a = temp;
        }

        return sign * a;
    }

    /**
     * Return the least common multiple (LCM).
     * @param a The given number a.
     * @param b The given number b.
     * @return The least common multiple.
     */
    public static long getLCM(long a, long b) {
        if ((a > 0 && b < 0) ||
            (a < 0 && b > 0)) {
            throw new IllegalArgumentException("The given two numbers must have the same sign.");
        } else if (a == 0 || b == 0) {
            throw new IllegalArgumentException("The given number must not be a zero.");
        }
        return a * b / getGCD(a, b);
    }

    /**
     * Calculate the best FPS according to the given FPS(s). The best FPS won't exceed MAX_FPS.
     * @param fpsArr The FPS array.
     * @return The best FPS according to the given FPS(s).
     */
    public static int getBestFps(List<Integer> fpsArr) {
        if (fpsArr == null || fpsArr.isEmpty()) {
            throw new IllegalArgumentException("The given FPS list is null or empty.");
        }

        int lcm = fpsArr.get(0);
        for (int i = 1; i < fpsArr.size(); ++i) {
            int current = fpsArr.get(i);

            if (current <= 0) {
                throw new IllegalArgumentException("FPS must be a positive number.");
            } else {
                lcm = (int) getLCM(lcm, current);
            }
        }
        return Math.min(MAX_FPS, lcm);
    }

    public static float[] calculateTargetScaleAndOffsetY(@NonNull RectF rect1, @NonNull RectF rect2) {
        float scale = Math.min(
            rect2.width() / rect1.width(),
            rect2.height() / rect1.height());
        float internalOffsetY = scale == 1f ? 0f : (rect2.height() - (rect1.height())) / 2;
        float externalOffsetY = (rect2.height() / 2) - ((rect1.height() * scale) / 2);
        return new float[] {scale, internalOffsetY + externalOffsetY + rect2.top - rect1.top};
    }

    public static String convertToMegabyte(int totalBytes) {
        return String.format(Locale.ENGLISH, "%.2f MB", totalBytes / (1024 * 1024f));
    }
}
