package com.wgc.cmwgc.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

/**
 * DO NOT do anything in this Receiver!<br/>
 *
 * Created by Mars on 12/24/15.
 */
public class Receiver1 extends BroadcastReceiver {

    /**实体按键报警，可以接收如下广播：
     "com.android.internal.policy.impl.alarmkey"
     实现原理:
     1，报警按键按下，以上广播不停发出；
     2，报警按键弹起，以上广播停止；*/

    private int alertFlag;

    @Override
    public void onReceive(Context context, Intent intent) {

//        if (intent.getAction().equals("com.android.internal.policy.impl.alarmkey")){
//
//            Toast.makeText(context, "智联车网紧急报警启动！", Toast.LENGTH_SHORT).show();
//            alertFlag=intent.getIntExtra("alertFlag", 1);
//            Bundle bundle = intent.getExtras();
//            int num = bundle.getInt("num",1);
//
//
//        }

    }
}
