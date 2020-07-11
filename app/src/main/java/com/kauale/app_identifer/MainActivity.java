package com.kauale.app_identifer;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.kauale.app_identifer.application.MyApplication;
import com.kauale.app_identifer.database.APICallBack;
import com.kauale.app_identifer.home.AppCountryModel;
import com.kauale.app_identifer.home.AppDetailsModel;
import com.kauale.app_identifer.home.HomeFragment;
import com.kauale.app_identifer.logs.AppIndentifierLogs;
import com.kauale.app_identifer.roomdb.CallBackInterface;
import com.kauale.app_identifer.roomdb.DBAppIdentifier;
import com.kauale.app_identifer.roomdb.PackageInfoDao;
import com.kauale.app_identifer.utils.CommonUtil;
import com.kauale.app_identifer.utils.Constants;
import com.kauale.app_identifer.utils.SuperActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Fragments.BottomSheetNavigationFragment;

public class MainActivity extends SuperActivity implements NavigationView.OnNavigationItemSelectedListener,CallBackInterface {

    public  List<AppDetailsModel> appDetailsModelList = new ArrayList<>();
    private  DatabaseReference myDatabase;
    List<AppCountryModel>  appCountryModels = new ArrayList<>();
    public static PackageInfoDao packageInfoDao;
    HomeFragment homeFragment;
    private SpringAnimation xAnimation;
    private SpringAnimation yAnimation;
    private float dX;
    private float dY;
    EditText editText;
    List<AppDetailsModel> filteredAppDetailsModelList = new ArrayList<>();
    private static List<AppDetailsModel> allAppDetailsList = new ArrayList<>();
    boolean iskeyboarOpen = false;
    DrawerLayout drawerLayout;
    TextView appTotalCount;
    ImageView hamburgerMenu,searchIcon,filterIcon,privacyPolicyBackArrow;
    NavigationView navigationView;
    LinearLayout mainLayout,privacyPolicyLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpToolBar();
        getInstalledApplication();
        homeFragment = new HomeFragment();
        gotoFragment(homeFragment, null, null, this);
        setupFirebaseDatabase();
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this::onNavigationItemSelected);

        editText.setOnClickListener(v -> {
            editText.requestFocus();
            editText.setCursorVisible(true);
        });



        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String searchTxt = s.toString().toLowerCase();
                filteredAppDetailsModelList.clear();
                if(allAppDetailsList != null && CommonUtil.isValidString(searchTxt)){
                    for (AppDetailsModel appDetailsModel: allAppDetailsList){
                        if(appDetailsModel.getAppName().toLowerCase().startsWith(searchTxt) || (CommonUtil.isValidString(appDetailsModel.getCountry())  && appDetailsModel.getCountry().toLowerCase().startsWith(searchTxt))){
                            filteredAppDetailsModelList.add(appDetailsModel);
                        }
                    }
                    appTotalCount.setText(filteredAppDetailsModelList.size() + " Apps");
                    homeFragment.updateAdapter(filteredAppDetailsModelList);
                }else {
                    homeFragment.updateAdapter(allAppDetailsList);
                    appTotalCount.setText(allAppDetailsList.size() + " Apps");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setUpToolBar() {
        hamburgerMenu = findViewById(R.id.hamburger_menu);
        searchIcon = findViewById(R.id.search_button);
        filterIcon = findViewById(R.id.filter_button);
        editText = findViewById(R.id.app_name_edt_txt);
        appTotalCount = findViewById(R.id.total_app_count);
        drawerLayout = findViewById(R.id.drawer_layout);
        mainLayout = findViewById(R.id.main_layout);
        privacyPolicyLayout = findViewById(R.id.privacy_policy_layout);
        privacyPolicyBackArrow = findViewById(R.id.privacy_policy_left_arrow);
        privacyPolicyBackArrow.setOnClickListener(v -> {
            mainLayout.setVisibility(View.VISIBLE);
            privacyPolicyLayout.setVisibility(View.GONE);
        });

        filterIcon.setOnClickListener(v -> {
            BottomSheetDialogFragment bottomSheetDialogFragment = new BottomSheetNavigationFragment(this);
            bottomSheetDialogFragment.show(getSupportFragmentManager(), "Bottom Sheet Dialog Fragment");
        });
        hamburgerMenu.setOnClickListener(v -> {
            if(iskeyboarOpen){
                iskeyboarOpen = false;
                hamburgerMenu.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_menu));
                editText.setHint("");
                editText.setText(getString(R.string.app_name));
                editText.setCursorVisible(false);
                editText.setEnabled(false);
                if(allAppDetailsList.size() != 0){
                    appTotalCount.setText(allAppDetailsList.size() + " Apps");
                }else appTotalCount.setText(appDetailsModelList.size() + " Apps");
                homeFragment.updateAdapter(allAppDetailsList);
                CommonUtil.hideSoftKeyboard(editText.getRootView(),MainActivity.this);
                searchIcon.setVisibility(View.VISIBLE);
                filterIcon.setVisibility(View.VISIBLE);
            }else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        searchIcon.setOnClickListener(v -> {
            editText.setSelection(0);
            editText.requestFocus();
            editText.setCursorVisible(true);
            editText.setEnabled(true);
            editText.setText("");
            editText.setHint(getString(R.string.search));
            hamburgerMenu.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_arrow_left));
            iskeyboarOpen = true;
            searchIcon.setVisibility(View.GONE);
            filterIcon.setVisibility(View.GONE);
            CommonUtil.openSoftKeyboard(editText.getRootView(),MainActivity.this);
        });

    }




    private void share() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "App Launcher");
            String shareMessage = "I used this app to support Indian army and uninstalled ch***se Apps installed. Share this to support indian army and boycott ch***se Apps. \n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, this.getString(R.string.share_with)));

        } catch (Exception e) {
            //e.toString();
        }
    }



    public SpringAnimation createSpringAnimation(View view,
                                                 DynamicAnimation.ViewProperty property,
                                                 float finalPosition,
                                                 float stiffness,
                                                 float dampingRatio) {
        SpringAnimation animation = new SpringAnimation(view, property);
        SpringForce springForce = new SpringForce(finalPosition);
        springForce.setStiffness(stiffness);
        springForce.setDampingRatio(dampingRatio);
        animation.setSpring(springForce);
        return animation;
    }

    public SpringAnimation createSpringAnimation(View view,
                                                 DynamicAnimation.ViewProperty property,
                                                 float stiffness,
                                                 float dampingRatio) {
        SpringAnimation animation = new SpringAnimation(view, property);
        SpringForce springForce = new SpringForce();
        springForce.setStiffness(stiffness);
        springForce.setDampingRatio(dampingRatio);
        animation.setSpring(springForce);
        return animation;
    }




    private void setupFirebaseDatabase() {
        // Write a message to the database
        FirebaseDatabase database = MyApplication.getFirebaseDatabase();
        myDatabase = database.getReference("fetchAppData");
        DBAppIdentifier dbAppIdentifier = DBAppIdentifier.getInstance(this);
        packageInfoDao = dbAppIdentifier.packageInfoDao();

        Log.e("Total Installed Apps", appDetailsModelList.size() + "");
        if(appDetailsModelList != null && appDetailsModelList.size() > 0){
            updateTableInFirebaseDatabase(appDetailsModelList,appDetailsModelList.get(0),null);
        }

    }

    public void updateTableInFirebaseDatabase(final List<AppDetailsModel> appDetailsModelList, final AppDetailsModel appDetailsModel, AppCountryModel appCountryModel){

        myDatabase.child(appDetailsModel.getPackageName().replace(".","_")).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                if(isPackageCountryNA(appDetailsModel))
                    mutableData.setValue(new AppCountryModel(appDetailsModel.getAppName(), "NA",appDetailsModel.getPackageName() ));
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                if(b){
                    try{
                        if(dataSnapshot != null){
                            HashMap<String, String> packageDetailsFromServer = new HashMap<>((Map<? extends String, ? extends String>) dataSnapshot.getValue());
                            if(packageInfoDao.getPackageInfo(appDetailsModel.getPackageName()) != null)
                                packageInfoDao.insert(new AppCountryModel(packageDetailsFromServer.get("appName"),packageDetailsFromServer.get("countryName"),packageDetailsFromServer.get("packageName")));
                        }
                    }catch (Exception e)  {
                        AppIndentifierLogs.printStackTrace(e);
                    }

                   appDetailsModelList.remove(appDetailsModel);
                    if(appDetailsModelList.size() > 0)
                        updateTableInFirebaseDatabase(appDetailsModelList,appDetailsModelList.get(0),null);
                    else fetchUpdatedDataFromDB();
                }
            }
        });
    }

    public void fetchUpdatedDataFromDB() {
        new APICallBack().getData(this,this);
    }

    public boolean isPackageCountryNA(AppDetailsModel appDetailsModel) {
        AppCountryModel  appCountryModel = packageInfoDao.getPackageInfo(appDetailsModel.getPackageName());
        if(appCountryModel == null || appCountryModel.getCountryName() == null) return true;
        return appCountryModel.getCountryName().equalsIgnoreCase("NA");
    }

    public void getInstalledApplication() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        appDetailsModelList.clear();
        for (ApplicationInfo packageInfo : packages) {

            if ((packageInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                addToInstalledApps(packageInfo, pm);
            } else if ((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {

            } else {
                addToInstalledApps(packageInfo, pm);
            }
        }
        allAppDetailsList.addAll(getAppDetailsModelList());
        if(allAppDetailsList.size() != 0){
            appTotalCount.setText(allAppDetailsList.size() + " Apps");
        }else appTotalCount.setText(appDetailsModelList.size() + " Apps");
    }

    public void addToInstalledApps(ApplicationInfo packageInfo, PackageManager pm) {
        int stringId = packageInfo.labelRes;
        String appName = null;
        Drawable appIcon = null;
        try {
            appName = (String) pm.getApplicationLabel(packageInfo);
            appIcon = pm.getApplicationIcon(packageInfo);
        } catch (Exception e) {
            try {
                appName = stringId == 0 ? (packageInfo.nonLocalizedLabel != null ? packageInfo.nonLocalizedLabel.toString() : null) : getString(stringId);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            AppIndentifierLogs.printStackTrace(e);
        }
        if(pm.getLaunchIntentForPackage(packageInfo.packageName) != null && CommonUtil.isValidString(appName)){
            AppDetailsModel appDetailsModel = new AppDetailsModel(packageInfo.packageName, packageInfo.sourceDir, pm.getLaunchIntentForPackage(packageInfo.packageName), appName, appIcon);
            appDetailsModelList.add(appDetailsModel);
        }

    }

    public List<AppDetailsModel> getAppDetailsModelList() {
        if(packageInfoDao != null){
            for (int i = 0; i < appDetailsModelList.size(); i++) {
                if(!isPackageCountryNA(appDetailsModelList.get(i)) && packageInfoDao.getPackageInfo(appDetailsModelList.get(i).getPackageName()) != null ){
                    appDetailsModelList.get(i).setCountry(packageInfoDao.getPackageInfo(appDetailsModelList.get(i).getPackageName()).getCountryName());
                }
            }
        }
        if(homeFragment != null){
            homeFragment.updateAdapter(appDetailsModelList);
        }
        return appDetailsModelList;
    }

    @Override
    public void onBackPressed() {
        if(mainLayout.getVisibility() == View.GONE){
            mainLayout.setVisibility(View.VISIBLE);
            privacyPolicyLayout.setVisibility(View.GONE);
            homeFragment.updateAdapter(appDetailsModelList);
        }else  if(iskeyboarOpen){
            iskeyboarOpen = false;
            hamburgerMenu.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_menu));
            editText.setHint("");
            editText.setText(getString(R.string.app_name));
            editText.setCursorVisible(false);
            editText.setEnabled(false);
            if(allAppDetailsList.size() != 0){
                appTotalCount.setText(allAppDetailsList.size() + " Apps");
            }else appTotalCount.setText(appDetailsModelList.size() + " Apps");
            homeFragment.updateAdapter(appDetailsModelList);
            CommonUtil.hideSoftKeyboard(editText.getRootView(),MainActivity.this);
            searchIcon.setVisibility(View.VISIBLE);
            filterIcon.setVisibility(View.VISIBLE);
        }else  CommonUtil.openDialogBox("Do you really want to exit ?",this);
    }

    public void removedApp(AppDetailsModel appDetailsModelDel) {
        allAppDetailsList.remove(appDetailsModelDel);
        appTotalCount.setText(allAppDetailsList.size() + "Apps");
    }

    @Override
    public void onSuccessResponse(Object o, String key) {
        try{
            if(key.equalsIgnoreCase(Constants.MAIN)){
                List<AppCountryModel> appCountryModels = (List<AppCountryModel>)o;
                for(AppCountryModel appCountryModel: appCountryModels){
                    for (AppDetailsModel appDetailsModel: allAppDetailsList){
                        if(appCountryModel.getPackageName().equalsIgnoreCase(appDetailsModel.getPackageName())){
                            if(appCountryModel.getCountryName() != null && !appCountryModel.getCountryName().equalsIgnoreCase(appDetailsModel.getCountry())){
                                appDetailsModel.setCountry(appCountryModel.getCountryName());
                            }
                        }
                    }
                }
                homeFragment.updateAdapter(allAppDetailsList);
            }
        }catch (Exception e){
            AppIndentifierLogs.printStackTrace(e);
        }
    }

    @Override
    public void onErrorResponse(Object o, String key) {

    }

    public void sortByAppName() {
        homeFragment.sortByAppName();
    }
    public void sortByCountryName(){
        homeFragment.sortByCountryName();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.prvacy:
                WebView webView = findViewById(R.id.webView);
                webView.getSettings().setJavaScriptEnabled(true);
                mainLayout.setVisibility(View.GONE);
                privacyPolicyLayout.setVisibility(View.VISIBLE);
                ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Loading...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                webView.setWebViewClient(new WebViewClient() {

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                        CommonUtil.showToast(getApplicationContext(), "Error:" + description);

                    }
                });
                webView.loadUrl("file:///android_asset/privacy_policy.html");
                break;
            case R.id.rateus:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)));
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
                }
               break;
            case R.id.exit:
                CommonUtil.openDialogBox("Do you really want to exit ?", this);
               break;
            case R.id.share:
                share();
                break;
            default:
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }
}