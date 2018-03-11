package com.piccollage.util;

import android.content.Context;
import android.content.res.Configuration;
import android.text.TextUtils;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.JsonObject;
import com.piccollage.editor.util.ContextUtils;

import org.apache.http.conn.util.InetAddressUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by luyolung on 09/08/2017.
 */

public class DeviceInfoUtils {

    // Params for device info
    private static final String PARAM_UUID = "uuid";
    private static final String PARAM_IP = "ip";
    private static final String PARAM_OS_NAME = "os_name";
    private static final String PARAM_DEVICE_MODEL = "device_model";
    private static final String PARAM_LOCALE = "locale";
    private static final String PARAM_LANGUAGE = "language";
    private static final String PARAM_COUNTRY_CODE = "country_code";
    private static final String PARAM_APP_VERSION = "app_version";
    private static final String PARAM_OS_VERSION = "os_version";
    private static final String PARAM_GCM_REGISTRATION_ID = "gcm_registration_id";
    private static final String PARAM_OPERATING_SYSTEM = "operating_system";
    private static final String PARAM_CB_DEVICE_ID = "cb_device_id";
    private static final String PARAM_DEVICE_WH_DPI = "dpi";
    private static final String PARAM_BUNDLE_IDENTIFIER = "bundle_identifier";
    private static final String FORMAT_DIMENSION = "%dx%d";

    /**
     * Gets device info, with GCM registration id to be loaded from
     * GCMRegistrar.
     *
     * @return result from
     *         {@link #getDeviceInfo(Context, boolean, String)}
     *
     * @throws IOException
     *             when context is null
     */
    public static List<BasicNameValuePair> getDeviceInfo(Context context,
        boolean hasBundleIdentifier) throws IOException {

        if (context == null) {
            throw new IOException("Context can't be null");
        }
        String token = null;
        try {
            token = FirebaseInstanceId.getInstance().getToken();
        } catch (Throwable ignored) {}
        return getDeviceInfo(context, hasBundleIdentifier, token);
    }

    /**
     * Gets device info.
     *
     * @param token to be included in the list
     *
     * @return a list contains all device info to be sent to PC API
     *
     * @throws IOException
     *             when context is null
     */
    public static List<BasicNameValuePair> getDeviceInfo(Context context,
        boolean hasBundleIdentifier, String token)
        throws IOException {
        if (context == null) {
            throw new IOException("Context can't be null");
        }

        ArrayList<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(PARAM_UUID, ContextUtils.getUuid(context)));

        final String language = Locale.getDefault().getLanguage();
        final String countryCode = Locale.getDefault().getCountry();
        params.add(new BasicNameValuePair(PARAM_LOCALE, language + "_" + countryCode));

        params.add(new BasicNameValuePair(PARAM_LANGUAGE, language));
        params.add(new BasicNameValuePair(PARAM_COUNTRY_CODE, countryCode));

        params.add(new BasicNameValuePair(PARAM_OS_NAME, ContextUtils.getOSName()));
        params.add(new BasicNameValuePair(PARAM_OS_VERSION, ContextUtils.getOSVersion()));
        params.add(new BasicNameValuePair(PARAM_DEVICE_MODEL, ContextUtils.getDeviceModel()));
        params.add(new BasicNameValuePair(PARAM_DEVICE_WH_DPI, getDeviceWHDpi(context)));

        params.add(new BasicNameValuePair(PARAM_APP_VERSION, ContextUtils.getVersionName(context)));
        params.add(new BasicNameValuePair(PARAM_OPERATING_SYSTEM, "Android"));
        params.add(new BasicNameValuePair(PARAM_CB_DEVICE_ID, ContextUtils.getUuid(context)));

        if (!TextUtils.isEmpty(token)) {
            params.add(new BasicNameValuePair(PARAM_GCM_REGISTRATION_ID, token));
        }
        if (hasBundleIdentifier) {
            params.add(new BasicNameValuePair(PARAM_BUNDLE_IDENTIFIER,
                ContextUtils.getBundleIdentifier(context)));
        }
        return params;
    }

    /**
     * Gets device info for record exception.
     *
     * @return a list contains device info to be sent to fabric
     *
     * @throws IOException
     *             when context is null
     */
    public static List<BasicNameValuePair> getDeviceInfoForErrorMsg(Context context,
        boolean hasBundleIdentifier) throws IOException {

        if (context == null) {
            throw new IOException("Context can't be null");
        }

        ArrayList<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(PARAM_UUID, ContextUtils.getUuid(context)));
        params.add(new BasicNameValuePair(PARAM_IP, getIPAddress()));

        final String language = Locale.getDefault().getLanguage();
        final String countryCode = Locale.getDefault().getCountry();
        params.add(new BasicNameValuePair(PARAM_LOCALE, language + "_" + countryCode));

        params.add(new BasicNameValuePair(PARAM_LANGUAGE, language));
        params.add(new BasicNameValuePair(PARAM_COUNTRY_CODE, countryCode));

        params.add(new BasicNameValuePair(PARAM_OS_NAME, ContextUtils
            .getOSName()));
        params.add(new BasicNameValuePair(PARAM_OS_VERSION, ContextUtils
            .getOSVersion()));
        params.add(new BasicNameValuePair(PARAM_DEVICE_MODEL, ContextUtils.getDeviceModel()));
        params.add(new BasicNameValuePair(PARAM_DEVICE_WH_DPI, getDeviceWHDpi(context)));

        params.add(new BasicNameValuePair(PARAM_APP_VERSION, ContextUtils
            .getVersionName(context)));

        if (hasBundleIdentifier) {
            params.add(new BasicNameValuePair(PARAM_BUNDLE_IDENTIFIER,
                ContextUtils.getBundleIdentifier(context)));
        }

        return params;
    }

    public static JsonObject prepareDeviceInfoForPublish(Context context) {
        JsonObject json = new JsonObject();

        json.addProperty(PARAM_APP_VERSION, ContextUtils.getVersionName(context));
        json.addProperty(PARAM_BUNDLE_IDENTIFIER, context.getPackageName());
        json.addProperty(PARAM_CB_DEVICE_ID, ContextUtils.getUuid(context));
        json.addProperty(PARAM_DEVICE_MODEL, ContextUtils.getDeviceModel());
        json.addProperty(PARAM_DEVICE_WH_DPI, getDeviceWHDpi(context));
        json.addProperty(PARAM_OS_NAME, ContextUtils.getOSName());
        json.addProperty(PARAM_OS_VERSION, ContextUtils.getOSVersion());

        final String language = Locale.getDefault().getLanguage();
        final String countryCode = Locale.getDefault().getCountry();
        json.addProperty(PARAM_LOCALE, language + "_" + countryCode);
        json.addProperty(PARAM_LANGUAGE, language);
        json.addProperty(PARAM_COUNTRY_CODE, countryCode);

        return json;
    }

    private static String getDeviceWHDpi(Context ctx) {
        Configuration config = ctx.getResources().getConfiguration();
        return String.format(Locale.US, FORMAT_DIMENSION, config.screenWidthDp, config.screenHeightDp);
    }

    private static String getIPAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface : interfaces) {
                List<InetAddress> address = Collections.list(networkInterface.getInetAddresses());
                for (InetAddress addr : address) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();

                        // TODO: IPv4 and IPv6 could be available at the same time
                        // TODO: need better way to ensure we get right IP
                        boolean useIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean useIPv6 = InetAddressUtils.isIPv6Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else if (useIPv6) {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignore) { } // for now eat exceptions
        return "";
    }
}
