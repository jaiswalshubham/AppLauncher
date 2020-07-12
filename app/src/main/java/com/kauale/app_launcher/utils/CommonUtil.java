package com.kauale.app_launcher.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.kauale.app_launcher.R;
import com.kauale.app_launcher.logs.AppIndentifierLogs;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

public class CommonUtil {
    @SuppressLint("StaticFieldLeak")
    private static FrameLayout frameLayout;

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



        TextView textView = view.findViewById(R.id.alertTitleTxt);
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
