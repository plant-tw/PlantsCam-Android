package com.piccollage.util.protocol;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import java.util.Map;

/**
 * Created by luyolung on 17/05/2017.
 */

public interface ILogEvent {
    /**
     * startActivity and stopActivity also trigger startSession and stopSession
     */
    void startActivity(Activity activity, String classname);
    void stopActivity(Activity activity, String classname);

    void startSession(Context ctx);
    void stopSession(Context ctx);

    void log(String message);
    void sendEvent(String action);
    void sendEvent(String action, Map<String, String> params);
    void sendEvent(String action, String... parameters);
    void sendEvent(Context cxt, String action, String... parameters);
    void sendException(Throwable ex);

    void sendFbEvent(Context context, String action, String contentId);

    void logStartFromNotification(Intent intent);
    void logForAppLinkRoute(Intent intent);
    void sendFbActivateApp(Context context);

    void createActivity(Activity activity);
    void updateDefaultUserProperties();
    void setupVersionHistory(Context context);
}
