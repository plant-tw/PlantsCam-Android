package com.piccollage.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.piccollage.util.protocol.ILogEvent;

import java.util.Map;

public class MockEventLogger implements ILogEvent {

    @Override
    public void startActivity(Activity activity, String classname) {
        // DO NOTHING.
    }

    @Override
    public void stopActivity(Activity activity, String classname) {
        // DO NOTHING.
    }

    @Override
    public void startSession(Context ctx) {
        // DO NOTHING.
    }

    @Override
    public void stopSession(Context ctx) {
        // DO NOTHING.
    }

    @Override
    public void log(String message) {
        // DO NOTHING.
    }

    @Override
    public void sendEvent(String action) {
        // DO NOTHING.
    }

    @Override
    public void sendEvent(String action, Map<String, String> params) {
        // DO NOTHING.
    }

    @Override
    public void sendEvent(String action, String... parameters) {
        // DO NOTHING.
    }

    @Override
    public void sendEvent(Context cxt, String action, String... parameters) {
        // DO NOTHING.
    }

    @Override
    public void sendException(Throwable ex) {
        // DO NOTHING.
    }

    @Override
    public void sendFbEvent(Context context, String action, String contentId) {
        // DO NOTHING.
    }

    @Override
    public void logStartFromNotification(Intent intent) {
        // DO NOTHING.
    }

    @Override
    public void logForAppLinkRoute(Intent intent) {
        // DO NOTHING.
    }

    @Override
    public void sendFbActivateApp(Context context) {
        // DO NOTHING.
    }

    @Override
    public void createActivity(Activity activity) {
        // DO NOTHING.
    }

    @Override
    public void updateDefaultUserProperties() {
        // DO NOTHING.
    }

    @Override
    public void setupVersionHistory(Context context) {
        // DO NOTHING.
    }
}
