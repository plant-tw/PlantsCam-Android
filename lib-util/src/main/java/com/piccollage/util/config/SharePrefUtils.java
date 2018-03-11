package com.piccollage.util.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Point;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.piccollage.util.BuildConfig;
import com.piccollage.util.DebugUtils;
import com.piccollage.util.JsonUtils;

import java.util.HashSet;
import java.util.Set;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

/**
 * Created by prada on 12/14/15.
 */
public class SharePrefUtils {
    public static final String NAME_PREFERENCES = "cardinalblue_3";

    // For Vungle
    public static final String NAME_VIDEO_AD_PREFERENCES_LAGECY = "cardinalblue_video_ad_metadata";
    public static final String NAME_VIDEO_AD_PREFERENCES        = "cardinalblue_video_ad_metadata2";
    public static final String KEY_HD_ENABLED                   = "ENABLE_HD";
    public static final boolean DEFAULT_ENABLE_HD               = true;
    public static final String KEY_DEFAULT_BORDER_COLOR         = "BORDER_COLOR";
    public static final String KEY_DEFAULT_HAS_SHADOW           = "HAS_SHADOW";
    public static final String KEY_DEFAULT_HAS_APPLY_ALL        = "HAS_APPLY_ALL";
    public static final String PREF_HAS_NOTIFICATION_BADGE      = "pref_has_notification_badge";
    public static final String PREF_WATERMARK_UNLOCK            = "pref_watermark_unlock";
    public static final String PREF_HAS_STORE_BADGE             = "pref_has_store_badge";

    // For new user
    public static final String KEY_FIRST_OPEN_APP_USER          = "key_first_open_user";
    public static final String KEY_FIRST_VERSION_USER           = "key_first_version_user";
    public static final String KEY_FIRST_SESSION_NEW_USER       = "pref_first_session_user";
    public static final String KEY_BEFORE_EVENT_FIRED_NEW_USER  = "pref_new_user_for_exp";


    // Photo Picker toasts
    public static final String PREF_PROMPT_IN_THUMBNAIL_PICKER  = "pref_prompt_in_thumbnail_picker";
    public static final String PREF_PROMPT_IN_PREVIEW_PICKER    = "pref_prompt_in_preview_picker";

    // Start page experiment
    public static final String PREF_START_PAGE_EXPERIMENT_VARIATION = "pref_start_page_variation";

    // Main flow experiment
    public static final String PREF_MAIN_FLOW_EXPERIMENT_VARIATION = "pref_main_flow_variation";
    public static final String PREF_MAIN_FLOW_FOLLOW_UP_VARIATION  = "pref_main_flow_follow_up";

    public static final int COLLAGE_EXPORT_SIZE_SHARE = 1024;
    // Export collage resolution
    public static final int COLLAGE_EXPORT_SIZE_GALLERY = 2048;
    public static final String PREF_COLLAGE_CURRENT_ID = "pref_current_collage_id";

    public static final String PREF_IS_MIGRATE_PURCHASE_ITEMS = "perf_is_migrate_purchase_items";
    public static final String PREF_BRAND_BUNDLES = "pref_brand_bundles";
    public static final String PREF_TARGET_PC_AUTHORITY = "pref_target_pc_authority";

    // Debug experiment variation selection
    public static final String PREF_TARGET_EXPERIMENT_VARIATION = "pref_target_experiment_variation";
    public static final String KEY_DEFAULT_VARIATION = Consts.DEFAULT;
    public static final String KEY_NOT_USE_DEBUG = "Control by remote config";

    public static final String PREF_FCM_TOKEN = "FCM token";

    public static final String KEY_IS_ADS_SHOWN = "is_ads_shown";
    public static final String KEY_IS_PAID_USER = "is_paid_user";

    private static Subject<Boolean> sNotiBadgeSub = BehaviorSubject.<Boolean>create().toSerialized();
    private static BehaviorSubject<Boolean> sStoreBadgeSub = BehaviorSubject.create();

    /**
     * SharedPreference
     */
    public static SharedPreferences getSharedPreferences(Context applicationContext) {
        return getSharedPref(applicationContext, NAME_PREFERENCES);
    }

    public static void registerOnChangeListener(Context applicationContext) {
        getSharedPreferences(applicationContext).registerOnSharedPreferenceChangeListener(sPrefChangeListener);
    }

    public static void unregisterOnChangeListener(Context applicationContext) {
        getSharedPreferences(applicationContext).unregisterOnSharedPreferenceChangeListener(sPrefChangeListener);
    }

    // clean the key if the value is empty string
    public static void setString(Context applicationContext, String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferences(applicationContext).edit();
        if (TextUtils.isEmpty(value)) {
            editor.remove(key);
        } else {
            editor.putString(key, value);
        }
        editor.apply();
    }

    public static void storeTimestampForStickerBundle(Context applicationContext, String bundleId) {
        long t = System.currentTimeMillis();
        getSharedPref(applicationContext, NAME_VIDEO_AD_PREFERENCES).edit().putLong(bundleId, t).apply();
    }

