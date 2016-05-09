package com.posn.main.wall.comments;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ListView;

import com.posn.R;
import com.posn.datatypes.Comment;

import java.util.ArrayList;


public class CommentActivity extends Activity
	{

		// declare variables
		ArrayList<Comment> commentList = new ArrayList<>();


		@Override
		protected void onCreate(Bundle savedInstanceState)
			{
				super.onCreate(savedInstanceState);

				// load the xml file for the logs

				requestWindowFeature(Window.FEATURE_NO_TITLE);
				this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);

				LayoutParams params = this.getWindow().getAttributes();
				params.alpha = 1.0f;
				params.dimAmount = 0.5f;
				this.getWindow().setAttributes(params);

				Display display = getWindowManager().getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);
				int width = size.x;
				int height = size.y;

				int statusBarHeight = getStatusBarHeight();

				// This sets the window size, while working around the IllegalStateException thrown by ActionBarView
				getWindow().setLayout(width, height - statusBarHeight);
				getWindow().setGravity(Gravity.BOTTOM);

				setContentView(R.layout.activity_post_comment);

				ListView lv = (ListView) findViewById(R.id.commentsListView);

				getComments();
				

				final CommentArrayAdapter adapter = new CommentArrayAdapter(this, commentList);
				lv.setAdapter(adapter);
			}


		public void getComments()
			{
				commentList.clear();

				commentList.add(new Comment("Test User 1", "Apr 18 at 1:56 pm", "What kind of dog?"));
				commentList.add(new Comment("Test User 2", "Apr 18 at 1:56 pm", "I think its a Jindo"));
				commentList.add(new Comment("Test User 1", "Apr 18 at 1:56 pm", "Thanks, she is very cute!"));
				commentList.add(new Comment("Test User 3", "Apr 18 at 1:56 pm", "How adorable"));
			}


		public int getStatusBarHeight()
			{
				int result = 0;
				int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
				if (resourceId > 0)
					{
						result = getResources().getDimensionPixelSize(resourceId);
					}
				return result;
			}

	}
