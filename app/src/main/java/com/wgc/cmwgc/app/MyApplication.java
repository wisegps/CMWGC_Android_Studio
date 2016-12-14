package com.wgc.cmwgc.app;

import android.app.Application;
import android.util.Log;

import com.orhanobut.logger.Logger;
import com.wgc.cmwgc.crash.CrashHandler;

/**
 * Created by Administrator on 2016/12/5.
 */
public class MyApplication extends Application {




    @Override
    public void onCreate() {
        super.onCreate();
//        CrashHandler crashHandler=CrashHandler.getInstance();
//        crashHandler.init(getApplicationContext());
        Logger.i("MyApplication", " MyApplication ====== onCreate");
    }
}
