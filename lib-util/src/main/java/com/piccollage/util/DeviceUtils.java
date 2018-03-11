package com.piccollage.util;

import android.os.Build;

/**
 * Created by apple on 2015-03-26.
 */
public class DeviceUtils {
    private static final String MANUFACTURER_AMAZON = "Amazon";
    private static final String MODEL_AMAZON_CELLO = "SD4930UR";

    public static boolean isAmazon() {
        return MANUFACTURER_AMAZON.equals(Build.MANUFACTURER);
    }

    public static boolean isAmazonCello() {
        return isAmazon() && MODEL_AMAZON_CELLO.equals(Build.MODEL);
    }

    // Copied from https://support.google.com/googleplay/answer/1727131
    private static final String[] SAMSUNG_S3_DEVICES = new String[] {
            "SC-03E",
            "c1att",
            "c1ktt",
            "c1lgt",
            "c1skt",
            "d2att",
            "d2can",
            "d2cri",
            "d2dcm",
            "d2lteMetroPCS",
            "d2lterefreshspr",
            "d2ltetmo",
            "d2mtr",
            "d2spi",
            "d2spr",
            "d2tfnspr",
            "d2tfnvzw",
            "d2tmo",
            "d2usc",
            "d2vmu",
            "d2vzw",
            "d2xar",
            "m0",
            "m0apt",
            "m0chn",
            "m0cmcc",
            "m0ctc",
            "m0ctcduos",
            "m0skt",
            "m3",
            "m3dcm",
    };

    public static boolean IsSamsungS3 = isSamsungS3();
    public static boolean isSamsungS3() {
        if (!Build.MANUFACTURER.contains("samsung")) {
            return false;
        }

        String d = Build.DEVICE;
        for (String s : SAMSUNG_S3_DEVICES) {
            if (s.equals(d)) {
                return true;
            }
        }

        return false;
    }

    // Copied from https://support.google.com/googleplay/answer/1727131
    private static final String[] SAMSUNG_NOTE2_DEVICES = new String[] {
            "SC-02E",
            "t03g",
            "t03g",
            "t03gchn",
            "t03gchnduos",
            "t03gcmcc",
            "t03gctc",
            "t03gcuduos",
            "t0lte",
            "t0lteatt",
            "t0ltecan",
            "t0ltecmcc",
            "t0ltedcm",
            "t0ltektt",
            "t0ltelgt",
            "t0lteskt",
            "t0ltespr",
            "t0ltetmo",
            "t0lteusc",
            "t0ltevzw",
    };

    public static boolean IsSamsungNote2 = isSamsungNote2();
    public static boolean isSamsungNote2() {
        if (!Build.MANUFACTURER.contains("samsung")) {
            return false;
        }

        String d = Build.DEVICE;
        for (String s : SAMSUNG_NOTE2_DEVICES) {
            if (s.equals(d)) {
                return true;
            }
        }

        return false;
    }
}
