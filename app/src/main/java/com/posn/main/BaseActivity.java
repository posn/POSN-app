package com.posn.main;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.microsoft.live.LiveAuthException;
import com.microsoft.live.LiveAuthListener;
import com.microsoft.live.LiveConnectClient;
import com.microsoft.live.LiveConnectSession;
import com.microsoft.live.LiveStatus;
import com.posn.application.POSNApplication;
import com.posn.clouds.GoogleDrive.GoogleDriveClientUsage;
import com.posn.clouds.OneDrive.OneDriveClientUsage;

import java.util.Arrays;


public abstract class BaseActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
   {
      private static final String TAG = "BaseDriveActivity";

      protected static final int REQUEST_CODE_RESOLUTION = 1;
      protected static final int NEXT_AVAILABLE_REQUEST_CODE = 2;

      private GoogleDriveClientUsage googleDrive = null;

      private OneDriveClientUsage oneDrive = null;

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

            /*
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
            */

            if (oneDrive == null)
               {
                  oneDrive = new OneDriveClientUsage(this);

                  // initialize skydrive
                  oneDrive.mAuthClient.initialize(Arrays.asList(oneDrive.SCOPES), new LiveAuthListener()
                  {

                     @Override
                     public void onAuthError(LiveAuthException exception, Object userState)
                        {
                           //mInitializeDialog.dismiss();
                           showToast(exception.getMessage());
                        }


                     @Override
                     public void onAuthComplete(LiveStatus status, LiveConnectSession session, Object userState)
                        {
                           // mInitializeDialog.dismiss();

                           if (status == LiveStatus.CONNECTED)
                              {
                                 showToast("Skydrive Connected!");
                                 oneDrive.setSession(session);
                                 oneDrive.setConnectClient(new LiveConnectClient(session));
                                 oneDrive.createStorageDirectories();


                              }
                           else
                              {
                                 showToast("Initialize did not connect. Please try login in.");

                                 oneDrive.mAuthClient.login(BaseActivity.this, Arrays.asList(oneDrive.SCOPES), new LiveAuthListener()
                                 {

                                    @Override
                                    public void onAuthComplete(LiveStatus status, LiveConnectSession session, Object userState)
                                       {
                                          if (status == LiveStatus.CONNECTED)
                                             {
                                                showToast("Skydrive Connected!");
                                                oneDrive.setSession(session);
                                                oneDrive.setConnectClient(new LiveConnectClient(session));

                                             }
                                          else
                                             {
                                                showToast("Login did not connect. Status is " + status + ".");
                                             }
                                       }


                                    @Override
                                    public void onAuthError(LiveAuthException exception, Object userState)
                                       {
                                          showToast(exception.getMessage());
                                       }
                                 });
                              }
                        }
                  });
               }
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

            app = (POSNApplication) getApplication();
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

      private void showToast(String message)
         {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
         }
   }
