package com.kauale.app_identifer;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;

public class PrivacyPolicyActivity extends AppCompatActivity {

    private WebView webView;
    private CoordinatorLayout coordinatorLayout;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        coordinatorLayout = findViewById(R.id.cd1);

        webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("file:///android_asset/privacy_policy.html");
        getSupportActionBar().setTitle("Privacy Policy");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, "PRESS GO BACK", Snackbar.LENGTH_LONG);
        snackbar.setAction("Go Back", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrivacyPolicyActivity.super.onBackPressed();
            }
        });
        snackbar.setActionTextColor(Color.WHITE);
        View view = snackbar.getView();
        TextView tv = view.findViewById(com.google.android.material.R.id.snackbar_text);
        tv.setTextColor(Color.LTGRAY);
        snackbar.show();
        snackbar.show();
    }
}
