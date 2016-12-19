package com.wgc.cmwgc.app;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.marswin89.marsdaemon.DaemonApplication;
import com.marswin89.marsdaemon.DaemonConfigurations;
import com.orhanobut.logger.Logger;
import com.wgc.cmwgc.crash.CrashHandler;
import com.wgc.cmwgc.service.CoreServer;
import com.wgc.cmwgc.service.Receiver1;
import com.wgc.cmwgc.service.Receiver2;
import com.wgc.cmwgc.service.Service2;
//
//import com.marswin89.marsdaemon.DaemonApplication;

/**
 * Created by Administrator on 2016/12/5.
 */
public class MyApplication extends DaemonApplication {



    @Override
    public void onCreate() {
        super.onCreate();
//        CrashHandler crashHandler=CrashHandler.getInstance();
//        crashHandler.init(getApplicationContext());
        Logger.i("MyApplication", " MyApplication ====== onCreate");
    }

    @Override
    public void attachBaseContextByDaemon(Context base) {
        super.attachBaseContextByDaemon(base);
    }

    /**
     * give the configuration to lib in this callback
     * @return
     */
    @Override
    protected DaemonConfigurations getDaemonConfigurations() {
        DaemonConfigurations.DaemonConfiguration configuration1 = new DaemonConfigurations.DaemonConfiguration(
                "com.wgc.cmwgc:process1",//一定是包名跟进程名字
                CoreServer.class.getCanonicalName(),
                Receiver1.class.getCanonicalName());

        DaemonConfigurations.DaemonConfiguration configuration2 = new DaemonConfigurations.DaemonConfiguration(
                "com.wgc.cmwgc:process2",//一定是包名跟进程名字
                Service2.class.getCanonicalName(),
                Receiver2.class.getCanonicalName());

        DaemonConfigurations.DaemonListener listener = new MyDaemonListener();
        //return new DaemonConfigurations(configuration1, configuration2);//listener can be null
        return new DaemonConfigurations(configuration1, configuration2, listener);
    }

    class MyDaemonListener implements DaemonConfigurations.DaemonListener{
        @Override
        public void onPersistentStart(Context context) {
        }

        @Override
        public void onDaemonAssistantStart(Context context) {

        }

        @Override
        public void onWatchDaemonDaed() {
        }
    }


}
