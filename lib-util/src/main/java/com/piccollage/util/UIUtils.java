package com.piccollage.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.StringRes;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

/**
 * Created by prada on 9/21/15.
 */
public class UIUtils {

    private static Context sContext;

    public static void setApplicationContext(Context context) {
        sContext = context;
    }

    public static int dpToPx(int dp) {
        if (sContext == null) return 0;
        DisplayMetrics displayMetrics = sContext.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int pxToDp(int px) {
        if (sContext == null) return 0;
        DisplayMetrics displayMetrics = sContext.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static Dialog createProgressDialog(Context ctx) {
        ProgressBar pbar = new ProgressBar(ctx);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(200, 200);
        pbar.setLayoutParams(layoutParams);

        LinearLayout layout = new LinearLayout(ctx);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(layoutParams2);
        layout.setGravity(Gravity.CENTER);
        layout.addView(pbar);

        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        return dialog;
    }

    public static ProgressDialog createProgressDialog(Context ctx, @StringRes int wordingStrId) {
        ProgressDialog mProgressDialog = new ProgressDialog(ctx);
        mProgressDialog.setMessage(ctx.getString(wordingStrId));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            mProgressDialog.setProgressNumberFormat(null);
        }
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.setCancelable(true);
        return mProgressDialog;
    }
}
