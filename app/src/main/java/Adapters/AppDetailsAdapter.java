package Adapters;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kauale.app_launcher.home.AppDetailsModel;
import com.kauale.app_launcher.R;
import com.kauale.app_launcher.logs.AppIndentifierLogs;
import com.kauale.app_launcher.utils.CommonUtil;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public class AppDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private Context context;
    private List<AppDetailsModel> mList;
    private OnItemClicked listener;
    PackageManager pm ;

    public AppDetailsAdapter(Context context, List<AppDetailsModel> mList, OnItemClicked listener) {
        this.context = context;
        this.mList = mList;
        this.listener = listener;
        pm = context.getPackageManager();
        notifyDataSetChanged();
    }


    public void updateAppList(List<AppDetailsModel> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View menuItemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.app_item, parent, false);
        return new AppDetailsHolder(menuItemLayoutView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AppDetailsHolder appDetailsHolder = (AppDetailsHolder) holder;
        AppDetailsModel appDetailsModel = (AppDetailsModel) mList.get(position);
        appDetailsHolder.appName.setText(appDetailsModel.getAppName());
        appDetailsHolder.appLogo.setImageDrawable(appDetailsModel.getAppIcon());
        appDetailsHolder.appCountryName.setText(CommonUtil.isValidString(appDetailsModel.getCountry()) && !appDetailsModel.getCountry().equalsIgnoreCase("NA")  ? appDetailsModel.getCountry():"Country NA");
        appDetailsHolder.open.setOnClickListener(v -> openApp(appDetailsModel.getPackageName()));
        appDetailsHolder.deleteIcon.setOnClickListener(v -> {
            appDetailsHolder.listener.onDeleteClicked(appDetailsModel, position);
        });
        appDetailsHolder.deleteIcon.setVisibility(isPrivilegedApp(appDetailsModel.getPackageName()) ? View.GONE:View.VISIBLE);
        appDetailsHolder.cvAPK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appDetailsHolder.listener.openAppDetailsDialogue(mList.get(position));
            }
        });
    }


    @Override
    public int getItemCount() {
        if (mList != null)
            return mList.size();
        else return 0;
    }

    public void sortByAppName() {
        Collections.sort(mList, (a, b) -> a.getAppName().compareTo(b.getAppName()));
        notifyDataSetChanged();
    }

    public void sortByCountryName() {
        Collections.sort(mList, (a, b) -> a.getCountry().compareTo(b.getCountry()));
        notifyDataSetChanged();
    }

    public static class AppDetailsHolder extends RecyclerView.ViewHolder{
        ImageView appLogo;
        TextView appName, appCountryName;
        ImageView deleteIcon;
        ImageView open;
        OnItemClicked listener;
        CardView cvAPK;

        public AppDetailsHolder(@NonNull View itemView, OnItemClicked listener) {
            super(itemView);

            appLogo = itemView.findViewById(R.id.apk_logo);
            appName = itemView.findViewById(R.id.app_name);
            appCountryName = itemView.findViewById(R.id.country);
            deleteIcon = itemView.findViewById(R.id.delete_app);
            open = itemView.findViewById(R.id.open);
            cvAPK = itemView.findViewById(R.id.cv_apk);
            this.listener = listener;
        }
    }

    private void openApp(String packageName) {
        if (isAppInstalled(packageName) && pm.getLaunchIntentForPackage(packageName) != null)
            context.startActivity(context.getPackageManager().getLaunchIntentForPackage(packageName));
        else Toast.makeText(context, "App not installed", Toast.LENGTH_SHORT).show();
    }

    private boolean isAppInstalled(String packageName) {
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (Exception e) {
            AppIndentifierLogs.printStackTrace(e);
        }
        return false;
    }



    public interface OnItemClicked {
        void onDeleteClicked(AppDetailsModel appDetailsModel, int position);
        void openAppDetailsDialogue(AppDetailsModel appDetailsModel);
    }
    public  boolean isPrivilegedApp(String pkg) {
        try {
            Method method = ApplicationInfo.class.getDeclaredMethod("isPrivilegedApp");
            return (Boolean)method.invoke( pm.getApplicationInfo(pkg, 0));
        } catch(Exception e) {
            AppIndentifierLogs.printStackTrace(e);
            return false;
        }
    }
}
