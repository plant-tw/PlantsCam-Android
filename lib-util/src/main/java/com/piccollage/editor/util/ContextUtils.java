package com.piccollage.editor.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Build.VERSION;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.piccollage.util.UuidFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ContextUtils {

    private static final String TAG = ContextUtils.class.getSimpleName();
    public static final String osName = "Android";

    public static String getVersionName(Context ctx) {
        try {
            return ctx.getPackageManager().getPackageInfo(ctx.getPackageName(),
                    0).versionName;
        } catch (NameNotFoundException exc) {
            return "version unavailable";
        }
    }

    public static String getBundleIdentifier(Context context) {
        return "android-" + getVersionName(context);
    }

    public static String getOSName() {
        return osName;
    }

    public static String getOSVersion() {
        return VERSION.RELEASE;
    }

    public static String getDeviceModel() {
        return Build.MODEL;
    }

    public static String getFullIdentifier(Context ctx) {
        Resources R = ctx.getResources();
        String appName = R.getText(
                R.getIdentifier("application_name", "string", ctx.getPackageName()))
                .toString();
        String version = getVersionName(ctx);

        return appName + " " + version + " (Android " + VERSION.RELEASE
                + ", " + Build.BRAND + " " + Build.DEVICE + "/" + Build.DISPLAY
                + "/" + Build.MODEL + ")";
    }

    public static AlertDialog popupMessage(Context ctx, String title,
            String contents, OnClickListener ok) {
        AlertDialog dlg = new AlertDialog.Builder(ctx).setTitle(title)
                .setMessage(contents)
                .setPositiveButton(android.R.string.ok, ok).setCancelable(true)
                .create();
        dlg.show();
        return dlg;
    }

    public static AlertDialog popupMessage(Context ctx, String title,
            String contents) {
        return popupMessage(ctx, title, contents, null);
    }

    public static AlertDialog popupMessage(Context ctx, int title,
            String contents, OnClickListener ok) {
        return popupMessage(ctx, ctx.getString(title), contents, ok);
    }

    public static AlertDialog popupMessage(Context ctx, int title,
            String contents) {
        return popupMessage(ctx, title, contents, null);
    }

    public static float getDensity(Context context) {
        return getDisplayMetrics(context).density;
    }
    public static DisplayMetrics getDisplayMetrics(Context context) {
        return context.getResources().getDisplayMetrics();
    }

    /**
     * This method requires ACCESS_NETWORK_STATE permission
     *
     * @param context
     *
     * @return it has internet connection if it's true
     */
    public static boolean hasInternetConnection(@NonNull Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) {
            return false;
        }

        NetworkInfo mActiveNetworkInfo = cm.getActiveNetworkInfo();
        return (mActiveNetworkInfo != null
            && mActiveNetworkInfo.isAvailable()
            && mActiveNetworkInfo.isConnected());
    }

    public static String getUuid(Context context) {
        // always get id from UuidFactory
        UuidFactory uuidFactory = new UuidFactory(context);
        return uuidFactory.getDeviceUuid().toString();
    }

    private static CookieManager getCookieManager(Context context) {
        CookieSyncManager.createInstance(context);
        return CookieManager.getInstance();
    }

    public static void enableCookies(Context context) {
        getCookieManager(context).setAcceptCookie(true);
    }

    public static void removeAllCookies(Context context) {
        getCookieManager(context).removeAllCookie();
    }

    public static void clearCookiesSafely(Context context, String domain) {
        // setCookie acts differently when trying to expire cookies between builds of Android that are using
        // Chromium HTTP stack and those that are not. Using both of these domains to ensure it works on both.
        clearCookies(context, domain);
        clearCookies(context, "." + domain);
        clearCookies(context, "https://" + domain);
        clearCookies(context, "https://." + domain);
    }

    public static void clearCookies(Context context, String domain) {
        CookieManager cookieManager = getCookieManager(context);
        String cookieString = cookieManager.getCookie(domain);

        // Check if any cookie found
        if (cookieString == null) {
            return;
        }
        // Clear cookies
        String[] cookie = cookieString.split("; ");
        for (int i=0; i<cookie.length; i++){
            String[] cookiekv = cookie[i].split("=");
            if (Arrays.asList(cookiekv).size() == 2) {
                cookieManager.setCookie(domain,
                    cookiekv[0]+ "=; expires=Sat, 1 Jan 2000 00:00:01 UTC");
            }
        }
        cookieManager.removeExpiredCookie();
    }

    public static boolean isActivityRunning(Activity activity) {
        return activity != null && !activity.isFinishing();
    }

    public static void showToast(Activity activity, int messageId, int length) {
        if (isActivityRunning(activity)) {
            Toast.makeText(activity, messageId, length).show();
        }
    }

    public static void showToast(Activity activity, String str, int length) {
        if (isActivityRunning(activity)) {
            Toast.makeText(activity, str, length).show();
        }
    }

    public static void showToast(Context context, String message, int length) {
        if (context instanceof Activity) {  // the context should be Activity
           showToast((Activity) context, message, length);
        }
    }

    public static void showToast(Context context, int messageId, int length) {
        if (context instanceof Activity) {  // the context should be Activity
            showToast((Activity) context, messageId, length);
        }
    }

    /**
     * Check if device has camera related app installed
     * @param context
     * @return true if has app, false otherwise
     */
    public static boolean hasCapturingApp(Context context) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        PackageManager pm = context.getPackageManager();
        if (pm == null) return false;
        List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);
        return list.size() > 0;
    }

    // This snippet hides the system bars.
    public static void hideSystemUI(View decorView) {
        if (decorView == null) return;
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        decorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    // This snippet shows the system bars. It does this by removing all the flags
    // except for the ones that make the content appear under the system bars.
    public static void showSystemUI(View decorView) {
        if (decorView == null) return;
        decorView.setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    private static final String PARAM_OS_NAME = "os_name";
    private static final String PARAM_DEVICE_MODEL = "device_model";
    private static final String PARAM_LOCALE = "locale";
    private static final String PARAM_LANGUAGE = "language";
    private static final String PARAM_COUNTRY_CODE = "country_code";
    private static final String PARAM_APP_VERSION = "app_version";
    private static final String PARAM_OS_VERSION = "os_version";
    private static final String PARAM_CB_DEVICE_ID = "cb_device_id";
    private static final String PARAM_DEVICE_WH_DPI = "dpi";
    private static final String PARAM_BUNDLE_IDENTIFIER = "bundle_identifier";
    private static final String FORMAT_DIMENSION = "%dx%d";

    public static JsonObject prepareDeviceInfoForPublish(Context ctx) {
        JsonObject json = new JsonObject();

        json.addProperty(PARAM_APP_VERSION, getVersionName(ctx));
        json.addProperty(PARAM_BUNDLE_IDENTIFIER, ctx.getPackageName());
        json.addProperty(PARAM_CB_DEVICE_ID, getUuid(ctx));
        json.addProperty(PARAM_DEVICE_MODEL, getDeviceModel());
        json.addProperty(PARAM_OS_NAME, getOSName());
        json.addProperty(PARAM_OS_VERSION, getOSVersion());

        Configuration config = ctx.getResources().getConfiguration();
        json.addProperty(PARAM_DEVICE_WH_DPI, String.format(Locale.US,
            FORMAT_DIMENSION, config.screenWidthDp, config.screenHeightDp));

        final String language = Locale.getDefault().getLanguage();
        final String countryCode = Locale.getDefault().getCountry();

        json.addProperty(PARAM_LOCALE, language + "_" + countryCode);
        json.addProperty(PARAM_LANGUAGE, language);
        json.addProperty(PARAM_COUNTRY_CODE, countryCode);

        return json;
    }
}
