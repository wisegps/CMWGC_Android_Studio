package com.wicare.wistorm.widget;

import com.wicare.wistorm.R;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * @author Wu
 * 
 * 圆形进度RotationProgressBar
 *
 */
public class WRotationProgressBar extends ProgressBar {

	private int default_Color = Color.WHITE;
	
	public WRotationProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initDrawable(context, attrs, defStyleAttr);
	}

	public WRotationProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public WRotationProgressBar(Context context) {
		this(context, null);
	}

	private void initDrawable(Context context, AttributeSet attrs, int defStyleAttr) {
		if (isInEditMode()) {
			setIndeterminateDrawable(new WRotationProgressDrawable.Build().builder());
		}
		Resources resources = getResources();
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RotationProgressBar, defStyleAttr, 0);
		int color = array.getColor(R.styleable.RotationProgressBar_rpb_color, default_Color);
		setIndeterminateDrawable(new WRotationProgressDrawable.Build().setColor(color).setSweepSpeed(0.5f).setRotationSpeed(0.5f)
				.setSweepMin(20).setStroke(4).setSweepMax(300).builder());
	}
}
