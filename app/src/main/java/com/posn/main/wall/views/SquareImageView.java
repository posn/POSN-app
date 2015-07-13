package com.posn.main.wall.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;


public class SquareImageView extends ImageView
	{

		private int width;
		boolean allocated = false;


		public SquareImageView(Context context)
			{
				super(context);
			}


		public SquareImageView(Context context, AttributeSet attrs)
			{
				super(context, attrs);
			}


		public SquareImageView(Context context, AttributeSet attrs, int defStyle)
			{
				super(context, attrs, defStyle);
			}


		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
			{
				super.onMeasure(widthMeasureSpec, heightMeasureSpec);

				System.out.println("HEY!");
				width = getMeasuredWidth();
				setMeasuredDimension(width, width);

			}


		public int getViewWidth()
			{
				return width;
			}
	}
