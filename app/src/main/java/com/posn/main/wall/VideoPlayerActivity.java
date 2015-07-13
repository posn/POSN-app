package com.posn.main.wall;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.posn.R;


public class VideoPlayerActivity extends Activity 
	{

		// declare variables
		private Uri videoURI;

		@Override
		protected void onCreate(Bundle savedInstanceState)
			{
				super.onCreate(savedInstanceState);

				// load the xml file for the logs
				setContentView(R.layout.activity_video_player);

				videoURI = getIntent().getData();

				VideoView videoView = (VideoView) findViewById(R.id.video);
				
				MediaController mc = new MediaController(this);
				videoView.setMediaController(mc);
				mc.setAnchorView(videoView);
				
				videoView.setVideoURI(videoURI);
				videoView.start();

			}

		@Override
		protected void onStop()
			{
        super.onStop();
			}

		


	}
