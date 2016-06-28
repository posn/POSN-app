package com.posn.main.initial_setup;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.posn.constants.Constants;
import com.posn.R;
import com.posn.clouds.DropboxProvider;
import com.posn.clouds.GoogleDriveProvider;
import com.posn.clouds.OnConnectedCloudListener;
import com.posn.clouds.OneDriveProvider;
import com.posn.managers.UserManager;
import com.posn.main.BaseActivity;


/**
 * This activity class implements the functionality to link the new user's cloud account to the application
 * Clouds implemented: Dropbox, Google Drive, OneDrive
 **/
public class SetupCloudProvidersActivity extends BaseActivity implements OnClickListener, OnConnectedCloudListener
   {
      // user interface variables
      Button next;
      RelativeLayout dropboxButton, googleDriveButton, oneDriveButton;

      // user object to store the information about the new user
      UserManager userManager;

      // password object to pass to next activity
      String password;


      /**
       * This method is called when the activity needs to be created and handles setting up the user interface objects and sets listeners for touch events.
       **/
      @Override
      protected void onCreate(Bundle savedInstanceState)
         {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_setup_cloud_providers);

            // get the new user and password from the previous activity
            if (getIntent().hasExtra("user"))
               {
                  userManager = (UserManager) getIntent().getExtras().get("user");
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

      /**
       * This method is called when the application was successfully connected to the cloud provider
       * Starts the set up encryption activty
       **/
      @Override
      public void OnConnected()
         {
            // launch the set up encryption activity
            Intent intent = new Intent(this, SetupEncryptionKeysActivity.class);
            intent.putExtra("user", userManager);
            intent.putExtra("password", password);
            startActivity(intent);
         }


      /**
       * This method is called when the user touches a UI element and gives the element its functionality
       **/
      @Override
      public void onClick(View v)
         {
            switch (v.getId())
               {
                  case R.id.next_button:

                     // display an error message prompting for the user to select a cloud provider
                     Toast.makeText(this, "Please select and login to a cloud provider.", Toast.LENGTH_SHORT).show();
                     break;

                  case R.id.dropbox_button:

                     // set the cloud provider object in the application class to Dropbox
                     app.cloud = new DropboxProvider(this, this);
                     app.cloud.initializeCloud();
                     userManager.cloudProvider = Constants.PROVIDER_DROPBOX;
                     break;

                  case R.id.google_drive_button:

                     // set the cloud provider object in the application class to Google Drive
                     app.cloud = new GoogleDriveProvider(this, this);
                     app.cloud.initializeCloud();
                     userManager.cloudProvider = Constants.PROVIDER_GOOGLEDRIVE;

                     break;

                  case R.id.onedrive_button:

                     // set the cloud provider object in the application class to OneDrive
                     app.cloud = new OneDriveProvider(this, this);
                     app.cloud.initializeCloud();
                     userManager.cloudProvider = Constants.PROVIDER_ONEDRIVE;
                     break;
               }
         }
   }
