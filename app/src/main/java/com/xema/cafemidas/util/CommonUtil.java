package com.xema.cafemidas.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.xema.cafemidas.R;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

            DateTime deviceTime = DateTime.now();
            if (numYear < deviceTime.getYear()) {
                result = context.getString(R.string.format_date_year_day, numYear, numMonth, numDay);
            } else if (numMonth < deviceTime.getMonthOfYear()) {
                result = context.getString(R.string.format_date_year_day, numYear, numMonth, numDay);
            } else if (numDay < deviceTime.getDayOfMonth()) {
                result = context.getString(R.string.format_date_day, deviceTime.getDayOfMonth() - numDay);
            } else if (numHour < deviceTime.getHourOfDay()) {
                result = context.getString(R.string.format_date_hour, deviceTime.getHourOfDay() - numHour);
            } else if (numMinute < deviceTime.getMinuteOfHour()) {
                result = context.getString(R.string.format_date_minute, deviceTime.getMinuteOfHour() - numMinute);
            } else {
                //result = context.getString(R.string.format_date_second, deviceTime.getSecondOfMinute() - localTime.getSecondOfMinute());
                result = context.getString(R.string.format_date_just);
            }

        } catch (Exception e) {
            result = date;
        }
        return result;
    }
}
