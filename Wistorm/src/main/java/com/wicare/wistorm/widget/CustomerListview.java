package com.wicare.wistorm.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 类描述：
 * 作  者：Wu
 * 时  间：4/27/2016 10:56 AM
 * 修改备注：
 */
public class CustomerListview extends ListView{


    public CustomerListview(Context context){
        super(context);

    }
    public CustomerListview(Context context, AttributeSet attrs) {

        super(context, attrs);
    }
    public CustomerListview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,

                MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
