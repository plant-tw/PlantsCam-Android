package com.piccollage.util;

import android.content.Context;
import android.provider.Settings.Secure;
import java.util.UUID;

public class UuidFactory {

    protected static UUID uuid;

    public UuidFactory(Context context) {

        if (uuid == null) {
            synchronized (UuidFactory.class) {
                if (uuid == null) {
                    try {
                        final String androidId = Secure.getString(
                                context.getContentResolver(), Secure.ANDROID_ID);
                        // Use the Android ID 
                        // http://stackoverflow.com/a/5628136
                        // By the discussion on StackOverflow, ANDROID_ID is 
                        // reliable from Android 2.3 and only 2.2 has problem.
                        // It's okay for us to use ANDROID_ID as UUID here.
                        
                        // In case, if Android ID is broken, use random ID
                        uuid = (!"9774d56d682e549c".equals(androidId)) 
                                ? UUID.nameUUIDFromBytes(androidId.getBytes("utf8"))
                                : UUID.randomUUID(); 
                    } catch (Throwable e) {
                        // generate random id
                        uuid = UUID.randomUUID();
                    }
                }
            }
        }
    }

    public UUID getDeviceUuid() {
        return uuid;
    }
}
