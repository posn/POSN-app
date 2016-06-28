package com.posn.main.initial_setup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


/**
 * This view class implements the functionality for drawing in a blank view
 * <ul><li>Used by the SetupEncrytionKeysActivity to get random seed values as the user draws in the view</ul>
 **/
public class DrawingView extends View
   {
      // declare public variables
      public int width;
      public int height;

      // declare private variables
      private boolean isTouched;
      private boolean allowDrawing;
      private Bitmap mBitmap;
      private Canvas mCanvas;
      private Path mPath;
      private Paint mBitmapPaint;
      private Paint circlePaint;
      private Path circlePath;
      private Paint mPaint;

      private int totalXValues;
      private int totalYValues;


      public DrawingView(final Context context, final AttributeSet attrs, final int defStyle)
         {
            super(context, attrs, defStyle);
            mPath = new Path();
            mBitmapPaint = new Paint(Paint.DITHER_FLAG);
            circlePaint = new Paint();
            circlePath = new Path();
            circlePaint.setAntiAlias(true);
            circlePaint.setColor(Color.BLUE);
            circlePaint.setStyle(Paint.Style.STROKE);
            circlePaint.setStrokeJoin(Paint.Join.MITER);
            circlePaint.setStrokeWidth(4f);

            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            mPaint.setColor(Color.GREEN);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeWidth(12);

            isTouched = false;
            totalXValues = 0;
            totalYValues = 0;
         }


      public DrawingView(final Context context, final AttributeSet attrs)
         {
            this(context, attrs, 0);
         }


      public DrawingView(final Context context)
         {
            this(context, null, 0);
         }


      @Override
      protected void onSizeChanged(int w, int h, int oldw, int oldh)
         {
            super.onSizeChanged(w, h, oldw, oldh);

            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);

         }


      @Override
      protected void onDraw(Canvas canvas)
         {
            super.onDraw(canvas);

            canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

            canvas.drawPath(mPath, mPaint);

            canvas.drawPath(circlePath, circlePaint);
         }

      private float mX, mY;
      private static final float TOUCH_TOLERANCE = 4;


      private void touch_start(float x, float y)
         {
            isTouched = true;
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;
         }


      private void touch_move(float x, float y)
         {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE)
               {
                  mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                  mX = x;
                  mY = y;

                  circlePath.reset();
                  circlePath.addCircle(mX, mY, 30, Path.Direction.CW);
               }
            totalXValues += dx;
            totalYValues += dy;
            System.out.println(totalXValues + " | " + totalYValues);
         }


      private void touch_up()
         {
            isTouched = false;
            mPath.lineTo(mX, mY);
            circlePath.reset();
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            mPath.reset();
         }


      @Override
      public boolean onTouchEvent(MotionEvent event)
         {
            float x = event.getX();
            float y = event.getY();
            if (allowDrawing)
               {
                  switch (event.getAction())
                     {
                        case MotionEvent.ACTION_DOWN:
                           touch_start(x, y);
                           invalidate();
                           break;
                        case MotionEvent.ACTION_MOVE:
                           touch_move(x, y);
                           invalidate();
                           break;
                        case MotionEvent.ACTION_UP:
                           touch_up();
                           invalidate();
                           break;
                     }
               }
            else
               {
                  circlePath.reset();
                  invalidate();

               }
            return true;
         }


      public boolean isTouched()
         {
            return isTouched;
         }


      public void resetDrawView()
         {
            totalXValues = 0;
            totalYValues = 0;
            mCanvas.drawColor(Color.WHITE);
            invalidate();
         }


      public void allowDrawing()
         {
            allowDrawing = true;
         }


      public void disableDrawing()
         {
            allowDrawing = false;
         }

      public int getRandomValues()
         {
            return totalXValues * totalYValues;

         }
   }
