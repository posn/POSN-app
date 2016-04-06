package com.posn.clouds.GoogleDrive;

import android.os.AsyncTask;

import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;

import java.util.HashMap;


public class GoogleDriveCreateDirectoriesAsyncTask extends AsyncTask<Boolean, Void, Void>
   {
      // declare variables
      GoogleDriveClientUsage googleDrive;
      public HashMap<String, DriveId> folderIds;


      public GoogleDriveCreateDirectoriesAsyncTask(GoogleDriveClientUsage googleDrive)
         {
            this.googleDrive = googleDrive;
            folderIds = googleDrive.folderIds;
         }


      @Override
      protected Void doInBackground(Boolean... arg0)
         {
            // create the parent folder
            createMainFolder("POSN");

            DriveId parentFolder = folderIds.get("POSN");

            // create the sub folders
            createSubFolder(parentFolder, "archive");
            createSubFolder(parentFolder, "keys");
            createSubFolder(parentFolder, "multimedia");
            createSubFolder(parentFolder, "profile");
            createSubFolder(parentFolder, "wall");

            return null;
         }


      protected void onPostExecute(Void result)
         {
           // googleDrive.uploadFile("multimedia", "testA.jpg", "image/jpeg", Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/multimedia" + "/test.jpg");
            // googleDrive.uploadFile("multimedia", "test2.txt", "text/plain", Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/wall" + "/user_notifications.txt");
            //googleDrive.downloadFile("multimedia", "test2.txt", Environment.getExternalStorageDirectory() + "/Android/data/com.posn/data/wall" + "/test2.txt");
         }


      private void createMainFolder(String folderName)
         {
            // create a query to check if a folder already exists
            Query query = new Query.Builder()
                              .addFilter(Filters.contains(SearchableField.TITLE, folderName))
                              .build();

            // execute query and wait for result
            DriveApi.MetadataBufferResult metaData = Drive.DriveApi.query(googleDrive.mGoogleApiClient, query).await();

            // if the query is empty then create a folder
            if (metaData.getMetadataBuffer().getCount() == 0)
               {
                  // if the folder does not exist, then create a new folder
                  MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle(folderName).build();
                  DriveFolder.DriveFolderResult result = Drive.DriveApi.getRootFolder(googleDrive.mGoogleApiClient).createFolder(googleDrive.mGoogleApiClient, changeSet).await();
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
            DriveFolder folder = Drive.DriveApi.getFolder(googleDrive.mGoogleApiClient, mainFolder);

            // check if the sub folder is in the main folder
            Query query = new Query.Builder()
                              .addFilter(Filters.contains(SearchableField.TITLE, subFolder))
                              .build();

            // execute query and wait for result
            DriveApi.MetadataBufferResult metaData = folder.queryChildren(googleDrive.mGoogleApiClient, query).await();

            // if the query is empty then create a subfolder
            if (metaData.getMetadataBuffer().getCount() == 0)
               {
                  // if the sub folder does not exist, then create a new sub folder
                  MetadataChangeSet changeSet = new MetadataChangeSet.Builder().setTitle(subFolder).build();
                  DriveFolder.DriveFolderResult result = folder.createFolder(googleDrive.mGoogleApiClient, changeSet).await();
                  googleDrive.folderIds.put(subFolder, result.getDriveFolder().getDriveId());
               }
            else
               {
                  // store the sub folder ID
                  googleDrive.folderIds.put(subFolder, metaData.getMetadataBuffer().get(0).getDriveId());
               }

            // release metadata
            metaData.release();
         }
   }



