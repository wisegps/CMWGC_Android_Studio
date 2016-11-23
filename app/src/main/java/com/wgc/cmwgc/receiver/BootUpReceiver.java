package com.wgc.cmwgc.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.wgc.cmwgc.Until.SystemTools;
import com.wgc.cmwgc.service.HttpService;

import java.util.Timer;
import java.util.TimerTask;

public class BootUpReceiver extends BroadcastReceiver{
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.e("HttpService", "shou dao  kai ji  guang bo ");
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){ 
			startService(context);
	    } else if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)){
			if(intent.getDataString().equals(context.getPackageName())){
				//安装完APK
			}
		}
	}
	
	
	
	/**
	 * @param context
	 */
	private void startService(final Context context){
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				Intent intent_service = new Intent(context,HttpService.class);
				context.startService(intent_service);
			}
		};
		timer.schedule(task, 12000);
		

	}

}