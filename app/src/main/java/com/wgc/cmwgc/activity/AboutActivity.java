package com.wgc.cmwgc.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.orhanobut.logger.Logger;
import com.wgc.cmwgc.R;
import com.wgc.cmwgc.Until.SystemTools;
import com.wgc.cmwgc.app.Config;
import com.wgc.cmwgc.receiver.BootUpReceiver;
import com.wicare.wistorm.WiStormApi;
import com.wicare.wistorm.api.WErrorLog;
import com.wicare.wistorm.http.BaseVolley;
import com.wicare.wistorm.http.OnFailure;
import com.wicare.wistorm.http.OnSuccess;
import com.wicare.wistorm.versionupdate.VersionUpdate;
import com.wicare.wistorm.widget.CustomerDialog;

import java.io.File;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/11/2.
 */
public class AboutActivity extends AppCompatActivity {

    @Bind(R.id.tv_ver)
    TextView tvVer;
    @Bind(R.id.tv_check_update)
    TextView tvCheckUpdate;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        initView();
    }

    private void initView(){
        mContext = this;
        tvVer.setText("VER :" + SystemTools.getVersion(this));
//        tvCheckUpdate.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG ); //下划线
//        tvCheckUpdate.getPaint().setAntiAlias(true);//抗锯齿

        tvVer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AppInfoActivity.startAction(AboutActivity.this);
                return false;
            }
        });
    }

    /**
     * @param context
     */
    public static void startAction(Activity context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }


    String apkFileName;

    @OnClick(R.id.tv_check_update)
    public void onClick() {
        VersionUpdate updata = new VersionUpdate(this);
        updata.check(Config.UPDATA_APK_URL, new VersionUpdate.UpdateListener() {
            @Override
            public void hasNewVersion(boolean isHad, String updateMsg, String apkUrl) {
                if(!isHad){
                    SystemTools.showToast(mContext,"已经是最新版本");
                }
            }

            @Override
            public void finishDownloadApk(String saveFileName) {
                Logger.w("下载完成............." + saveFileName);
                apkFileName = saveFileName;
                showInstallDialog(mContext);
            }
        });
    }



    /**
     * 马上安装对话框
     * @param context
     */
    private void showInstallDialog(Context context){
        CustomerDialog.Builder builder = new CustomerDialog.Builder(context);
        builder.setTitle(context.getResources().getString(com.wicare.wistorm.R.string.new_version_install));
        builder.setMessage(context.getResources().getString(com.wicare.wistorm.R.string.if_install));
        builder.setPositiveButton(context.getResources().getString(com.wicare.wistorm.R.string.install_now), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                installApk(apkFileName);
            }
        });
        builder.setNegativeButton( new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }


    /**
     * 安装apk
     *
     */
    private void installApk(String saveFileName) {
        File apkfile = new File(saveFileName);
        if (!apkfile.exists()) {
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        startActivity(i);
    }
}
