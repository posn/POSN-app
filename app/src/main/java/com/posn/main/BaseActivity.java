package com.posn.main;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.posn.application.POSNApplication;
import com.posn.clouds.GoogleDrive.GoogleDriveClientUsage;


public abstract class BaseActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
   {
      private static final String TAG = "BaseDriveActivity";

      protected static final int REQUEST_CODE_RESOLUTION = 1;
      protected static final int NEXT_AVAILABLE_REQUEST_CODE = 2;

      private GoogleDriveClientUsage googleDrive = null;

      public POSNApplication app;

      /**
       * Called when activity gets visible. A connection to Drive services need to
       * be initiated as soon as the activity is visible. Registers
       * {@code ConnectionCallbacks} and {@code OnConnectionFailedListener} on the
       * activities itself.
       */
      @Override
      protected void onResume()
         {
            super.onResume();
            if (googleDrive == null)
               {
                  googleDrive = new GoogleDriveClientUsage(this);
                  googleDrive.mGoogleApiClient = new GoogleApiClient.Builder(this)
                                                     .addApi(Drive.API)
                                                     .addScope(Drive.SCOPE_FILE)
                                                     .addScope(Drive.SCOPE_APPFOLDER) // required for App Folder sample
                                                     .addConnectionCallbacks(this)
                                                     .addOnConnectionFailedListener(this)
                                                     .build();
               }
            googleDrive.mGoogleApiClient.connect();
         }

      /**
       * Handles resolution callbacks.
       */
      @Override
      protected void onActivityResult(int requestCode, int resultCode, Intent data)
         {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == REQUEST_CODE_RESOLUTION && resultCode == RESULT_OK)
               {
                  googleDrive.mGoogleApiClient.connect();
               }
         }

      /**
       * Called when activity gets invisible. Connection to Drive service needs to
       * be disconnected as soon as an activity is invisible.
       */
      @Override
      protected void onPause()
         {
            if (googleDrive.mGoogleApiClient != null)
               {
                  googleDrive.mGoogleApiClient.disconnect();
               }
            super.onPause();
         }

      /**
       * Called when {@code mGoogleApiClient} is connected.
       */
      @Override
      public void onConnected(Bundle connectionHint)
         {
            Log.i(TAG, "GoogleApiClient connected");

            googleDrive.createStorageDirectories();

            app = (POSNApplication)getApplication();
         }

      /**
       * Called when {@code mGoogleApiClient} is disconnected.
       */
      @Override
      public void onConnectionSuspended(int cause)
         {
            Log.i(TAG, "GoogleApiClient connection suspended");
         }

      /**
       * Called when {@code mGoogleApiClient} is trying to connect but failed.
       * Handle {@code result.getResolution()} if there is a resolution is
       * available.
       */
      @Override
      public void onConnectionFailed(ConnectionResult result)
         {
            Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
            if (!result.hasResolution())
               {
                  // show the localized error dialog.
                  GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
                  return;
               }
            try
               {
                  result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
               }
            catch (IntentSender.SendIntentException e)
               {
                  Log.e(TAG, "Exception while starting resolution activity", e);
               }
         }
   }
