package com.posn.main.wall;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.posn.Constants;
import com.posn.R;
import com.posn.datatypes.WallPost;


public class VideoPlayerActivity extends Activity
   {
      @Override
      protected void onCreate(Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);

            // load the xml file for the logs
            setContentView(R.layout.activity_video_player);

            // videoURI = getIntent().getData();
            Intent intent = getIntent();
            WallPost wallPost = intent.getExtras().getParcelable("post");

            if (wallPost != null)
               {
                  String filepath = Constants.multimediaFilePath + "/" + wallPost.postID + ".mp4";

                  VideoView videoView = (VideoView) findViewById(R.id.video);

                  MediaController mc = new MediaController(this);
                  videoView.setMediaController(mc);
                  mc.setAnchorView(videoView);

                  videoView.setVideoPath(filepath);
                  videoView.start();
               }


         }

      @Override
      protected void onStop()
         {
            super.onStop();
         }


   }
