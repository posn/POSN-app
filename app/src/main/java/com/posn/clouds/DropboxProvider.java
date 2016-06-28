package com.posn.clouds;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AppKeyPair;
import com.posn.constants.Constants;
import com.posn.utility.UserInterfaceHelper;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class implements the Dropbox cloud functionality and uses the Cloud Provider interface
 **/
public class DropboxProvider implements CloudProvider
   {
      private Context context;
      private OnConnectedCloudListener connectedListener;
      private boolean isConnected = false;

      // variable declarations
      public DropboxAPI<AndroidAuthSession> dropboxSession;

      public DropboxProvider(Context context, OnConnectedCloudListener connectedListener)
         {
            this.context = context;
            this.connectedListener = connectedListener;
         }

      /**
       * This method connects the application to the user's dropbox account
       * The user will be prompted to log into their account and accept the permissions.
       * Note: the access token is saved on the device, so the user only needs to log in once
       **/
      @Override
      public void initializeCloud()
         {
            // create the application key token
            AppKeyPair appKeyToken = new AppKeyPair(Constants.DROPBOX_APP_KEY, Constants.DROPBOX_APP_SECRET);

            // try to get the session token from the shared preferences
            String sessionToken = getDropboxToken();

            // check if the session token was retrieved
            if (sessionToken == null)
               {
                  // if no token, then have the user sign in
                  dropboxSession = new DropboxAPI<>(new AndroidAuthSession(appKeyToken));
                  dropboxSession.getSession().startOAuth2Authentication(context);
               }
            else
               {
                  // session token already available
                  dropboxSession = new DropboxAPI<>(new AndroidAuthSession(appKeyToken, sessionToken));
                  isConnected = true;

                  // call the on connected listener method
                  connectedListener.OnConnected();
               }
         }

      /**
       * This method is called after the user logs in using the Dropbox Authentication activity (provided by Dropbox)
       **/
      @Override
      public void onResume()
         {
            if (!isConnected)
               {
                  authenticateDropboxLogin();
               }
         }

      /**
       * This method is to finish the authenticate process and get the token to be saved to the device
       **/
      private void authenticateDropboxLogin()
         {
            // check if the dropbox session has been created
            if (dropboxSession != null)
               {
                  // check if the authentication was successful
                  if (dropboxSession.getSession().authenticationSuccessful())
                     {
                        // finish the authentication
                        dropboxSession.getSession().finishAuthentication();

                        // save the Dropbox token to the device
                        saveDropboxToken(dropboxSession.getSession().getOAuth2AccessToken());

                        // show a connection toast
                        UserInterfaceHelper.showToast(context, "Dropbox Connected!");

                        // call the on connected listener method
                        connectedListener.OnConnected();
                        isConnected = true;
                     }
               }
         }

      /**
       * This method creates the cloud storage directories in a asynctask (can be called from the main UI thread)
       **/
      @Override
      public void createStorageDirectoriesOnCloudAsyncTask()
         {
            new AsyncTask<Void, Void, Void>()
               {
                  protected Void doInBackground(Void... params)
                     {
                        createStorageDirectoriesOnCloud();
                        return null;
                     }
               }.execute();
         }

      /**
       * This method downloads a file from the user's cloud in a asynctask (can be called from the main UI thread)
       **/
      @Override
      public void downloadFileFromCloudAsyncTask(final String folderName, final String fileName, final String devicePath)
         {
            new AsyncTask<Void, Void, Void>()
               {
                  protected Void doInBackground(Void... params)
                     {
                        downloadFileFromCloud(folderName, fileName, devicePath);
                        return null;
                     }
               }.execute();
         }

      /**
       * This method uploads a file to the user's cloud in a asynctask (can be called from the main UI thread)
       **/
      @Override
      public void uploadFileToCloudAsyncTask(final String folderName, final String fileName, final String devicePath)
         {
            new AsyncTask<Void, Void, Void>()
               {
                  protected Void doInBackground(Void... params)
                     {
                        uploadFileToCloud(folderName, fileName, devicePath);
                        return null;
                     }
               }.execute();
         }


      /**
       * This method is not required for Dropbox
       **/
      @Override
      public void activityResult(int requestCode, int resultCode, Intent data)
         {

         }

      /**
       * This method creates the cloud storage directories (can not be called from the main UI thread)
       **/
      @Override
      public void createStorageDirectoriesOnCloud()
         {
            // loop through the list of directories (Refer to Constants.java)
            for (int i = 0; i < Constants.NUM_DIRECTORIES; i++)
               {
                  // check if a direct exists, if it does not then exception will be thrown
                  try
                     {
                        // attempt to create dropbox folder
                        dropboxSession.createFolder(Constants.directoryNames[i]);
                     }
                  catch (DropboxException e)
                     {
                        // folder already exists, so ignore
                     }
               }
         }

      /**
       * This method downloads a file from the user's cloud (can not be called from the main UI thread)
       **/
      @Override
      public void downloadFileFromCloud(String folderName, String fileName, String devicePath)
         {
            // declare variables
            FileOutputStream outputStream;

            String dropboxPath = "/" + folderName + "/" + fileName;

            // get the location of the file to be downloaded
            java.io.File f = new java.io.File(devicePath);

            try
               {
                  // create a stream to put the file onto the device
                  outputStream = new FileOutputStream(f);

                  // download the file from dropbox
                  DropboxAPI.DropboxFileInfo info = dropboxSession.getFile(dropboxPath, null, outputStream, null);

                  // check if the download was successful
                  if (info != null)
                     {
                        Log.i("Dropbox Download", "Download Complete");
                     }
                  else
                     {
                        Log.i("Dropbox Download", "Download Failed");
                     }

                  // close the stream
                  outputStream.close();
               }
            catch (Exception e)
               {
                  e.printStackTrace();
                  Log.i("Dropbox Download", "Exception");
               }

         }

      /**
       * This method uploads a file to the user's cloud and returns the direct download link (can not be called from the main UI thread)
       * Overwrites an existing file with the same name
       **/
      @Override
      public String uploadFileToCloud(String folderName, String fileName, String devicePath)
         {
            // declare variables
            DropboxAPI.Entry response;
            FileInputStream inputStream;

            String dropboxPath = "/" + folderName + "/" + fileName;
            String directLink = null;

            // get the location of the file to be uploaded
            java.io.File file = new java.io.File(devicePath);

            try
               {
                  // get the file into memory
                  inputStream = new FileInputStream(file);

                  // upload the file to dropbox
                  response = dropboxSession.putFileOverwrite(dropboxPath, inputStream, file.length(), null);

                  // check if the upload was successful
                  if (response != null)
                     {
                        // share the file to the public
                        DropboxAPI.DropboxLink link = dropboxSession.share(dropboxPath);

                        // returned link is a short-link, so get long link
                        // create a new URL
                        URL url = new URL(link.url);

                        // open the connection to redirect
                        HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
                        ucon.setInstanceFollowRedirects(false);

                        // get the long link string in the Location part of the header
                        String test = ucon.getHeaderField("Location");

                        // replace end 0 with a 1 to make it become a direct link
                        directLink = test.substring(0, test.length() - 1) + "1";


                        Log.i("Dropbox Upload", "Upload Complete");
                     }
                  else
                     {
                        Log.i("Dropbox Upload", "Upload Failed");
                     }

                  // close the stream
                  inputStream.close();
               }
            catch (IOException | DropboxException e)
               {
                  e.printStackTrace();
                  Log.i("Dropbox Upload", "IO Exception");
               }


            return directLink;
         }

      /**
       * This method removes a file from the user's cloud (can not be called from the main UI thread)
       **/
      @Override
      public void removeFileOnCloud(String folderName, String fileName)
         {
            String dropboxPath = "/" + folderName + "/" + fileName;

            try
               {
                  dropboxSession.delete(dropboxPath);
               }
            catch (DropboxException e)
               {
                  e.printStackTrace();
               }
         }

      /**
       * This method fetches a previously created Dropbox authentication token from the device's shared preferences
       * If token is not there, then it returns null
       **/
      private String getDropboxToken()
         {
            // get the shared preferences area for the token
            SharedPreferences sessionTokenRecord = context.getSharedPreferences("token", Context.MODE_PRIVATE);

            // get the token key
            String sessionToken = sessionTokenRecord.getString("accessToken", null);

            // check if the token key and secret were retrieved successfully
            if (!(sessionToken == null))
               {
                  // return the token
                  return sessionToken;
               }

            // return null since it was not found
            return null;
         }

      /**
       * This method saves the Dropbox authentication token to the device's shared preferences
       **/
      private void saveDropboxToken(String accessToken)
         {
            // create a new shared preferences area for the token
            SharedPreferences.Editor tokenRecordEditor = context.getSharedPreferences("token", Context.MODE_PRIVATE).edit();

            // put the access token in the token object
            tokenRecordEditor.putString("accessToken", accessToken);

            // write the token object to the device
            tokenRecordEditor.apply();
         }
   }
