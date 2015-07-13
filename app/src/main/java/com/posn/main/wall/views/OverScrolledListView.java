package com.posn.main.wall.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ListView;


public class OverScrolledListView extends ListView
	{

		private final String TAG = "ListView";


		public OverScrolledListView(Context context)
			{
				super(context);
				setOverScrollMode(OVER_SCROLL_ALWAYS);
				init();
			}


		public OverScrolledListView(Context context, AttributeSet attrs)
			{
				super(context, attrs);
				init();
			}


		private void init()
			{
				setOverScrollMode(OVER_SCROLL_ALWAYS);
			}


		@Override
		protected boolean overScrollBy(int deltaX, int deltaY, int scrollX, int scrollY, int scrollRangeX, int scrollRangeY, int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent)
			{

				return super.overScrollBy(0, deltaY, 0, scrollY, 0, scrollRangeY, 0, 200, isTouchEvent);

			}


		@Override
		protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY)
			{

				Log.v(TAG, "scrollX:" + scrollX + " scrollY:" + scrollY + " clampedX:" + clampedX + " clampedY:" + clampedX);

				super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);

			}
	}
