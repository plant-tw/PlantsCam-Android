package com.piccollage.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import com.piccollage.util.config.Consts;
import com.piccollage.util.config.SharePrefUtils;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

public class TwitterVerifier {

    private static final String API_KEY = "pDlpJ3WjkOc5e93fs561A";
    private static final String API_SECRET = "l0MClx5L5TzOYcfYHiSNhsmwRIAcDuuXjfyME66E";

    private final Context mContext;

    public TwitterVerifier(Context context) {
        mContext = context;
    }

    public static Twitter getTwitter() {
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(API_KEY, API_SECRET);
        return twitter;
    }

    public AccessToken getAccessToken() throws Exception {
        SharedPreferences preferences = mContext.getSharedPreferences(SharePrefUtils.NAME_PREFERENCES, Context.MODE_PRIVATE);
        String token = preferences.getString(Consts.KEY_TOKEN, "");
        String secret = preferences.getString(Consts.KEY_TOKEN_SECRET, "");
        return new AccessToken(token, secret);
    }

    public boolean hasAccessToken() {
        SharedPreferences preferences = mContext.getSharedPreferences(SharePrefUtils.NAME_PREFERENCES, Context.MODE_PRIVATE);
        String token = preferences.getString(Consts.KEY_TOKEN, "");
        String secret = preferences.getString(Consts.KEY_TOKEN_SECRET, "");
        return !(TextUtils.isEmpty(token) || TextUtils.isEmpty(secret));
    }

    public void removeAccessToken() {
        SharedPreferences preferences = mContext.getSharedPreferences(SharePrefUtils.NAME_PREFERENCES, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.remove(Consts.KEY_TOKEN);
        editor.remove(Consts.KEY_TOKEN_SECRET);
        editor.remove(Consts.KEY_TWITTER_USER_NAME);
        editor.apply();
    }
}
