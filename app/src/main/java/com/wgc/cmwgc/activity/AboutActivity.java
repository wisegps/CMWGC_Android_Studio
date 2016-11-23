package com.wgc.cmwgc.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.wgc.cmwgc.R;
import com.wgc.cmwgc.Until.SystemTools;
import com.wgc.cmwgc.app.Config;
import com.wicare.wistorm.versionupdate.VersionUpdate;

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
    }

    /**
     * @param context
     */
    public static void startAction(Activity context) {
        Intent intent = new Intent(context, AboutActivity.class);
        context.startActivity(intent);
    }

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
        });
    }
}
