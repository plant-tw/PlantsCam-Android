package com.piccollage.util;

import android.content.Context;
import com.piccollage.util.config.SharePrefUtils;

/**
 * Created by jimytc on 4/16/15.
 */
public final class DebugUtils {
    public static final long DEFAULT_PAID_STICKER_EXPIRED_DURATION = 3 * 24 * 60 * 60 * 1000; // 3 day

    /** Preference for debug mode enabled or not */
    public static final String PREF_KEY_DEBUG_MODE_ENABLED              = "pref_debug_mode_enabled";
    public static final String PREF_KEY_NSLOGGER                        = "pref_key_nslogger";
    public static final String PREF_KEY_DEBUG_COLLAGE_PANEL             = "pref_key_debug_collage_panel";
    public static final String PREF_KEY_DEBUG_BRIDGE                    = "pref_key_debug_bridge";
    public static final String PREF_KEY_NOTIFICATION_LOG                = "pref_key_notification_log";
    public static final String PREF_KEY_LIGHT_NOTIFICATION_BADGE        = "pref_key_light_notification_badge";
    public static final String PREF_KEY_DEBUG_NEW_USER                  = "pref_key_debug_new_user";
    /**
     * The highest priority to determine whether to show the ADs.
     */
    public static final String PREF_KEY_HIDE_ADS_SWITCH                 = "pref_key_hide_ads_switch";
    public static final String PREF_KEY_ADS_TEST_MODE                   = "pref_key_ads_test_mode";
    public static final String PREF_KEY_DFP_AD_UNIT_ID                  = "pref_key_dfp_ad_unit_id";
    public static final String PREF_KEY_COLORFUL_GRID_OPTION            = "pref_key_colorful_grid_option";

    /** preference key for setup WebView debug, it's API 19+ only */
    public static final String PREF_KEY_WEBVIEW_DEBUG_ENABLED           = "pref_key_webview_debug_enabled";

    /**
     * The experimental snap-to-object feature (see #7479 thread and
     * 7479_snap_to_object branch).
     */
    public static final String PREF_KEY_SNAP_TO_OBJECT                  = "pref_key_snap_to_object";
    /**
     * The experimental cutout-path-highlight feature (see#7493 thread
     * and 7493_polygon_highlight)
     */
    public static final String PREF_KEY_CUTOUT_PATH_HIGHLIGHT           = "pref_key_cutout_path_highlight_for_drop_zone";

    public static final String PREF_KEY_FORCE_LOAD_GALLERY_BANNER       = "pref_force_load_gallery_banner";
    public static final String PREF_KEY_TEST_PAID_STICKER_EXPIRE        = "pref_key_test_paid_sticker_expire_time";
    public static final String PREF_KEY_TEST_UNFINISHED_NOTI_DELAY_MS   = "pref_key_test_unfinished_notification_delay_milliseconds";
    public static final String PREF_KEY_TEST_UNCREATED_NOTI_DELAY_MS    = "pref_key_test_uncreated_notification_delay_milliseconds";
    public static final String PREF_KEY_PAID_STICKER_EXPIRE             = "pref_key_paid_sticker_expire_time";
    public static final String PREF_KEY_PRINT_SANDBOX_MODE              = "pref_key_print_sandbox_mode";

    public static boolean debugModeEnabled(Context applicationContext) {
        return SharePrefUtils.getSharedPreferences(applicationContext).getBoolean(PREF_KEY_DEBUG_MODE_ENABLED, false);
    }

    public static void setDebugMode(Context applicationContext, boolean enable) {
        SharePrefUtils.getSharedPreferences(applicationContext).edit()
                .putBoolean(PREF_KEY_DEBUG_MODE_ENABLED, enable).apply();
    }

    public static boolean isNsloggerDebugEnabled(Context applicationContext) {
        return SharePrefUtils.getSharedPreferences(applicationContext).getBoolean(PREF_KEY_NSLOGGER, false);
    }

    public static boolean isDebugCollagePanelEnabled(Context applicationContext) {
        return SharePrefUtils.getSharedPreferences(applicationContext).getBoolean(PREF_KEY_DEBUG_COLLAGE_PANEL, false);
    }

    public static boolean isDebugBridgeEnabled(Context applicationContext) {
        return SharePrefUtils.getSharedPreferences(applicationContext).getBoolean(PREF_KEY_DEBUG_BRIDGE, false);
    }

    public static boolean isNotificationLogEnabled(Context applicationContext) {
        return SharePrefUtils.getSharedPreferences(applicationContext).getBoolean(PREF_KEY_NOTIFICATION_LOG, false);
    }

    public static boolean ifHideAllTheADs(Context applicationContext) {
        return SharePrefUtils.getSharedPreferences(applicationContext).getBoolean(PREF_KEY_HIDE_ADS_SWITCH, false);
    }

    public static boolean isAdTestModeEnabled(Context applicationContext) {
        return SharePrefUtils.getSharedPreferences(applicationContext).getBoolean(PREF_KEY_ADS_TEST_MODE, false);
    }

    public static boolean isWebViewDebugEnabled(Context applicationContext) {
        return SharePrefUtils.getSharedPreferences(applicationContext).getBoolean(PREF_KEY_WEBVIEW_DEBUG_ENABLED, false);
    }

    public static boolean isForceLoadGalleryBanner(Context applicationContext) {
        return SharePrefUtils.getSharedPreferences(applicationContext).getBoolean(PREF_KEY_FORCE_LOAD_GALLERY_BANNER, false);
    }

    public static boolean isPrintSdkSandboxModeEnabled(Context applicationContext) {
        return SharePrefUtils.getSharedPreferences(applicationContext).getBoolean(PREF_KEY_PRINT_SANDBOX_MODE, false);
    }

    public static boolean isDisplayColorfulGridOption(Context applicationContext) {
        return SharePrefUtils.getSharedPreferences(applicationContext).getBoolean(PREF_KEY_COLORFUL_GRID_OPTION, false);
    }

    public static long getExpiredDuration(Context applicationContext) {
        return SharePrefUtils.getSharedPreferences(applicationContext).getLong((debugModeEnabled(applicationContext)) ?
                PREF_KEY_TEST_PAID_STICKER_EXPIRE : PREF_KEY_PAID_STICKER_EXPIRE, DEFAULT_PAID_STICKER_EXPIRED_DURATION);
    }

    public static boolean isDebugNewUserEnabled(Context applicationContext) {
        return SharePrefUtils.getSharedPreferences(applicationContext).getBoolean(PREF_KEY_DEBUG_NEW_USER, false);
    }
}
