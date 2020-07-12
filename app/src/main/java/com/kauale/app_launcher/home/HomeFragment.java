package com.kauale.app_launcher.home;

import android.Manifest;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.kauale.app_launcher.MainActivity;
import com.kauale.app_launcher.R;
import com.kauale.app_launcher.database.APICallBack;
import com.kauale.app_launcher.logs.AppIndentifierLogs;
import com.kauale.app_launcher.roomdb.CallBackInterface;
import com.kauale.app_launcher.utils.CommonUtil;
import com.kauale.app_launcher.utils.Constants;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import Adapters.AppDetailsAdapter;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment implements AppDetailsAdapter.OnItemClicked , CallBackInterface {

    private View view;
    private RecyclerView recyclerView;
    private static List<AppDetailsModel> mList = new ArrayList<>();
    private Button refresh;
    private AppDetailsAdapter adapter;
    public static final int NUMBER_OF_ADS = 5;
    private static boolean isUninstalledClick = false;
    private static AppDetailsModel appDetailsModelDel;
    private static int postionDel;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if(view == null){
            view = inflater.inflate(R.layout.fragment_home, container, false);
            CommonUtil.hideSoftKeyboard(view,getActivity());

            swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
            swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);


            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new APICallBack().getData(getActivity(),HomeFragment.this);
                    swipeRefreshLayout.setRefreshing(false);
                }
            });

            recyclerView = view.findViewById(R.id.apk_list);
            if (mList.size() == 0) {
                ((MainActivity) getActivity()).getAppDetailsModelList();
            }
            adapter = new AppDetailsAdapter(getContext(), mList, this);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
        }

        return view;
    }


    public void updateAdapter(List<AppDetailsModel> appDetailsModels) {
        ArrayList<AppDetailsModel> appDetailsModelList = new ArrayList<>(appDetailsModels);
        mList.clear();
        mList.addAll(appDetailsModelList);
        if (adapter == null)
            adapter = new AppDetailsAdapter(getContext(), mList, this);
        else adapter.updateAppList(mList);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDeleteClicked(AppDetailsModel appDetailsModel, int position) {
//        openDialogueBox(appDetailsModel);
        deleteApplication(appDetailsModel.getPackageName());
        isUninstalledClick = true;
        postionDel = position;
        appDetailsModelDel = appDetailsModel;
    }

    @Override
    public void openAppDetailsDialogue(AppDetailsModel appDetailsModel) {
        if(checkPermission(appDetailsModel.getPackageName())){
            try {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View view =  getActivity().getLayoutInflater().inflate(R.layout.dialogue_app_details, null);
                TextView appName,packageID,firstInstalled,lastUpdated,lastTimeUsed,dailyUsageTime;
                appName = view.findViewById(R.id.app_name);
                packageID = view.findViewById(R.id.package_id);
                firstInstalled = view.findViewById(R.id.first_installed);
                lastUpdated = view.findViewById(R.id.last_updated_time);
                lastTimeUsed = view.findViewById(R.id.last_time_used);
                dailyUsageTime = view.findViewById(R.id.approx_daily_usage);
                ImageView closeBtn = view.findViewById(R.id.close_btn);
                RelativeLayout lastTimeUsedLayout,dailyUsageTimeLayout;
                lastTimeUsedLayout = view.findViewById(R.id.last_time_used_layout);
                dailyUsageTimeLayout= view.findViewById(R.id.approx_daily_usage_layout);
                builder.setView(view);
                AlertDialog alert = builder.create();
                closeBtn.setOnClickListener(v -> alert.dismiss());
                alert.show();
                appName.setText(appDetailsModel.getAppName());
                packageID.setText(appDetailsModel.getPackageName());

                PackageInfo packageInfo = ((MainActivity)getActivity()).getPackageManager().getPackageInfo(appDetailsModel.packageName, PackageManager.GET_PERMISSIONS);
                Date installTime = new Date(packageInfo.firstInstallTime );
                firstInstalled.setText(CommonUtil.getFormattedDate(installTime,null));
                Log.d("TAG", "Installed: " + installTime.toString());
                Date updateTime = new Date( packageInfo.lastUpdateTime );
                lastUpdated.setText(CommonUtil.getFormattedDate(updateTime,null));
                Log.d("TAG", "Updated: " + updateTime.toString());
                UsageStatsManager usm= null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    usm = (UsageStatsManager)getActivity().getSystemService(Context.USAGE_STATS_SERVICE);
                }
                Calendar calendar= Calendar.getInstance();
                long toTime=calendar.getTimeInMillis();
                calendar.add(Calendar.DAY_OF_MONTH,-1);
                long fromTime=calendar.getTimeInMillis();
                final List<UsageStats> queryUsageStats;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    queryUsageStats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,fromTime,toTime);
                    boolean granted = queryUsageStats!=null&&queryUsageStats!=Collections.EMPTY_LIST;
                    if(!granted){
                        lastTimeUsedLayout.setVisibility(View.GONE);
                        dailyUsageTimeLayout.setVisibility(View.GONE);
                        new AlertDialog.Builder(getActivity()).setTitle("Permission Required!").setMessage("To get more details of the "+appDetailsModel.getAppName() +" app, we need additional permission").setNegativeButton(getString(R.string.ignore), (dialog, which) -> dialog.dismiss()).setPositiveButton(getString(R.string.grant_permission), (dialog, which) -> {
                            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                            // intent.setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$SecuritySettingsActivity"));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivityForResult(intent,0);
                        }).show();
                    }else {
                        long approxDailyUsage = 0;
                        List<UsageStats> appStats = new ArrayList<>();
                        for(UsageStats usageStats: queryUsageStats){
                            if(usageStats.getPackageName().equalsIgnoreCase(appDetailsModel.getPackageName())){
                                appStats.add(usageStats);
                                approxDailyUsage += usageStats.getTotalTimeInForeground();
                            }
                        }
                        if(appStats.size() > 0){
                            lastTimeUsed.setText(CommonUtil.getDate(appStats.get(appStats.size() -1).getLastTimeStamp(),"dd MMM yyyy hh:mm a"));
//                            lastTimeUsed.setText(DateUtils.formatSameDayTime(appStats.get(appStats.size() -1).getLastTimeUsed(), System.currentTimeMillis(), DateFormat.LONG, DateFormat.LONG));
                        } else lastTimeUsedLayout.setVisibility(View.GONE);
                        dailyUsageTime.setText(CommonUtil.getFormattedTime(approxDailyUsage));
                    }
                }

            } catch ( PackageManager.NameNotFoundException e ) {
                e.printStackTrace();
            }
        }else {
            requestforPermissions();
        }

    }
    private boolean checkPermission(String packageName) {
        try {
            boolean granted = false;
            PackageManager packageManager = ((MainActivity)getActivity()).getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            AppOpsManager appOps = (AppOpsManager) getActivity().getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    android.os.Process.myUid(),packageName);

            if (mode == AppOpsManager.MODE_DEFAULT) {
                granted = (getActivity().checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
            } else {
             return  granted = (mode == AppOpsManager.MODE_ALLOWED);
            }
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        try {
            return (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.GET_PACKAGE_SIZE) == PackageManager.PERMISSION_GRANTED);
        } catch (Exception e) {
            AppIndentifierLogs.printStackTrace(e);
        }
        return false;
    }



    private void requestPermission() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }
        } catch (Exception e) {
            AppIndentifierLogs.printStackTrace(e);
        }
    }


    private void requestforPermissions() {

    }

    private boolean isPermissionGranted() {
        return false;
    }

    public void deleteApplication(String pkgName) {
        Uri packageURI = Uri.parse("package:" + pkgName);
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
        startActivityForResult(uninstallIntent,RESULT_OK);
        mList.remove(appDetailsModelDel);
        ((MainActivity)getActivity()).removedApp(appDetailsModelDel);
        adapter.updateAppList(mList);
    }


    private void openDialogueBox(AppDetailsModel appDetailsModel) {
        new AlertDialog.Builder(getActivity()).setTitle("").setPositiveButton("YES", (dialog, which) -> {
            deleteApplication(appDetailsModel.getPackageName());
            dialog.dismiss();
        }).setNegativeButton("NO", (dialog, which) -> dialog.dismiss()).setMessage("Are you sure you want to remove " + appDetailsModel.getAppName()).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isUninstalledClick) {
            if (adapter != null) {
                mList.remove(appDetailsModelDel);
                ((MainActivity)getActivity()).removedApp(appDetailsModelDel);
                adapter.updateAppList(mList);
            }
            isUninstalledClick = false;
        }
    }

    public List<AppDetailsModel> getappDetailsModelList() {
        return mList;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RESULT_OK){

        }
    }

    @Override
    public void onSuccessResponse(Object o, String key) {
        try{
            if(key.equalsIgnoreCase(Constants.FRAG)){
                List<AppCountryModel> appCountryModels = (List<AppCountryModel>)o;
                for(AppCountryModel appCountryModel: appCountryModels){
                    for (AppDetailsModel appDetailsModel: mList){
                        if(appCountryModel.getPackageName().equalsIgnoreCase(appDetailsModel.getPackageName())){
                            if(appCountryModel.getCountryName() != null && !appCountryModel.getCountryName().equalsIgnoreCase(appDetailsModel.getCountry())){
                                appDetailsModel.setCountry(appCountryModel.getCountryName());
                            }
                        }
                    }
                }
                updateAdapter(mList);
            }
        }catch (Exception e){
            AppIndentifierLogs.printStackTrace(e);
        }
    }

    @Override
    public void onErrorResponse(Object o, String key) {

    }

    public void sortByAppName() {
        adapter.sortByAppName();
    }
    public void sortByCountryName(){
        adapter.sortByCountryName();
    }

}