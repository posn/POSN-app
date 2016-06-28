package com.posn.main.main.wall.comments;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.posn.R;
import com.posn.datatypes.Comment;
import com.posn.datatypes.WallPost;
import com.posn.managers.AppDataManager;
import com.posn.utility.UserInterfaceHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class CommentActivity extends Activity implements View.OnClickListener
   {
      // declare variables
      TextView numCommentsTextView;
      TextView noCommentsTextView;

      EditText commentBoxText;
      CommentArrayAdapter adapter;

      public AppDataManager dataManager;
      public WallPost post;

      private ArrayList<Comment> newUserComments = new ArrayList<>();

      @Override
      protected void onCreate(Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);

            initializeWindow();

            setContentView(R.layout.activity_post_comment);

            ListView lv = (ListView) findViewById(R.id.commentsListView);

            noCommentsTextView = (TextView) findViewById(R.id.notification_text);
            numCommentsTextView = (TextView) findViewById(R.id.numComments);

            commentBoxText = (EditText) findViewById(R.id.writeComment);
            Button postCommentButton = (Button) findViewById(R.id.post_button);
            postCommentButton.setOnClickListener(this);

            //getComments();
            if (getIntent().getExtras().containsKey("post"))
               {
                  post = getIntent().getExtras().getParcelable("post");
               }


            dataManager = getIntent().getExtras().getParcelable("dataManager");

            adapter = new CommentArrayAdapter(this, post.comments, dataManager.friendManager.currentFriends, dataManager.userManager);
            lv.setAdapter(adapter);


            updateAdapter();
         }

      @Override public void onClick(View v)
         {
            switch (v.getId())
               {
                  case R.id.post_button:

                     if (!UserInterfaceHelper.isEditTextEmpty(commentBoxText))
                        {
                           String commentText = commentBoxText.getText().toString().trim();

                           Comment newComment = new Comment(dataManager.userManager.ID, post.postID, commentText);

                           newUserComments.add(newComment);

                           post.comments.add(newComment);

                           // update the comment listview
                           updateAdapter();
                        }

                     break;
               }
         }

      @Override
      public boolean onKeyDown(int keyCode, KeyEvent event)
         {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
               {
                  // check if new comments need to be processed
                  if (newUserComments.size() > 0)
                     {
                        // create a new intent to pass the newComments back to the UserWall Fragment
                        Intent resultIntent = new Intent();
                        resultIntent.putParcelableArrayListExtra("newUserComments", newUserComments);
                        resultIntent.putExtra("post", post);
                        setResult(Activity.RESULT_OK, resultIntent);
                     }

                  // close the activity
                  finish();

                  return true;
               }

            return super.onKeyDown(keyCode, event);
         }

      public void updateAdapter()
         {
            // clear the edit text
            commentBoxText.setText("");

            // update the number of comments message
            String message = post.comments.size() + " comments have been made";
            numCommentsTextView.setText(message);

            // if at least one comment, remove the "No comments" view and sort the comments
            if (post.comments.size() > 0)
               {
                  noCommentsTextView.setVisibility(View.GONE);
                  sortCommentList();
               }

            // show new data in the listview
            adapter.notifyDataSetChanged();
         }

      public void sortCommentList()
         {
            Comparator<Comment> commentDateComparator = new Comparator<Comment>()
               {
                  public int compare(Comment emp1, Comment emp2)
                     {
                        return (emp1.date.compareTo(emp2.date));
                     }
               };

            Collections.sort(post.comments, commentDateComparator);
         }


      public void initializeWindow()
         {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_DIM_BEHIND);

            WindowManager.LayoutParams params = this.getWindow().getAttributes();
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
