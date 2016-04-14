package com.posn.clouds.GoogleDrive;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
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
import com.posn.Constants;
import com.posn.clouds.CloudProvider;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class GoogleDriveClientUsage extends CloudProvider implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener
   {
      private static final String TAG = "Google Drive Client";

      protected static final int REQUEST_CODE_RESOLUTION = 1;
      protected static final int NEXT_AVAILABLE_REQUEST_CODE = 2;

      private Context context;
      public GoogleApiClient mGoogleApiClient;

      public HashMap<String, DriveId> folderIds;


      public GoogleDriveClientUsage(Context context)
         {
            // set the activity context
            this.context = context;
            folderIds = new HashMap<>();
         }

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


      @Override
      public void downloadFileFromCloud(String folderName, String fileName, String outputPath)
         {
            // get the folder where the file is located
            DriveFolder folder = Drive.DriveApi.getFolder(mGoogleApiClient, folderIds.get(folderName));

            // check if the file exists
            Query query = new Query.Builder()
                              .addFilter(Filters.contains(SearchableField.TITLE, fileName))
                              .build();

            // execute query and wait for result
            DriveApi.MetadataBufferResult metaData = folder.queryChildren(mGoogleApiClient, query).await();
            DriveApi.DriveContentsResult driveContentsResult;

            // if the query is not empty then download the file
            if (metaData.getMetadataBuffer().getCount() != 0)
               {
                  // get the file from Drive
                  DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, metaData.getMetadataBuffer().get(0).getDriveId());

                  // set the file to be read
                  driveContentsResult = file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null).await();

                  try
                     {
                        // get all the Drive file data contents
                        DriveContents driveContents = driveContentsResult.getDriveContents();

                        InputStream input = driveContents.getInputStream();

                        // open a new file on device
                        OutputStream output = new BufferedOutputStream(new FileOutputStream(new File(outputPath)));
                        int bufferSize = 1024;
                        byte[] buffer = new byte[bufferSize];

                        // read all the Drive file data into a buffer
                        int len = 0;
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

                        System.out.println("Downloaded file: " + (driveContentsResult.getStatus().isSuccess() ? "True" : "False"));
                     }
                  catch (IOException e)
                     {
                        e.printStackTrace();
                     }
               }

            // release the Metadata
            metaData.release();
         }

      @Override
      public String uploadFileToCloud(String folderName, String fileName, String devicePath)
         {
            String MIME_Type = getMimeType(fileName);
            String directLink = null;

            // get the folder where the file is located
            DriveFolder folder = Drive.DriveApi.getFolder(mGoogleApiClient, folderIds.get(folderName));

            Query query = new Query.Builder()
                              .addFilter(Filters.contains(SearchableField.TITLE, fileName))
                              .build();

            // execute query and wait for result
            DriveApi.MetadataBufferResult metaData = folder.queryChildren(mGoogleApiClient, query).await();

            // check if the file exists
            DriveApi.DriveContentsResult driveContentsResult;

            // if the query is empty then create a subfolder
            if (metaData.getMetadataBuffer().getCount() == 0)
               {
                  // if the file does not exist, then create a new file
                  driveContentsResult = Drive.DriveApi.newDriveContents(mGoogleApiClient).await();
               }
            else
               {
                  // if the file exists, then get the file
                  DriveFile file = Drive.DriveApi.getFile(mGoogleApiClient, metaData.getMetadataBuffer().get(0).getDriveId());

                  // get the direct link for the file
                  DriveResource.MetadataResult meta = file.getMetadata(mGoogleApiClient).await();
                  directLink = meta.getMetadata().getWebContentLink();

                  // open the file and edit the contents
                  driveContentsResult = file.open(mGoogleApiClient, DriveFile.MODE_WRITE_ONLY, null).await();
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
                  input.read(bytes, 0, bytes.length);

                  // write data to Drive file
                  outputStream.write(bytes);

                  // close streams
                  outputStream.close();
                  input.close();
               }
            catch (IOException e)
               {
                  e.printStackTrace();
               }

            // if the file is a new file, upload as a new file
            if (metaData.getMetadataBuffer().getCount() == 0)
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
                  com.google.android.gms.common.api.Status status = driveContents.commit(mGoogleApiClient, null).await();
                  //System.out.println("Updated file: " + (status.getStatus().isSuccess() ? "True" : "False"));
               }

            //System.out.println("Google LINK: " + directLink);

            // release the metadata
            metaData.release();

            return directLink;
         }

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

            System.out.println("Starting FOLDERS!!");

            //  googleDrive.createStorageDirectories();
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
                  GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), (Activity) context, 0).show();
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

      @Override
      public void activityResult(int requestCode, int resultCode, Intent data)
         {
            if (requestCode == REQUEST_CODE_RESOLUTION && resultCode == Activity.RESULT_OK)
               {
                  mGoogleApiClient.connect();
                  showToast("Google Drive Connected!");
               }
         }

      @Override
      public void onResume()
         {
         }

      private void showToast(String message)
         {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
         }

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


      private void createSubFolder(DriveId mainFolder, String subFolder)
         {
            // set the folder to check as the parent
            DriveFolder folder = Drive.DriveApi.getFolder(mGoogleApiClient, mainFolder);

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
   }
