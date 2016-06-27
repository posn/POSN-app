package com.posn.main.wall.comments;

import android.app.Activity;
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
import com.posn.datatypes.Friend;
import com.posn.datatypes.User;
import com.posn.datatypes.WallPost;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class CommentActivity extends Activity implements View.OnClickListener
   {

      // declare variables
      WallPost post;
      ArrayList<Comment> commentList;

      TextView numCommentsTextView;
      TextView noCommentsTextView;

      EditText commentBoxText;
      User user;
      CommentArrayAdapter adapter;

      ArrayList<Comment> newUserComments = new ArrayList<>();

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
                  commentList = post.comments;
               }

            if (commentList != null)
               {
                  String message = commentList.size() + " people have commented on this";
                  numCommentsTextView.setText(message);

                  if (commentList.size() > 0)
                     {
                        noCommentsTextView.setVisibility(View.GONE);
                     }
               }

            user = getIntent().getExtras().getParcelable("user");
            HashMap<String, Friend> friends = (HashMap<String, Friend>) getIntent().getExtras().getSerializable("friends");

            adapter = new CommentArrayAdapter(this, commentList, friends, user);
            lv.setAdapter(adapter);
         }

      @Override public void onClick(View v)
         {
            switch (v.getId())
               {
                  case R.id.post_button:

                     if (!isEmpty(commentBoxText))
                        {
                           String comment = commentBoxText.getText().toString().trim();

                           Comment newComment = new Comment(user.ID, post.postID, comment);

                           commentList.add(newComment);
                           newUserComments.add(newComment);
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
                  if(newUserComments.size() > 0)
                     {
                        // THIS NEEDS TO GO IN THE ASYNC TASK TO PROCESS THE COMMENT
                           // NEED TO DO THIS CHECK TO DETERMINE IF THE COMMENTS ARE EMBEDDED DIRECTLY INTO POST AND UPDATE THE WALLS

                        // check if the post is a user or friendID post
                        if(post.friendID.equals(user.ID))
                           {

                           }
                        else
                           {

                           }

                        finish();
                     }

                  return true;
               }

            return super.onKeyDown(keyCode, event);
         }

      public void updateAdapter()
         {
            commentBoxText.setText("");

            String message = commentList.size() + " comments have been made";
            numCommentsTextView.setText(message);

            if (commentList.size() > 0)
               {
                  noCommentsTextView.setVisibility(View.GONE);
               }

            sortCommentList();
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

            Collections.sort(commentList, commentDateComparator);
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

      private boolean isEmpty(EditText etText)
         {
            // check if the length of the text is greater than 0
            return (etText.getText().toString().trim().length() <= 0);
         }

   }
