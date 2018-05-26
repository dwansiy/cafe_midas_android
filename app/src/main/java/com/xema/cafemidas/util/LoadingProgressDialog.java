package com.xema.cafemidas.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.Window;
import android.widget.ProgressBar;

import com.xema.cafemidas.R;

public class LoadingProgressDialog extends Dialog {
    private static Dialog mDialog;

    private LoadingProgressDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_loading);

        ProgressBar pbLoading = findViewById(R.id.pb_loading);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Drawable drawableProgress = DrawableCompat.wrap(pbLoading.getIndeterminateDrawable());
            DrawableCompat.setTint(drawableProgress, ContextCompat.getColor(getContext(), R.color.colorWhite));
            pbLoading.setIndeterminateDrawable(DrawableCompat.unwrap(drawableProgress));
        } else {
            pbLoading.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.colorWhite), PorterDuff.Mode.SRC_IN);
        }
    }

    public static void showProgress(Context context) {
        if (mDialog == null) {
            mDialog = new LoadingProgressDialog(context);
        }
        if (mDialog.getWindow() != null)
            mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setCancelable(false);
        mDialog.show();
    }

    public static void hideProgress() {
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
    }
}
