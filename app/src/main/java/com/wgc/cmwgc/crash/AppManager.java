package com.wgc.cmwgc.crash;

import java.util.Stack;

import android.app.Activity;

public class AppManager {
	 private static Stack<Activity> activityStack;  
	    private static AppManager instance;  
	      
	    public AppManager() {}  
	      
	    public static AppManager getAppManager(){  
	        if(instance==null){  
	            instance=new AppManager();  
	        }  
	        return instance;  
	    }  
	      
	    //���Activity����ջ  
	    public void addActivity(Activity activity){  
	        if(activityStack==null){  
	            activityStack=new Stack<Activity>();  
	        }  
	        activityStack.add(activity);  
	    }  
	      
	    public void finishAllActivity(){  
	        for(int i=0;i<activityStack.size();i++){  
	            if(activityStack.get(i) != null){  
	                activityStack.get(i).finish();  
	            }  
	        }  
	        activityStack.clear();  
	    }  
}
