package com.posn.main.wall;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.posn.R;


public class PhotoViewerActivity extends Activity 
	{

		// declare variables
		private String photoPath;

		@Override
		protected void onCreate(Bundle savedInstanceState)
			{
				super.onCreate(savedInstanceState);

				// load the xml file for the logs
				setContentView(R.layout.activity_photo_viewer);
				
				Intent intent = getIntent();
				photoPath = intent.getExtras().getString("photoPath");
				
				ImageView imageView = (ImageView) findViewById(R.id.image);
				
				imageView.setImageURI(Uri.fromFile(new File(photoPath)));

			}

		@Override
		protected void onStop()
			{
        super.onStop();
			}

		


	}
