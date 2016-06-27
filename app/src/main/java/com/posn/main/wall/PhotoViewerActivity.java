package com.posn.main.wall;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.posn.R;
import com.posn.asynctasks.wall.LoadImageAsyncTask;
import com.posn.datatypes.WallPost;

/**
 * This activity class allows the user to view the full image that was clicked from a wall post
 **/
public class PhotoViewerActivity extends Activity
   {
      /**
       * This method is called when the activity needs to be created and handles the interface elements.
       * <ul><li>Fetches the wall post object that was passed in by the wall fragment
       * <li>Loads the photo to the imageview in an asynctask</ul>
       **/
      @Override
      protected void onCreate(Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);

            // load the user interface layout from the xml file
            setContentView(R.layout.activity_photo_viewer);

            // get the wall post object that was passed from the wall fragment
            WallPost wallPost = getIntent().getExtras().getParcelable("post");

            // get the imageview and loading spinner from the layout
            ImageView imageView = (ImageView) findViewById(R.id.image);
            RelativeLayout loadingSpinner = (RelativeLayout) findViewById(R.id.loadingPanel);

            // check if the wall object exists
            if (wallPost != null)
               {
                  // set the imageview tag with the wall post
                  imageView.setTag(wallPost);

                  // load the image from the device
                  new LoadImageAsyncTask(imageView, loadingSpinner).execute();

                  // set the imageview background to transparent and visible to see the loading circle
                  imageView.setImageResource(android.R.color.transparent);
                  imageView.setVisibility(View.VISIBLE);
               }
         }
   }
