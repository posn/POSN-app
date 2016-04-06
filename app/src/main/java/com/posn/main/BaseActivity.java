package com.posn.main;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.posn.application.POSNApplication;
import com.posn.clouds.CloudProvider;


public abstract class BaseActivity extends FragmentActivity
   {
      private static final String TAG = "BaseDriveActivity";

      protected static final int REQUEST_CODE_RESOLUTION = 1;
      protected static final int NEXT_AVAILABLE_REQUEST_CODE = 2;

      public CloudProvider cloud;

      //  private GoogleDriveClientUsage googleDrive = null;

    //  private OneDriveClientUsage oneDrive = null;

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
                                                     .addConnectionCallbacks(this)
                                                     .addOnConnectionFailedListener(this)
                                                     .build();
               }
            googleDrive.mGoogleApiClient.connect();
*/
/*
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
               }*/
         }


      @Override
      protected void onActivityResult(int requestCode, int resultCode, Intent data)
         {
            super.onActivityResult(requestCode, resultCode, data);

            cloud.activityResult(requestCode, resultCode, data);
         }


   }
