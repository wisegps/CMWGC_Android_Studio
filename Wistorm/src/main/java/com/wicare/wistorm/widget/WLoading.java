package com.wicare.wistorm.widget;


import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.wicare.wistorm.R;

/**
 * @author Wu
 * 
 * 自定义加载框
 */
public class WLoading extends Dialog{
	 
	private Context context;
	private static WLoading wLoading;
	public static int LARGE_TYPE = 1;
	public static int SMALL_TYPE = 2;
	
	
	public WLoading(Context context) {
		super(context);
		this.context = context;
	}
	
	public WLoading(Context context, int theme) {
	    super(context, theme);
	}
	     
    /**
     * 设置WLoading的参数
     * @param context
     * @param type Loading 样式  ：type=1 样式是Large;type=2 样式是Small;
     * @return
     */
    public static WLoading createDialog(Context context,int type){  	
        wLoading = new WLoading(context,R.style.progressDialog);
        if(type == 1){
        	wLoading.setContentView(R.layout.ws_progressbar_loading_large);
        }else{
        	wLoading.setContentView(R.layout.ws_progressbar_loading_small);
        }
        wLoading.getWindow().getAttributes().gravity = Gravity.CENTER;
        wLoading.getWindow().getAttributes().width  =  LayoutParams.WRAP_CONTENT;
        wLoading.getWindow().getAttributes().height =  LayoutParams.WRAP_CONTENT;
        return wLoading;
    }
	  
    
    /**
     * 设置加载框显示的信息
     * @param strMessage
     * @return
     *
     */
    public WLoading setMessage(String strMessage){
        TextView tvMsg = (TextView)wLoading.findViewById(R.id.tv_loading_msg);
        if (tvMsg != null){
            tvMsg.setText(strMessage);
        } 
        return wLoading;
    }
}
