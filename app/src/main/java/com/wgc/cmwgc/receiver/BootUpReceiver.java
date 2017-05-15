package com.wgc.cmwgc.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.wgc.cmwgc.Until.SystemTools;
import com.wgc.cmwgc.service.CoreServer;
import com.wgc.cmwgc.service.HttpService;

import java.util.Timer;
import java.util.TimerTask;

public class BootUpReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.e("BootUpReceiver", "shou dao  kai ji  guang bo ......... ");
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			Toast.makeText(context, "智联车网已启动！", Toast.LENGTH_SHORT).show();
			startService(context);
		}

	}
	/**
	 * @param context
	 */
	public static void startService(final Context context){
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				Intent intent_service = new Intent(context,CoreServer.class);
				context.startService(intent_service);
			}
		};
		timer.schedule(task, 12000);
	}



}
