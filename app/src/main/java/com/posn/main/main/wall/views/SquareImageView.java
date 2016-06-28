package com.posn.main.main.wall.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;


/**
 * This view class creates a square image view for the photo wall post listview item
 **/
public class SquareImageView extends ImageView
   {
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
            int width = getMeasuredWidth();
            setMeasuredDimension(width, width);
         }
   }
