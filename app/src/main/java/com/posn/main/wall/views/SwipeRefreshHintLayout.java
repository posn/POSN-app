package com.posn.main.wall.views;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;


public class SwipeRefreshHintLayout extends RelativeLayout
	{

		public SwipeRefreshHintLayout(Context context)
			{
				super(context);
			}


		public SwipeRefreshHintLayout(Context context, AttributeSet attrs)
			{
				super(context, attrs);
			}


		public SwipeRefreshHintLayout(Context context, AttributeSet attrs, int defStyle)
			{
				super(context, attrs, defStyle);
			}


		public void setSwipeLayoutTarget(final SwipeRefreshLayout swipeRefreshLayout)
			{

				final View swipeTarget = swipeRefreshLayout.getChildAt(0);
				if (swipeTarget == null)
					{
						return;
					}

				swipeTarget.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener()
					{

						private Rect oldBounds = new Rect(), newBounds = new Rect();


						@Override
						public boolean onPreDraw()
							{
								newBounds.set(swipeTarget.getLeft(), swipeRefreshLayout.getTop(), swipeTarget.getRight(), swipeTarget.getTop());

								if (!oldBounds.equals(newBounds))
									{
										getLayoutParams().height = newBounds.height();
										requestLayout();
										oldBounds.set(newBounds);
									}
								return true;
							}
					});

			}

	}
