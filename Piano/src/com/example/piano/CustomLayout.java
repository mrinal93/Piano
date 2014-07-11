package com.example.piano;

import android.content.Context;
import android.widget.RelativeLayout;

public class CustomLayout extends RelativeLayout{
	static public int windowW=0;
	static public int windowH=0 ;

	public CustomLayout(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
	{
		// we overriding onMeasure because this is where the application gets its right size.
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		windowW = getMeasuredWidth();
		windowH = getMeasuredHeight();
	}

}
