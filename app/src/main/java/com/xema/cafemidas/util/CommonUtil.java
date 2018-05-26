package com.xema.cafemidas.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.xema.cafemidas.R;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.content.ContentValues.TAG;

/**
 * Created by xema0 on 2018-05-26.
 */

public class CommonUtil {
    public static String toDecimalFormat(int num) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(num);
    }

    public static String toDecimalFormat(long num) {
        DecimalFormat df = new DecimalFormat("#,###");
        return df.format(num);
    }

    public static String getModifiedDate(Date modified) {
        return getModifiedDate(Locale.getDefault(), modified);
    }

    private static String getModifiedDate(Locale locale, Date modified) {
        SimpleDateFormat dateFormat = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            dateFormat = new SimpleDateFormat(getDateFormat(locale), locale);
        } else {
            dateFormat = new SimpleDateFormat("MMM/dd/yyyy", locale);
        }

        return dateFormat.format(modified);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private static String getDateFormat(Locale locale) {
        //return DateFormat.getBestDateTimePattern(locale, "MM/dd/yyyy hh:mm:ss aa");
        return DateFormat.getBestDateTimePattern(locale, "MM/dd/yyyy");
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        if (imm != null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String formatDate(String date, Context context) {
        String result;
        try {
            String[] dateArr = date.split("T");
            String[] yearMonthDay = dateArr[0].split("-");
            String[] hourMinute = dateArr[1].split(":");
            String year = yearMonthDay[0];
            String month = yearMonthDay[1];
            String day = yearMonthDay[2];
            String hour = hourMinute[0];
            String minute = hourMinute[1];
            String amOrPm;

            DateTime serverTime = new DateTime(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day), Integer.parseInt(hour), Integer.parseInt(minute), DateTimeZone.forID("Asia/Seoul"));//korea
            DateTime localTime = serverTime.withZone(DateTimeZone.forID("Asia/Seoul"));
            int numYear = localTime.getYear();
            int numMonth = localTime.getMonthOfYear();
            int numDay = localTime.getDayOfMonth();
            int numHour = localTime.getHourOfDay();
            int numMinute = localTime.getMinuteOfHour();

            result = numMonth + "월 " + numDay + "일 " + numHour + "시 " + numMinute + "분 ";

            //if (numHour < 12) {
            //    //오전
            //    if (numHour == 0) numHour = 12; //자정
            //    amOrPm = context.getString(R.string.common_am);
            //} else {
            //    //오후
            //    if (numHour != 12) numHour = numHour - 12; //
            //    amOrPm = context.getString(R.string.common_pm);
            //}
            //result = context.getString(R.string.format_date, "" + numYear, "" + numMonth, "" + numDay, amOrPm, numHour < 10 ? "0" + numHour : "" + numHour, numMinute < 10 ? "0" + numMinute : "" + numMinute);

            //DateTime deviceTime = DateTime.now();
            //if (numYear < deviceTime.getYear()) {
            //    result = context.getString(R.string.format_date_year_day, numYear, numMonth, numDay);
            //} else if (numMonth < deviceTime.getMonthOfYear()) {
            //    result = context.getString(R.string.format_date_year_day, numYear, numMonth, numDay);
            //} else if (numDay < deviceTime.getDayOfMonth()) {
            //    result = context.getString(R.string.format_date_day, deviceTime.getDayOfMonth() - numDay);
            //} else if (numHour < deviceTime.getHourOfDay()) {
            //    result = context.getString(R.string.format_date_hour, deviceTime.getHourOfDay() - numHour);
            //} else if (numMinute < deviceTime.getMinuteOfHour()) {
            //    result = context.getString(R.string.format_date_minute, deviceTime.getMinuteOfHour() - numMinute);
            //} else {
            //    //result = context.getString(R.string.format_date_second, deviceTime.getSecondOfMinute() - localTime.getSecondOfMinute());
            //    result = context.getString(R.string.format_date_just);
            //}

        } catch (Exception e) {
            result = date;
        }
        return result;
    }

    public static PackageInfo getPackageInfo(final Context context, int flag) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), flag);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w(TAG, "Unable to get PackageInfo", e);
        }
        return null;
    }

    public static String getKeyHash(final Context context) {
        PackageInfo packageInfo = getPackageInfo(context, PackageManager.GET_SIGNATURES);
        if (packageInfo == null)
            return null;

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
            } catch (NoSuchAlgorithmException e) {
                Log.w(TAG, "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
        return null;
    }

    public static void checkKeyHash(Context context) {
        Log.d("hash", getCertificateSHA1Fingerprint(context));

        PackageInfo packageInfo = null;
        try {
            packageInfo = context.getPackageManager().getPackageInfo("com.idcorp.buyble", PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo != null) {
                for (Signature signature : packageInfo.signatures) {
                    try {
                        MessageDigest md = MessageDigest.getInstance("SHA");
                        md.update(signature.toByteArray());
                        Log.d("hash2",Base64.encodeToString(md.digest(), Base64.NO_WRAP));
                    } catch (NoSuchAlgorithmException e) {
                        Log.w("HASH2", "Unable to get MessageDigest. signature=" + signature, e);
                    }
                }
            }

    }


    public static String getCertificateSHA1Fingerprint(Context context) {
        PackageManager pm = context.getPackageManager();
        String packageName = context.getPackageName();
        int flags = PackageManager.GET_SIGNATURES;
        PackageInfo packageInfo = null;
        try {
            packageInfo = pm.getPackageInfo(packageName, flags);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Signature[] signatures = packageInfo.signatures;
        byte[] cert = signatures[0].toByteArray();
        InputStream input = new ByteArrayInputStream(cert);
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X509");
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        X509Certificate c = null;
        try {
            c = (X509Certificate) cf.generateCertificate(input);
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        String hexString = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            byte[] publicKey = md.digest(c.getEncoded());
            hexString = byte2HexFormatted(publicKey);
        } catch (NoSuchAlgorithmException e1) {
            e1.printStackTrace();
        } catch (CertificateEncodingException e) {
            e.printStackTrace();
        }
        return hexString;
    }

    public static String byte2HexFormatted(byte[] arr) {
        StringBuilder str = new StringBuilder(arr.length * 2);
        for (int i = 0; i < arr.length; i++) {
            String h = Integer.toHexString(arr[i]);
            int l = h.length();
            if (l == 1) h = "0" + h;
            if (l > 2) h = h.substring(l - 2, l);
            str.append(h.toUpperCase());
            if (i < (arr.length - 1)) str.append(':');
        }
        return str.toString();
    }
}