    public static SharedPreferences getSharedPref(Context applicationContext, String name) {
        // after we let editor runs on its process #2889. we can not access the
        // same shared preference (after android v2.3.x). we should put the type
        // Context.MODE_MULTI_PROCESS to avoid this issue
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return applicationContext.getSharedPreferences(
                    name, Context.MODE_MULTI_PROCESS);
        } else {
            return applicationContext.getSharedPreferences(
                    name, Context.MODE_PRIVATE);
        }
    }

    public static boolean isHDEnabled(Context applicationContext) {
        return getSharedPreferences(applicationContext).getBoolean(KEY_HD_ENABLED, DEFAULT_ENABLE_HD);
    }

    public static void setHasNotificationBadge(Context applicationContext, boolean hasBadge) {
        setBoolean(applicationContext, PREF_HAS_NOTIFICATION_BADGE, hasBadge);
    }

    public static void setPrefHasStoreBadge(Context applicationContext, boolean hasStoreBadge) {
        setBoolean(applicationContext, PREF_HAS_STORE_BADGE, hasStoreBadge);
    }

    public static Observable<Boolean> hasStoreBadge(final Context applicationContext) {
        Observable
            .fromCallable(new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    Boolean enabled = getSharedPreferences(applicationContext).getBoolean("pref_has_store_badge", true);
                    sStoreBadgeSub.onNext(enabled);

                    return enabled;
                }
            })
            .subscribeOn(Schedulers.io())
            .subscribe();
        return sStoreBadgeSub;
    }

    public static boolean hasStoreBadgeSync() {
        return sStoreBadgeSub.getValue();
    }


    public static boolean isAddWatermark(Context applicationContext) {
        // this flag only for the amazon underground program
        if (BuildConfig.FREE_DOWNLOAD_PAID_BUNDLE_ENABLE) {
            return false;
        }
        if (!BuildConfig.IN_APP_PURCHASE_ENABLE) {
            return true;
        }
        return !getSharedPreferences(applicationContext).getBoolean(PREF_WATERMARK_UNLOCK, false);
    }

    // in this version, we improve the low-resolution device have chance to improve resolution to 1024x1024.
    // for example : your device resolution is 320x480, we will create a canvas with 683x1024.
    // but your device resolution is 960x1280, it will keep the same resolution.
    // TODO support different resolution for different target (for example : gallery should increase to 2048x2048).
    public static Point getCapturingStaticImageSize(Context applicationContext, int collageWidth, int collageHeight) {
        if (!isHDEnabled(applicationContext)) {
            return new Point(collageWidth, collageHeight);
        }
        int len = Math.max(collageWidth, collageHeight);
        float scale = Math.max(1.0f, (float) (COLLAGE_EXPORT_SIZE_SHARE) / len);
        return new Point((int)(collageWidth * scale), (int)(collageHeight * scale));
    }

    public static void setBoolean(Context applicationContext, String key, boolean value) {
        getSharedPreferences(applicationContext).edit().putBoolean(key, value).apply();
    }

    public static boolean getBoolean(Context applicationContext, String key, boolean defaultValue) {
        return getSharedPreferences(applicationContext).getBoolean(key, defaultValue);
    }

    /**
     * Save current collage id to SharedPreference.
     * @param collageId - id of current collage
     */
    public static void saveCurrentCollageId(Context context, long collageId) {
        SharedPreferences preferences = context.getSharedPreferences(
                NAME_PREFERENCES, Context.MODE_PRIVATE);
        if (preferences != null) {
            preferences.edit().putLong(PREF_COLLAGE_CURRENT_ID, collageId).apply();
        }
    }

    /**
     * Get the saved index from SharedPreference.
     * @return id - index of current collage or -1 if error occurs
     */
    public static long getSavedCurrentCollageId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                NAME_PREFERENCES, Context.MODE_PRIVATE);
        long collageId = Consts.NEW_COLLAGE_ID;
        if (preferences != null) {
            collageId = preferences.getLong(PREF_COLLAGE_CURRENT_ID, collageId);
        }
        return collageId;
    }

    private static OnSharedPreferenceChangeListener sPrefChangeListener = new OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs,
                                              String key) {
            // Currently, we only support the notification badge.
            if (key.equalsIgnoreCase(PREF_HAS_NOTIFICATION_BADGE)) {
                sNotiBadgeSub.onNext(prefs.getBoolean(key, true));
            } else if (key.equalsIgnoreCase(PREF_HAS_STORE_BADGE)) {
                sStoreBadgeSub.onNext(prefs.getBoolean(key, true));
            }
        }
    };

    public static String getString(Context applicationContext, String key) {
        return getSharedPreferences(applicationContext).getString(key, null);
    }

    public static Map<String, String> getMap(Context ctx, String key) {
        String str = getSharedPreferences(ctx).getString(key, "");
        try {
            return JsonUtils.toMap(new JSONObject(str));
        } catch (JSONException e) {
            return new HashMap<>();
        }
    }

    public static void setMap(Context ctx, String key, Map<String, String> value) {
        getSharedPreferences(ctx).edit().putString(key, new JSONObject(value).toString()).apply();
    }

    public static boolean isNewUser(Context ctx, String type) {
        return isNewUser(ctx, type, true);
    }

    /**
     * If a user is new user or set debugNewUser to true
     * The logic here is tied to {@Link HomeActivity#upgradePreferences()}
     * @param ctx
     * @Param type : indicate the definition of a new user.
     * @return isNewUser
     */
    public static boolean isNewUser(Context ctx, String type, Boolean debugNewUserEnabled) {
        final boolean newUser;
        switch (type) {
            case KEY_FIRST_OPEN_APP_USER:
                newUser = isFirstTimeOpenAppNewUser(ctx);
                break;
            case KEY_FIRST_VERSION_USER:
                newUser = isVersionNewUser(ctx);
                break;
            case KEY_FIRST_SESSION_NEW_USER:
                newUser = isFirstSessionNewUser(ctx);
                break;
            case KEY_BEFORE_EVENT_FIRED_NEW_USER:
                newUser = isNotFireNewUserEventNewUser(ctx);
                break;
            default:
                newUser = false;
        }

        // enable debug new user mode
        if (!debugNewUserEnabled) {
            return newUser;
        } else {
            return newUser || isDebugNewUser(ctx);
        }
    }

    private static boolean isDebugNewUser(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(
            DebugUtils.PREF_KEY_DEBUG_NEW_USER, false);
    }

    private static boolean isVersionNewUser(Context ctx) {
        // Version code set always contains value '0', so there should be two
        // code for new user in the set.
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        Set<String> versionHistory = pref.getStringSet(Consts.VERSION_CODE_HISTORY, new HashSet<String>());
        return versionHistory.size() <= 2;
    }

    private static boolean isFirstSessionNewUser(Context ctx) {
        return getSharedPreferences(ctx).getBoolean(
            KEY_FIRST_SESSION_NEW_USER, true);
    }

    private static boolean isNotFireNewUserEventNewUser(Context ctx) {
        // Since we are doing a lot of first time user related event but
        // the timing of firing it is hard to control, we use a key to record
        // it is fired.
        return getSharedPreferences(ctx).getBoolean(KEY_BEFORE_EVENT_FIRED_NEW_USER, Consts.NEW_USER_FOR_EXP_DEFAULT);
    }

    private static boolean isFirstTimeOpenAppNewUser(Context ctx) {
        // When the second time user enter {@Link HomeActivity}, the
        // version code will be written into sharePref.
        // In DKA mode, a new user -> background -> foreground will
        // become NOT a new user.
        int lastVersion = getSharedPreferences(ctx)
            .getInt(Consts.VERSION_CODE, Consts.DEFAULT_VERSION_CODE);
        return (lastVersion == Consts.DEFAULT_VERSION_CODE);
    }

    public static boolean isOneTimeFlagSet(Context ctx, String key) {
        return getSharedPreferences(ctx).getBoolean(key, false);
    }

    public static void turnOffOneTimeFlag(Context ctx, String key) {
        getSharedPreferences(ctx).edit().putBoolean(key, true).apply();
    }

    public static boolean isDebugExperimentSelection(Context ctx) {
        String variation = SharePrefUtils
            .getSharedPreferences(ctx).getString(SharePrefUtils.PREF_TARGET_EXPERIMENT_VARIATION,
                                                 KEY_NOT_USE_DEBUG);
        return !variation.equalsIgnoreCase(KEY_NOT_USE_DEBUG);
    }

    public static String debugExperimentSelection(Context ctx) {
        return SharePrefUtils
            .getSharedPreferences(ctx).getString(SharePrefUtils.PREF_TARGET_EXPERIMENT_VARIATION,
                                                 KEY_NOT_USE_DEBUG);
    }

    public static int getDebugExperimentIndex(Context ctx) {
        String pref = SharePrefUtils
            .getSharedPreferences(ctx).getString(SharePrefUtils.PREF_TARGET_EXPERIMENT_VARIATION,
                                                 KEY_NOT_USE_DEBUG);
        switch (pref) {
            case "A":
                return 1;
            case "B":
                return 2;
            case "C":
                return 3;
            case "D":
                return 4;
            case "E":
                return 5;
            case "F":
                return 6;
            case "G":
                return 7;
            case "H":
                return 8;
            case "I":
                return 9;
            case "J":
                return 10;
            case "K":
                return 11;
            case "L":
                return 12;
            case "M":
                return 13;
            case "N":
                return 14;
            case "O":
                return 15;
            case "P":
                return 16;
            case Consts.DEFAULT:
            case KEY_NOT_USE_DEBUG:
            default:
                return 0;
        }
    }
}
