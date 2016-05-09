package com.posn.main.wall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.posn.R;
import com.posn.asynctasks.LoadImageAsyncTask;
import com.posn.datatypes.Post;


public class PhotoViewerActivity extends Activity
   {

      // declare variables
      ImageView imageView;
      RelativeLayout loadingSpinner;

      @Override
      protected void onCreate(Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);

            // load the xml file for the logs
            setContentView(R.layout.activity_photo_viewer);

            Intent intent = getIntent();
            Post post = intent.getExtras().getParcelable("post");


            imageView = (ImageView) findViewById(R.id.image);
            loadingSpinner = (RelativeLayout) findViewById(R.id.loadingPanel);

            if (post != null)
               {
                  imageView.setTag(post);
                  new LoadImageAsyncTask(imageView, loadingSpinner).execute();
                  imageView.setImageResource(android.R.color.transparent);
                  imageView.setVisibility(View.VISIBLE);
               }
         }

      @Override
      protected void onStop()
         {
            super.onStop();
         }
   }
