package com.posn.initial_setup;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.posn.R;
import com.posn.application.POSNApplication;


public class SetupCloudProvidersActivity extends FragmentActivity implements OnClickListener
	{

		Button next;
		RelativeLayout dropboxButton, googleDriveButton, oneDriveButton, copyCloudButton, mediafireButton;

		POSNApplication app;


		@Override
		protected void onCreate(Bundle savedInstanceState)
			{
				super.onCreate(savedInstanceState);
				setContentView(R.layout.activity_setup_cloud_providers);

				// get the buttons from the layout
				next = (Button) findViewById(R.id.next_button);
				dropboxButton = (RelativeLayout) findViewById(R.id.dropbox_button);
				googleDriveButton = (RelativeLayout) findViewById(R.id.google_drive_button);
				oneDriveButton = (RelativeLayout) findViewById(R.id.onedrive_button);
				mediafireButton = (RelativeLayout) findViewById(R.id.mediafire_button);
				copyCloudButton = (RelativeLayout) findViewById(R.id.copy_cloud_button);

				// set an onclick listener for each button
				next.setOnClickListener(this);
				dropboxButton.setOnClickListener(this);
				googleDriveButton.setOnClickListener(this);
				oneDriveButton.setOnClickListener(this);
				copyCloudButton.setOnClickListener(this);
				mediafireButton.setOnClickListener(this);

				// get the action bar and set the page title
				ActionBar actionBar = getActionBar();
				actionBar.setTitle("Setup Cloud Providers");

				app = (POSNApplication) this.getApplication();
			}


		@Override
		protected void onResume()
			{
				super.onResume();
				//if (app.getDropbox() != null){
				//	System.out.println("HELLO!!!!");
					//app.getDropbox().authenticateDropboxLogin();
				//}
			}


		@Override
		public void onClick(View v)
			{
				switch(v.getId())
				{

					case R.id.next_button:

						if (app.getCloudProvider() != null)
							{
								Intent intent = new Intent(this, SetupEncryptionKeysActivity.class);
								startActivity(intent);
							}
						else
							{
								Toast.makeText(this, "Please select and login to a cloud provider.", Toast.LENGTH_SHORT).show();
							}
						break;

					case R.id.dropbox_button:

						//app.setDropbox(new DropboxClientUsage(this));
						//app.setCloudProvider("Dropbox");
						//app.getDropbox().initializeDropbox();

						// app.getDropbox().uploadFile("/multimedia/Test.jpg", app.multimediaFilePath + "/Test2.jpg");

						break;

					case R.id.google_drive_button:

						break;
					case R.id.onedrive_button:

						break;
					case R.id.mediafire_button:

						break;
					case R.id.copy_cloud_button:

						break;

				//

				}
			}
	}
