package com.kauale.app_identifer.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.kauale.app_identifer.MainActivity;
import com.kauale.app_identifer.R;
import com.kauale.app_identifer.logs.AppIndentifierLogs;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class CommonUtil {
    @SuppressLint("StaticFieldLeak")
    private static FrameLayout frameLayout;
    private static AdView adView;

    public static boolean isValidString(String s) {
        return (s != null && !s.trim().isEmpty() && !s.equalsIgnoreCase("null"));
    }
    public static boolean isValidEmail(String string) {
        return (isValidString(string) && string.contains("@") && Patterns.EMAIL_ADDRESS.matcher(string).matches());
    }

    public static boolean isValidMobile(String phone) {
        if(!Pattern.matches("[a-zA-Z]+", phone)) {
            return phone.length() > 6 && phone.length() <= 13 && android.util.Patterns.PHONE.matcher(phone).matches();
        }
        return false;
    }

    public static void showToast(Context context,String message){
        if(context == null)return;
        if(!isValidString(message)) message = "Something went wrong";
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }


    @SuppressLint("WrongConstant")
    public static void openDialogBox(String subtitle, final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        @SuppressLint("InflateParams") View view = ((Activity) context).getLayoutInflater().inflate(R.layout.layout_alert, null);
        TextView subTitle = view.findViewById(R.id.alertSubtitleTxt);

         frameLayout = view.findViewById(R.id.banner_container);
         adView = new AdView(context);

        adView.setAdUnitId(context.getString(R.string.banner_ad_unit_id));
        frameLayout.addView(adView);

        loadBanner(context);

        TextView textView = view.findViewById(R.id.alertTitleTxt);
        CardView cardView=view.findViewById(R.id.cv_native);
        TextView yes = view.findViewById(R.id.yes_btn);
        yes.setVisibility(0);
        TextView no = view.findViewById(R.id.ok_btn);
        no.setText("NO");
        subTitle.setText(subtitle);
        builder.setView(view);
        final AlertDialog alert = builder.create();
        no.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        yes.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            public void onClick(View v) {
                ((Activity) context).finishAffinity();
                System.exit(0);
            }
        });
        alert.show();
    }
    public static String getFormattedDate(Date date1, String resultPattern) {
        try {
            if(!isValidString(resultPattern))
                resultPattern = "dd MMM yyyy";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(resultPattern);
            return simpleDateFormat.format(date1);
        } catch (Exception e) {
            AppIndentifierLogs.printStackTrace(e);
        }
        return null;
    }
    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }


    private static void loadBanner(Context context) {
        // Create an ad request. Check your logcat output for the hashed device ID
        // to get test ads on a physical device, e.g.,
        // "Use AdRequest.Builder.addTestDevice("ABCDE0123") to get test ads on this
        // device."
        AdRequest adRequest =
                new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                        .build();

        AdSize adSize = getAdSize(context);
        // Step 4 - Set the adaptive ad size on the ad view.
        adView.setAdSize(adSize);

        // Step 5 - Start loading the ad in the background.
        adView.loadAd(adRequest);
    }

    private static AdSize getAdSize(Context context) {

        Display display = ((MainActivity)context).getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float widthPixels = outMetrics.widthPixels;
        float density = outMetrics.density;

        int adWidth = (int) (widthPixels / density);

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, adWidth);
    }

    public static boolean isInternetConnectivityAvailable(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo isConnectivityAvailable = connectivityManager.getActiveNetworkInfo();
            return isConnectivityAvailable != null;
        }
        return true;
    }

    public static void writePrefBoolean(Context context, String key, boolean value) {
        try {
            if (context == null)
                return;
            SharedPreferences.Editor editor = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE).edit();
            editor.putBoolean(key, value);
            editor.apply();
        } catch (Exception e) {
            AppIndentifierLogs.printStackTrace(e);
        }
    }

    public static boolean readPrefBoolean(Context context, String key) {
        if (context == null)
            return false;
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(key, false);
    }

    public static void writePrefString(Context context, String key, String value) {
        try {
            if (context == null)
                return;
            SharedPreferences.Editor editor = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE).edit();
            editor.putString(key, value);
            editor.apply();
        } catch (Exception e) {
            AppIndentifierLogs.printStackTrace(e);
        }
    }

    public static String readPrefString(Context context, String key) {
        if(context == null)return "";
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getString(key,"");
    }
    public static void writePrefSet(Context context, String key, Set<String> value) {
        try {
            if (context == null)
                return;
            SharedPreferences.Editor editor = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE).edit();
            editor.putStringSet(key, value);
            editor.apply();
        } catch (Exception e) {
            AppIndentifierLogs.printStackTrace(e);
        }
    }

    public static Set<String> readPrefSet(Context context, String key) {
        if(context == null)return null;
        SharedPreferences prefs = context.getSharedPreferences(Constants.PREF_NAME, Context.MODE_PRIVATE);
        return prefs.getStringSet(key,null);
    }

    public static String getTime() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        dateFormat.setTimeZone(tz);
        return (dateFormat.format(new Date()));
    }
    public static void hideSoftKeyboard(View view, Activity activity) {
        try {
            if (view != null) {
                InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                if (activity.getCurrentFocus() != null && inputMethodManager != null)
                    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        } catch (Exception e) {
            AppIndentifierLogs.printStackTrace(e);
        }
    }
    public static void openSoftKeyboard(View view,Activity activity){
        try {
            InputMethodManager inputMethodManager =
                    (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInputFromWindow(
                    view.getApplicationWindowToken(),
                    InputMethodManager.SHOW_FORCED, 0);
        } catch (Exception e) {
            AppIndentifierLogs.printStackTrace(e);
        }
    }


    public static String getFormattedTime(long timeInMillis) {
        return timeInMillis/60000+" min " + (timeInMillis/1000)%60 + " s";
    }
}
