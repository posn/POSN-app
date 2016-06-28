package com.posn.clouds;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.posn.constants.Constants;
import com.posn.utility.UserInterfaceHelper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

/**
 * This class implements the Google Drive cloud functionality and uses the Cloud Provider interface
 **/
public class GoogleDriveProvider implements CloudProvider, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
   {
      private static final String TAG = "Google Drive Client";

      protected static final int REQUEST_CODE_RESOLUTION = 1;

      private Context context;
      public GoogleApiClient mGoogleApiClient;

      // hash map to hold the folder IDs of the storage directories
      public HashMap<String, DriveId> folderIds;

      private OnConnectedCloudListener connectedListener;


      public GoogleDriveProvider(Context context, OnConnectedCloudListener connectedListener)
         {
            // set the activity context
            this.context = context;
            folderIds = new HashMap<>();
            this.connectedListener = connectedListener;
         }

      /**
       * This method connects the application to the user's google drive account
       * The user will be prompted to log into their account and accept the permissions.
       * Note: the user only needs to log in once
       **/
      @Override
      public void initializeCloud()
         {
            mGoogleApiClient = new GoogleApiClient.Builder(context)
                                   .addApi(Drive.API)
                                   .addScope(Drive.SCOPE_FILE)
                                   .addConnectionCallbacks(this)
                                   .addOnConnectionFailedListener(this)
                                   .build();
            mGoogleApiClient.connect();
         }


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
       * This method creates the cloud storage directories in a asynctask (can be called from the main UI thread)
       * The folder IDs are fetched and placed into the hash map
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
       * This method downloads a file from the user's cloud (can not be called from the main UI thread)
       **/
      @Override
      public void downloadFileFromCloud(String folderName, String fileName, String outputPath)
         {
            // get the folder where the file is located
            DriveFolder folder = folderIds.get(folderName).asDriveFolder();

            DriveFile file = fetchDriveFile(folder, fileName);

            // if the query is not empty then download the file
            if (file != null)
               {
                  // set the file to be read
                  DriveApi.DriveContentsResult driveContentsResult = file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null).await();

                  try
                     {
                        // get all the Drive file data contents
                        DriveContents driveContents = driveContentsResult.getDriveContents();

                        InputStream input = driveContents.getInputStream();

                        // open a new file on device
                        OutputStream output = new BufferedOutputStream(new FileOutputStream(new File(outputPath)));
                        byte[] buffer = new byte[1024];

                        // read all the Drive file data into a buffer
                        int len;
                        while ((len = input.read(buffer)) != -1)
                           {
                              // write data to the new file
                              output.write(buffer, 0, len);
                           }

                        // close streams
                        input.close();
                        output.close();

                        // discard file changes
                        driveContents.discard(mGoogleApiClient);
                     }
                  catch (IOException e)
                     {
                        e.printStackTrace();
                     }
               }
         }


      /**
       * This method uploads a file to the user's cloud and returns the direct download link (can not be called from the main UI thread)
       * Overwrites an existing file with the same name
       **/
      @Override
      public String uploadFileToCloud(String folderName, String fileName, String devicePath)
         {
            String MIME_Type = getMimeType(fileName);
            String directLink = null;
            DriveApi.DriveContentsResult driveContentsResult;

            // get the folder where the file is located
            DriveFolder folder = folderIds.get(folderName).asDriveFolder();

            DriveFile file = fetchDriveFile(folder, fileName);

            // if the file already exists
            if (file != null)
               {
                  // get the direct link for the file
                  DriveResource.MetadataResult meta = file.getMetadata(mGoogleApiClient).await();
                  directLink = meta.getMetadata().getWebContentLink();

                  // open the file and edit the contents
                  driveContentsResult = file.open(mGoogleApiClient, DriveFile.MODE_WRITE_ONLY, null).await();
               }
            else
               {
                  // if the file does not exist, then create a new file
                  driveContentsResult = Drive.DriveApi.newDriveContents(mGoogleApiClient).await();
               }

            // get the content from file
            DriveContents driveContents = driveContentsResult.getDriveContents();

            OutputStream outputStream = driveContents.getOutputStream();

            // open the file and get the file size
            File myFile = new File(devicePath);
            int size = (int) myFile.length();
            byte[] bytes = new byte[size];

            // write all of the file contents to the Drive file
            try
               {
                  // read all the file data in
                  BufferedInputStream input = new BufferedInputStream(new FileInputStream(myFile));
                  int numBytesRead = input.read(bytes, 0, bytes.length);

                  if (numBytesRead >= 0)
                     {
                        // write data to Drive file
                        outputStream.write(bytes);
                     }

                  // close streams
                  outputStream.close();
                  input.close();
               }
            catch (IOException e)
               {
                  e.printStackTrace();
               }

            // if the file is a new file, upload as a new file
            if (file == null)
               {
                  // set metadata
                  MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                                    .setTitle(fileName)
                                                    .setMimeType(MIME_Type)
                                                    .build();

                  // create the file in Drive
                  DriveFolder.DriveFileResult finalResult = folder.createFile(mGoogleApiClient, changeSet, driveContents).await();

                  // get the direct download link for the file
                  // Need to do in a while loop with sleep because Google Drive has a delay for the file to register after the upload
                  while (directLink == null)
                     {
                        try
                           {
                              Thread.sleep(2000, 0);
                           }
                        catch (InterruptedException e)
                           {
                              System.out.println(e.getMessage());
                           }

                        // get the file meta data to acquire the link
                        DriveResource.MetadataResult meta = finalResult.getDriveFile().getMetadata(mGoogleApiClient).await();
                        directLink = meta.getMetadata().getWebContentLink();
                     }
               }
            else
               {
                  // update the Drive file with new contents
                  driveContents.commit(mGoogleApiClient, null).await();
               }

            return directLink;
         }


      /**
       * This method removes a file from the user's cloud (can not be called from the main UI thread)
       **/
      @Override
      public void removeFileOnCloud(String folderName, String fileName)
         {
            DriveFolder folder = folderIds.get(folderName).asDriveFolder();

            DriveFile driveFile = fetchDriveFile(folder, fileName);

            // Call to delete file.
            driveFile.delete(mGoogleApiClient).await();
         }


      /**
       * This method creates the cloud storage directories (can not be called from the main UI thread)
       **/
      @Override
      public void createStorageDirectoriesOnCloud()
         {
            // create the parent folder
            createMainFolder("POSN");

            DriveId parentFolder = folderIds.get("POSN");

            // create the sub folders
            for (int i = 0; i < Constants.NUM_DIRECTORIES; i++)
               {
                  createSubFolder(parentFolder, Constants.directoryNames[i]);
               }
         }

      /**
       * Called when {@code mGoogleApiClient} is connected.
       */
      @Override
      public void onConnected(Bundle connectionHint)
         {
            Log.i(TAG, "GoogleApiClient connected");

            // call the on connected listener method
            connectedListener.OnConnected();
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
      public void onConnectionFailed(@NonNull ConnectionResult result)
         {
            Log.i(TAG, "GoogleApiClient connection failed: " + result.toString());
            if (!result.hasResolution())
               {
                  // show the localized error dialog.
                  GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
                  googleAPI.getErrorDialog((Activity) context, result.getErrorCode(), 0).show();
                  return;
               }
            try
               {
                  result.startResolutionForResult((Activity) context, REQUEST_CODE_RESOLUTION);
               }
            catch (IntentSender.SendIntentException e)
               {
                  Log.e(TAG, "Exception while starting resolution activity", e);
               }
         }

      /**
       * This method is called when there was an issue connecting and requires the user to retry
       */
      @Override
      public void activityResult(int requestCode, int resultCode, Intent data)
         {
            if (requestCode == REQUEST_CODE_RESOLUTION && resultCode == Activity.RESULT_OK)
               {
                  mGoogleApiClient.connect();
                  UserInterfaceHelper.showToast(context, "Google Drive Connected!");
               }
         }


      /**
       * This method is not required for Google Drive
       **/
      @Override
      public void onResume()
         {
         }


      /**
       * This method gets the file's media type from a given URL
       **/
      private String getMimeType(String url)
         {
            String type = null;
            String extension = MimeTypeMap.getFileExtensionFromUrl(url);
            if (extension != null)
               {
                  type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
               }
            return type;
         }

      /**
       * This method creates the root folder for the application on the cloud (can not be called from the main UI thread)
       **/
      private void createMainFolder(String folderName)
         {
            // create a query to check if a folder already exists
            Query query = new Query.Builder()
                              .addFilter(Filters.contains(SearchableField.TITLE, folderName))
                              .build();

            // execute query and wait for result
            DriveApi.MetadataBufferResult metaData = Drive.DriveApi.query(mGoogleApiClient, query).await();

            // if the query is empty then create a folder
            if (metaData.getMetadataBuffer().getCount() == 0)
               {
                  // if the folder does not exist, then create a new folder
                  MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle(folderName).build();
                  DriveFolder.DriveFolderResult result = Drive.DriveApi.getRootFolder(mGoogleApiClient).createFolder(mGoogleApiClient, changeSet).await();
                  folderIds.put(folderName, result.getDriveFolder().getDriveId());
               }
            else
               {
                  // store the folder ID
                  folderIds.put(folderName, metaData.getMetadataBuffer().get(0).getDriveId());
               }

            // release metadata
            metaData.release();
         }


      /**
       * This method creates new directories within the root folder on the cloud (can not be called from the main UI thread)
       **/
      private void createSubFolder(DriveId mainFolder, String subFolder)
         {
            // set the folder to check as the parent
            // DriveFolder folder = Drive.DriveApi.getFolder(mGoogleApiClient, mainFolder);
            DriveFolder folder = mainFolder.asDriveFolder();

            // check if the sub folder is in the main folder
            Query query = new Query.Builder()
                              .addFilter(Filters.contains(SearchableField.TITLE, subFolder))
                              .build();

            // execute query and wait for result
            DriveApi.MetadataBufferResult metaData = folder.queryChildren(mGoogleApiClient, query).await();

            // if the query is empty then create a subfolder
            if (metaData.getMetadataBuffer().getCount() == 0)
               {
                  // if the sub folder does not exist, then create a new sub folder
                  MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle(subFolder).build();
                  DriveFolder.DriveFolderResult result = folder.createFolder(mGoogleApiClient, changeSet).await();
                  folderIds.put(subFolder, result.getDriveFolder().getDriveId());
               }
            else
               {
                  // store the sub folder ID
                  folderIds.put(subFolder, metaData.getMetadataBuffer().get(0).getDriveId());
               }

            // release metadata
            metaData.release();
         }


      /**
       * This method gets the DriveFile object from a given folder and file name
       **/
      private DriveFile fetchDriveFile(DriveFolder folder, String fileName)
         {
            DriveFile file = null;

            // check if the file exists
            Query query = new Query.Builder()
                              .addFilter(Filters.contains(SearchableField.TITLE, fileName))
                              .build();

            // execute query and wait for result
            DriveApi.MetadataBufferResult metaData = folder.queryChildren(mGoogleApiClient, query).await();

            // if the query is not empty then download the file
            if (metaData.getMetadataBuffer().getCount() != 0)
               {
                  file = metaData.getMetadataBuffer().get(0).getDriveId().asDriveFile();
               }

            // release the Metadata
            metaData.release();

            return file;
         }
   }
