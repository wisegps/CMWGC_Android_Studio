package com.wgc.cmwgc.Until;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

public class WakeUpServiceManager {

	public static final String SERVICE_NAME = "com.rmt.speech.helper.service.WakeUpService";

	public static Intent sWakeUpIntent, sStartListeningIntent, sStopListeningIntent;
	
	public static final String ACTION_START_LISTENING = "com.rmt.action.wakeup.START_LISTENING";
	public static final String ACTION_STOP_LISTENING  = "com.rmt.action.wakeup.STOP_LISTENING";
	
	public static void start(Context context) {
		if (isRunning(context)) {
			return;
		}
		context.startService(getWakeUpIntent());
	}
	
	public static void stop(Context context) {
		if (!isRunning(context)) {
			return;
		}
		context.stopService(getWakeUpIntent());
	}
	
	public static void startListening(Context context) {
		if (!isRunning(context)) {
			context.startService(getWakeUpIntent());
		} else {
			if (sStartListeningIntent == null) {
				sStartListeningIntent = new Intent(ACTION_START_LISTENING);
			}
			context.sendBroadcast(sStartListeningIntent);
		}
	}
	
	private static Intent getWakeUpIntent() {
		if (sWakeUpIntent == null) {
			sWakeUpIntent = new Intent();
			sWakeUpIntent.setClassName("com.rmt.speech.helper", "com.rmt.speech.helper.service.WakeUpService");
		}
		return sWakeUpIntent;
	}
	
	public static void stopListening(Context context) {
		if (!isRunning(context)) {
			return;
		}
		if (sStopListeningIntent == null) {
			sStopListeningIntent = new Intent(ACTION_STOP_LISTENING);
		}
		context.sendBroadcast(sStopListeningIntent);
	}
	
	public static boolean isRunning(Context context) {
        ActivityManager myManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager.getRunningServices(100);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString().equals(SERVICE_NAME)) {
                return true;
            }
        }
        return false;
    }
}
