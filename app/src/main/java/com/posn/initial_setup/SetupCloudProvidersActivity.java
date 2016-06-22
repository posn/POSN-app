package com.posn.initial_setup;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.posn.Constants;
import com.posn.R;
import com.posn.clouds.DropboxClientUsage;
import com.posn.clouds.GoogleDriveClientUsage;
import com.posn.clouds.OneDriveClientUsage;
import com.posn.datatypes.User;
import com.posn.main.BaseActivity;


public class SetupCloudProvidersActivity extends BaseActivity implements OnClickListener
   {

      Button next;
      RelativeLayout dropboxButton, googleDriveButton, oneDriveButton;

      User user;
      String password;

      @Override
      protected void onCreate(Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_setup_cloud_providers);

            if (getIntent().hasExtra("user"))
               {
                  user = (User) getIntent().getExtras().get("user");
                  password = getIntent().getExtras().getString("password");
               }

            // get the buttons from the layout
            next = (Button) findViewById(R.id.next_button);
            dropboxButton = (RelativeLayout) findViewById(R.id.dropbox_button);
            googleDriveButton = (RelativeLayout) findViewById(R.id.google_drive_button);
            oneDriveButton = (RelativeLayout) findViewById(R.id.onedrive_button);


            // set an onclick listener for each button
            next.setOnClickListener(this);
            dropboxButton.setOnClickListener(this);
            googleDriveButton.setOnClickListener(this);
            oneDriveButton.setOnClickListener(this);


            // get the action bar and set the page title
            ActionBar actionBar = getActionBar();
            if (actionBar != null)
               {
                  actionBar.setTitle("Setup Cloud Providers");
               }
         }


      @Override
      public void onClick(View v)
         {
            switch (v.getId())
               {

                  case R.id.next_button:

                     if (app.cloud.isConnected)
                        {
                           Intent intent = new Intent(this, SetupEncryptionKeysActivity.class);
                           intent.putExtra("user", user);
                           intent.putExtra("password", password);
                           startActivity(intent);
                        }
                     else
                        {
                           Toast.makeText(this, "Please select and login to a cloud provider.", Toast.LENGTH_SHORT).show();
                        }
                     break;

                  case R.id.dropbox_button:
                     app.cloud = new DropboxClientUsage(this);
                     app.cloud.initializeCloud();
                     user.cloudProvider = Constants.PROVIDER_DROPBOX;
                     break;

                  case R.id.google_drive_button:
                     app.cloud = new GoogleDriveClientUsage(this);
                     app.cloud.initializeCloud();
                     user.cloudProvider = Constants.PROVIDER_GOOGLEDRIVE;

                     break;
                  case R.id.onedrive_button:
                     app.cloud = new OneDriveClientUsage(this);
                     app.cloud.initializeCloud();
                     user.cloudProvider = Constants.PROVIDER_ONEDRIVE;


                     break;
               }
         }
   }
